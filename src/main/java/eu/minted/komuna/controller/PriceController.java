package eu.minted.komuna.controller;

import eu.minted.komuna.model.*;
import eu.minted.komuna.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/prices")
public class PriceController {

    @Autowired private PriceService priceService;
    @Autowired private FeeService feeService;
    @Autowired private CommunityService communityService;
    @Autowired private UserService userService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(priceService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Nepavyko gauti kainų: " + e.getMessage()));
        }
    }

    @PostMapping
    @ResponseBody
    public Map<String, String> createPrice(@RequestBody Map<String, Object> payload) {
        try {
            if (payload.get("feeId") == null || payload.get("communityId") == null)
                return Map.of("error", "Reikalingi ir paslaugos, ir bendrijos ID.");

            Long feeId = Long.valueOf(payload.get("feeId").toString());
            Long communityId = Long.valueOf(payload.get("communityId").toString());
            Double amount = Double.valueOf(payload.get("amount").toString());
            LocalDate validFrom = LocalDate.parse(payload.get("validFrom").toString());
            LocalDate validTo = payload.get("validTo") != null && !payload.get("validTo").toString().isBlank()
                    ? LocalDate.parse(payload.get("validTo").toString())
                    : null;

            Price price = new Price();
            price.setFee(feeService.findById(feeId).orElseThrow(() -> new IllegalArgumentException("Paslauga nerasta.")));
            price.setCommunity(communityService.findById(communityId).orElseThrow(() -> new IllegalArgumentException("Bendrija nerasta.")));
            price.setAmount(amount);
            price.setValidFrom(validFrom);
            price.setValidTo(validTo);

            priceService.save(price);
            return Map.of("success", "Kaina sėkmingai sukurta.");
        } catch (IllegalArgumentException e) {
            return Map.of("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Klaida kuriant kainą: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public String updatePrice(@PathVariable Long id, @RequestBody Map<String, Object> payload, Authentication auth) {
        if (auth == null || auth.getName() == null)
            return "{\"error\":\"Naudotojo autentifikacija negalima.\"}";

        Optional<Price> existingOpt = priceService.findById(id);
        if (existingOpt.isEmpty()) return "{\"error\":\"Kaina nerasta.\"}";

        try {
            Price price = existingOpt.get();
            User user = userService.findByEmail(auth.getName()).orElseThrow();

            if (user.getRole().getName().equals("MANAGER") &&
                    (user.getCommunity() == null || !user.getCommunity().getId().equals(price.getCommunity().getId()))) {
                return "{\"error\":\"Negalite redaguoti kitų bendrijų kainų.\"}";
            }

            if (payload.containsKey("amount")) price.setAmount(Double.valueOf(payload.get("amount").toString()));
            if (payload.containsKey("validFrom") && payload.get("validFrom") != null)
                price.setValidFrom(LocalDate.parse(payload.get("validFrom").toString()));
            if (payload.containsKey("validTo")) {
                String to = (String) payload.get("validTo");
                price.setValidTo(to != null && !to.isBlank() ? LocalDate.parse(to) : null);
            }

            priceService.save(price);
            return "{\"success\":\"Kaina atnaujinta sėkmingai.\"}";
        } catch (Exception e) {
            return "{\"error\":\"Klaida atnaujinant kainą: " + e.getMessage() + "\"}";
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deletePrice(@PathVariable Long id, Authentication auth) {
        if (auth == null || auth.getName() == null)
            return "{\"error\":\"Naudotojo autentifikacija negalima.\"}";

        try {
            Price price = priceService.findById(id).orElseThrow(() -> new IllegalArgumentException("Kaina nerasta."));
            User user = userService.findByEmail(auth.getName()).orElseThrow();

            if (user.getRole().getName().equals("MANAGER") &&
                    (user.getCommunity() == null || !user.getCommunity().getId().equals(price.getCommunity().getId()))) {
                return "{\"error\":\"Negalite trinti kitų bendrijų kainų.\"}";
            }

            priceService.deleteById(id);
            return "{\"success\":\"Kaina pašalinta.\"}";
        } catch (IllegalArgumentException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        } catch (Exception e) {
            return "{\"error\":\"Klaida šalinant kainą: " + e.getMessage() + "\"}";
        }
    }
}

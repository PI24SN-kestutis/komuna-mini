package eu.minted.komuna.controller;

import eu.minted.komuna.model.Fee;
import eu.minted.komuna.model.User;
import eu.minted.komuna.service.CommunityService;
import eu.minted.komuna.service.FeeService;
import eu.minted.komuna.service.PriceService;
import eu.minted.komuna.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/fees")
public class FeeController {

    private final FeeService feeService;
    private final UserService userService;
    private final CommunityService communityService;
    private final PriceService priceService;

    public FeeController(FeeService feeService, UserService userService,
                         CommunityService communityService, PriceService priceService) {
        this.feeService = feeService;
        this.userService = userService;
        this.communityService = communityService;
        this.priceService = priceService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(feeService.findAll());
    }


    @GetMapping("/{id}")
    public Optional<Fee> getById(@PathVariable Long id) {
        return feeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createFee(@RequestBody Fee fee, Authentication auth) {
        try {
            if (fee.getName() == null || fee.getName().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Paslaugos pavadinimas privalomas."));
            }
            if (fee.getUnit() == null || fee.getUnit().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Matavimo vienetas privalomas."));
            }
            if (fee.getDescription() == null || fee.getDescription().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aprašymas privalomas."));
            }

            if (auth == null || auth.getName() == null)
                return ResponseEntity.status(403).body(Map.of("error", "Naudotojas neautentifikuotas."));

            User user = userService.findByEmail(auth.getName()).orElseThrow();
            boolean isManager = user.getRole().getName().equals("MANAGER");
            boolean isAdmin = user.getRole().getName().equals("ADMIN");

            if (isManager) {
                if (user.getCommunity() == null)
                    return ResponseEntity.badRequest().body(Map.of("error", "Vadybininkas neturi priskirtos bendrijos."));
                fee.setCommunity(user.getCommunity());
            } else if (isAdmin) {
                fee.setCommunity(null);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Būtina priskirti bendriją."));
            }

            Fee saved = feeService.save(fee);
            return ResponseEntity.ok(Map.of("success", "Paslauga sukurta sėkmingai (ID: " + saved.getId() + ")."));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Duomenų bazės klaida: " + e.getMostSpecificCause().getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Klaida kuriant paslaugą: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateFee(@PathVariable Long id,
                                                         @RequestBody Fee updatedFee,
                                                         Authentication auth) {
        try {
            if (auth == null || auth.getName() == null)
                return ResponseEntity.status(403).body(Map.of("error", "Naudotojas neautentifikuotas."));

            User user = userService.findByEmail(auth.getName()).orElseThrow();
            Optional<Fee> existingOpt = feeService.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Paslauga nerasta."));
            }

            Fee fee = existingOpt.get();

            if (user.getRole().getName().equals("MANAGER")) {
                if (user.getCommunity() == null ||
                        !user.getCommunity().getId().equals(fee.getCommunity().getId())) {
                    return ResponseEntity.status(403).body(Map.of("error", "Negalite redaguoti kitų bendrijų paslaugų."));
                }
            }

            fee.setName(updatedFee.getName());
            fee.setUnit(updatedFee.getUnit());
            fee.setDescription(updatedFee.getDescription());
            feeService.save(fee);

            return ResponseEntity.ok(Map.of("success", "Paslauga atnaujinta sėkmingai."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Klaida atnaujinant paslaugą: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFee(@PathVariable Long id, Authentication auth) {
        try {
            if (auth == null || auth.getName() == null)
                return ResponseEntity.status(403).body(Map.of("error", "Naudotojas neautentifikuotas."));

            User user = userService.findByEmail(auth.getName()).orElseThrow();
            Optional<Fee> existingOpt = feeService.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Paslauga nerasta."));
            }

            Fee fee = existingOpt.get();


            if (user.getRole().getName().equals("MANAGER")) {
                if (user.getCommunity() == null ||
                        !user.getCommunity().getId().equals(fee.getCommunity().getId())) {
                    return ResponseEntity.status(403).body(Map.of("error", "Negalite trinti kitų bendrijų paslaugų."));
                }
            }

            feeService.deleteById(id);
            return ResponseEntity.ok(Map.of("success", "Paslauga pašalinta sėkmingai."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Klaida šalinant paslaugą: " + e.getMessage()));
        }
    }
}

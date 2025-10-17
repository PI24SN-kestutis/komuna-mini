package eu.minted.komuna.controller;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.model.Price;
import eu.minted.komuna.model.User;
import eu.minted.komuna.service.CommunityService;
import eu.minted.komuna.service.FeeService;
import eu.minted.komuna.service.PriceService;
import eu.minted.komuna.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/fees")
public class FeeController {

    private final FeeService feeService;
    private final UserService userService;
    private final CommunityService communityService;
    private final PriceService priceService;

    public FeeController(FeeService feeService, UserService userService, CommunityService communityService, PriceService priceService) {
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

    // ===========================
    // CREATE FEE
    // ===========================
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, String>> createFee(@RequestBody Fee fee) {
        try {
            if (fee.getName() == null || fee.getName().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Paslaugos pavadinimas privalomas."));
            }


            if (fee.getUnit() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Paslaugos žymėjimas būtinas privalomas."));
            }
            if (fee.getDescription() == null){
                return ResponseEntity.badRequest().body(Map.of("error", "Paslaugos aprašymas būtinas privalomas."));
            }

            Fee saved = feeService.save(fee);

            return ResponseEntity.ok(Map.of("success", "Paslauga sukurta sėkmingai (ID: " + saved.getId() + ")."));

        } catch (DataIntegrityViolationException e) {
            // Klaida iš DB (pvz. laukui negalima null)
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Duomenų bazės klaida: " + e.getMostSpecificCause().getMessage()
            ));
        } catch (Exception e) {
            // Bet kokia kita klaida
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Klaida kuriant paslaugą: " + e.getMessage()
            ));
        }
    }


    // ===========================
    // UPDATE FEE
    // ===========================
    @PutMapping("/{id}")
    @ResponseBody
    public String updateFee(@PathVariable Long id, @RequestBody Fee updatedFee) {
        Optional<Fee> existingOpt = feeService.findById(id);
        if (existingOpt.isEmpty()) {
            return "{\"error\":\"Mokestis nerastas.\"}";
        }

        Fee fee = existingOpt.get();
        fee.setName(updatedFee.getName());
        fee.setUnit(updatedFee.getUnit());
        fee.setDescription(updatedFee.getDescription());

        feeService.save(fee);
        return "{\"success\":\"Mokestis atnaujintas sėkmingai.\"}";
    }

    // ===========================
    // DELETE FEE
    // ===========================
    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteFee(@PathVariable Long id) {
        try {
            Optional<Fee> existingOpt = feeService.findById(id);
            if (existingOpt.isEmpty()) {
                return "{\"error\":\"Mokestis nerastas.\"}";
            }

            feeService.deleteById(id);
            return "{\"success\":\"Mokestis pašalintas.\"}";
        } catch (Exception e) {
            return "{\"error\":\"Klaida šalinant mokestį: " + e.getMessage() + "\"}";
        }
    }

}

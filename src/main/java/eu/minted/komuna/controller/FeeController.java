package eu.minted.komuna.controller;

import eu.minted.komuna.model.Fee;
import eu.minted.komuna.model.User;
import eu.minted.komuna.service.FeeService;
import eu.minted.komuna.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/fees")
public class FeeController {

    private final FeeService feeService;
    private final UserService userService;

    public FeeController(FeeService feeService, UserService userService) {
        this.feeService = feeService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(feeService.findAll());
    }

    @GetMapping("/{id}")
    public Fee getById(@PathVariable Long id) {
        return feeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            String type = (String) payload.get("type");
            Double amount = Double.parseDouble(payload.get("amount").toString());
            boolean paid = Boolean.parseBoolean(payload.get("paid").toString());
            Long userId = payload.get("userId") != null ? Long.parseLong(payload.get("userId").toString()) : null;

            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Vartotojo ID privalomas."));
            }

            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nurodytas vartotojas nerastas."));
            }

            Fee fee = new Fee();
            fee.setType(type);
            fee.setAmount(amount);
            fee.setPaid(paid);
            fee.setUser(user);

            feeService.save(fee);

            return ResponseEntity.ok(fee);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Klaida kuriant įrašą: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Fee fee = feeService.findById(id);
            if (fee == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mokestis su ID " + id + " nerastas."));
            }

            // Atnaujinam laukus
            if (payload.containsKey("type")) fee.setType((String) payload.get("type"));
            if (payload.containsKey("amount")) fee.setAmount(Double.parseDouble(payload.get("amount").toString()));
            if (payload.containsKey("paid")) fee.setPaid(Boolean.parseBoolean(payload.get("paid").toString()));

            if (payload.containsKey("userId")) {
                Long userId = Long.parseLong(payload.get("userId").toString());
                User user = userService.findById(userId);
                if (user == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Nurodytas vartotojas nerastas."));
                }
                fee.setUser(user);
            }

            feeService.save(fee);

            return ResponseEntity.ok(fee);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Klaida atnaujinant įrašą: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            feeService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Įrašas pašalintas."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Klaida šalinant įrašą: " + e.getMessage()));
        }
    }

}

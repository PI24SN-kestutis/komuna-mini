package eu.minted.komuna.controller;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Role;
import eu.minted.komuna.model.User;
import eu.minted.komuna.service.CommunityService;
import eu.minted.komuna.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            String roleName = (String) payload.get("role");
            String communityCode = (String) payload.get("communityCode");

            // 🔎 Patikrinam ar el. paštas jau egzistuoja
            if (userService.existsByEmail(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Vartotojas su tokiu el. paštu jau egzistuoja."));
            }

            Role role = userService.findRoleByName(roleName);
            Community community = userService.findCommunityByCode(communityCode);

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            user.setCommunity(community);

            userService.save(user);
            return ResponseEntity.ok(user);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Įvyko nenumatyta klaida: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            User existing = userService.findById(id);
            if (existing == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Vartotojas nerastas."));
            }

            String name = (String) payload.get("name");
            String email = (String) payload.get("email");
            String roleName = (String) payload.get("role");
            String communityCode = (String) payload.get("communityCode");

            // 🔎 Tikriname el. paštą (jei keičiamas)
            if (email != null && !email.equals(existing.getEmail()) && userService.existsByEmail(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Vartotojas su tokiu el. paštu jau egzistuoja."));
            }

            if (name != null) existing.setName(name);
            if (email != null) existing.setEmail(email);

            if (roleName != null && !roleName.isBlank()) {
                Role role = userService.findRoleByName(roleName);
                existing.setRole(role);
            }

            if (communityCode != null && !communityCode.isBlank()) {
                Community community = userService.findCommunityByCode(communityCode);
                existing.setCommunity(community);
            }

            userService.save(existing);
            return ResponseEntity.ok(existing);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Klaida redaguojant vartotoją: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}


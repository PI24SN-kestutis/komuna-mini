package eu.minted.komuna.controller;

import eu.minted.komuna.model.Role;
import eu.minted.komuna.model.User;
import eu.minted.komuna.service.CommunityService;
import eu.minted.komuna.service.RoleService;
import eu.minted.komuna.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseBody
    public Map<String, String> createUser(@RequestBody Map<String, Object> payload, Authentication auth) {
        try {
            String name = (String) payload.get("name");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            String roleName = (String) payload.get("role");
            String communityCode = (String) payload.get("communityCode");

            if (email == null || email.isBlank())
                return Map.of("error", "El. paštas privalomas.");
            if (name == null || name.isBlank())
                return Map.of("error", "Vardas privalomas.");

            Optional<Role> roleOpt = roleService.findByName(roleName);
            if (roleOpt.isEmpty())
                return Map.of("error", "Neteisinga rolė.");


            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setRole(roleOpt.get());


            boolean isManager = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));


            if (isManager) {
                User manager = userService.findByEmail(auth.getName()).orElseThrow();
                user.setCommunity(manager.getCommunity());
            }

            else if (communityCode != null && !communityCode.isBlank()) {
                communityService.findByCode(communityCode).ifPresent(user::setCommunity);
            }


            String rawPassword = (password != null && !password.isBlank())
                    ? password
                    : name.trim();
            user.setPassword(passwordEncoder.encode(rawPassword));

            userService.save(user);
            return Map.of("success", "Vartotojas sukurtas sėkmingai.",
                    "generatedPassword", rawPassword);
        } catch (Exception e) {
            return Map.of("error", "Klaida kuriant vartotoją: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Map<String, String> updateUser(@PathVariable Long id,
                                          @RequestBody Map<String, Object> payload,
                                          Authentication auth) {
        Optional<User> existingOpt = userService.findById(id);
        if (existingOpt.isEmpty()) {
            return Map.of("error", "Vartotojas nerastas.");
        }

        try {
            User targetUser = existingOpt.get();
            User currentUser = userService.findByEmail(auth.getName()).orElseThrow();

            String currentRole = currentUser.getRole().getName();

            // RESIDENT gali keisti tik savo duomenis (vardą, email, password)
            if (currentRole.equals("RESIDENT")) {
                if (!currentUser.getId().equals(targetUser.getId())) {
                    return Map.of("error", "Negalite redaguoti kitų naudotojų duomenų.");
                }

                targetUser.setName((String) payload.getOrDefault("name", targetUser.getName()));
                targetUser.setEmail((String) payload.getOrDefault("email", targetUser.getEmail()));


                if (payload.containsKey("password")) {
                    String newPass = (String) payload.get("password");
                    if (newPass != null && !newPass.isBlank()) {
                        targetUser.setPassword(passwordEncoder.encode(newPass));
                    }
                }

                userService.save(targetUser);
                return Map.of("success", "Profilis atnaujintas sėkmingai.");
            }

            // MANAGER gali redaguoti tik savo bendrijos vartotojus (be rolės keitimo)
            if (currentRole.equals("MANAGER")) {
                if (targetUser.getCommunity() == null ||
                        !targetUser.getCommunity().getId().equals(currentUser.getCommunity().getId())) {
                    return Map.of("error", "Negalite redaguoti kitų bendrijų vartotojų.");
                }

                targetUser.setName((String) payload.get("name"));
                targetUser.setEmail((String) payload.get("email"));

                userService.save(targetUser);
                return Map.of("success", "Vartotojas atnaujintas sėkmingai.");
            }

            // ADMIN – visos teisės
            if (currentRole.equals("ADMIN")) {
                targetUser.setName((String) payload.get("name"));
                targetUser.setEmail((String) payload.get("email"));

                String roleName = (String) payload.get("role");
                if (roleName != null)
                    roleService.findByName(roleName).ifPresent(targetUser::setRole);

                String communityCode = (String) payload.get("communityCode");
                if (communityCode != null && !communityCode.isBlank())
                    communityService.findByCode(communityCode).ifPresent(targetUser::setCommunity);

                userService.save(targetUser);
                return Map.of("success", "Vartotojas atnaujintas sėkmingai (admin režimas).");
            }

            return Map.of("error", "Rolė neatpažinta.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Klaida atnaujinant vartotoją: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    @ResponseBody
    public Map<String, String> deleteUser(@PathVariable Long id, Authentication auth) {
        try {
            Optional<User> existingOpt = userService.findById(id);
            if (existingOpt.isEmpty()) {
                return Map.of("error", "Vartotojas nerastas.");
            }

            User user = existingOpt.get();

            boolean isManager = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
            if (isManager) {
                User manager = userService.findByEmail(auth.getName()).orElseThrow();
                if (user.getCommunity() == null ||
                        !user.getCommunity().getId().equals(manager.getCommunity().getId())) {
                    return Map.of("error", "Negalite pašalinti kitų bendrijų vartotojų.");
                }
            }

            userService.deleteById(id);
            return Map.of("success", "Vartotojas pašalintas.");
        } catch (Exception e) {
            return Map.of("error", "Klaida šalinant vartotoją: " + e.getMessage());
        }
    }
}

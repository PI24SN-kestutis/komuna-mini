package eu.minted.komuna.controller;

import eu.minted.komuna.model.User;
import eu.minted.komuna.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model) {

        User user = userRepository.findAll()
                .stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user == null) {
            model.addAttribute("error", "Neteisingas el. paštas arba slaptažodis");
            return "login";
        }

        model.addAttribute("user", user);
        return "redirect:/dashboard/" + user.getRole().getName().toLowerCase();
    }
}

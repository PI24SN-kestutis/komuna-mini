package eu.minted.komuna.controller;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.model.User;
import eu.minted.komuna.service.CommunityService;
import eu.minted.komuna.service.FeeService;
import eu.minted.komuna.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard/resident")
public class ResidentController {

    private final UserService userService;
    private final FeeService feeService;
    private final CommunityService communityService;

    public ResidentController(UserService userService, FeeService feeService, CommunityService communityService) {
        this.userService = userService;
        this.feeService = feeService;
        this.communityService = communityService;
    }

    /**
     * Gyventojo skydelis
     */
    @GetMapping
    public String residentDashboard(
            @RequestParam(defaultValue = "overview") String view,
            Authentication authentication,
            Model model) {

        Optional<User> residentOpt = userService.findByEmail(authentication.getName());
        User resident = residentOpt.orElse(null);

        if (resident == null) {
            model.addAttribute("error", "Naudotojas nerastas.");
            return "error";
        }

        model.addAttribute("pageTitle", "Gyventojo skydelis");
        model.addAttribute("role", "RESIDENT");
        model.addAttribute("view", view);
        model.addAttribute("contentTemplate", "dashboard-resident");
        model.addAttribute("user", resident);

        Community community = resident.getCommunity();
        model.addAttribute("community", community);

        switch (view) {
            case "payments" -> {
                if (community != null) {
                    List<Fee> fees = feeService.findByCommunity(community);
                    model.addAttribute("fees", fees);
                } else {
                    model.addAttribute("fees", List.of());
                    model.addAttribute("error", "Gyventojas nepriskirtas jokiai bendrijai.");
                }
            }
            case "profile" -> model.addAttribute("user", resident);
            case "notifications" -> model.addAttribute("notifications", List.of());
            default -> model.addAttribute("communityName", community != null ? community.getName() : "Nepriskirta");
        }

        return "layout";
    }

    /**
     * Apmokėjimo veiksmas
     */
    @PostMapping("/pay/{id}")
    @ResponseBody
    public ResponseEntity<?> payFee(@PathVariable Long id, Authentication authentication) {
        Optional<User> residentOpt = userService.findByEmail(authentication.getName());
        User resident = residentOpt.orElse(null);
        if (resident == null) {
            return ResponseEntity.badRequest().body("{\"error\":\"Naudotojas nerastas.\"}");
        }

        Optional<Fee> feeOpt = feeService.findById(id);
        if (feeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\":\"Mokestis nerastas.\"}");
        }

        Fee fee = feeOpt.get();

        // Tikrinti, ar gyventojas priklauso tai pačiai bendrijai
        if (resident.getCommunity() == null ||
                !resident.getCommunity().getId().equals(fee.getCommunity().getId())) {
            return ResponseEntity.status(403).body("{\"error\":\"Negalite apmokėti svetimos bendrijos mokesčio.\"}");
        }

        // Žymime apmokėjimą
        fee.setPaid(true);
        feeService.save(fee);

        return ResponseEntity.ok("{\"success\":\"Mokestis sėkmingai apmokėtas.\"}");
    }
}

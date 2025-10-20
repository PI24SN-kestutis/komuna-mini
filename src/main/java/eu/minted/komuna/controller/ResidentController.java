package eu.minted.komuna.controller;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.model.Price;
import eu.minted.komuna.model.User;
import eu.minted.komuna.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard/resident")
public class ResidentController {

    private final UserService userService;
    private final FeeService feeService;
    private final PriceService priceService;

    public ResidentController(UserService userService, FeeService feeService, PriceService priceService) {
        this.userService = userService;
        this.feeService = feeService;
        this.priceService = priceService;
    }

    @GetMapping
    public String residentDashboard(
            @RequestParam(defaultValue = "overview") String view,
            Authentication authentication,
            Model model) {

        Optional<User> residentOpt = userService.findByEmail(authentication.getName());
        if (residentOpt.isEmpty()) {
            model.addAttribute("error", "Naudotojas nerastas.");
            return "error";
        }

        User resident = residentOpt.get();
        Community community = resident.getCommunity();

        model.addAttribute("pageTitle", "Gyventojo skydelis");
        model.addAttribute("role", "RESIDENT");
        model.addAttribute("view", view);
        model.addAttribute("contentTemplate", "dashboard-resident");
        model.addAttribute("user", resident);
        model.addAttribute("community", community);

        switch (view) {
            case "payments" -> {
                if (community != null) {
                    model.addAttribute("fees", feeService.findByCommunity(community));
                    model.addAttribute("prices", priceService.findByCommunity(community));
                } else {
                    model.addAttribute("fees", List.of());
                    model.addAttribute("prices", List.of());
                    model.addAttribute("error", "Gyventojas nepriskirtas jokiai bendrijai.");
                }
            }
            case "profile" -> model.addAttribute("user", resident);
            default -> {
                model.addAttribute("communityName", community != null ? community.getName() : "Nepriskirta");
                model.addAttribute("feesCount", community != null ? feeService.findByCommunity(community).size() : 0);
                model.addAttribute("pricesCount", community != null ? priceService.findByCommunity(community).size() : 0);
            }
        }

        return "layout";
    }
}

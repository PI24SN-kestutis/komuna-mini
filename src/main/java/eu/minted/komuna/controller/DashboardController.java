package eu.minted.komuna.controller;

import eu.minted.komuna.service.CommunityService;
import eu.minted.komuna.service.PriceService;
import eu.minted.komuna.service.UserService;
import eu.minted.komuna.service.FeeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final CommunityService communityService;
    private final UserService userService;
    private final FeeService feeService;
    private final PriceService priceService;

    public DashboardController(CommunityService communityService, UserService userService, FeeService feeService, PriceService priceService) {
        this.communityService = communityService;
        this.userService = userService;
        this.feeService = feeService;
        this.priceService = priceService;
    }

    @GetMapping
    public String redirectToDashboard(Authentication auth) {
        if (auth == null) return "redirect:/login";
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return switch (role) {
            case "ROLE_ADMIN" -> "redirect:/dashboard/admin";
            case "ROLE_MANAGER" -> "redirect:/dashboard/manager";
            default -> "redirect:/dashboard/resident";
        };
    }

    // ADMIN DASHBOARD
    @GetMapping("/admin")
    public String adminDashboard(@RequestParam(defaultValue = "overview") String view, Model model) {
        model.addAttribute("pageTitle", "Administratoriaus skydelis");
        model.addAttribute("role", "ADMIN");
        model.addAttribute("view", view);
        model.addAttribute("contentTemplate", "dashboard-admin");

        model.addAttribute("communitiesCount", communityService.findAll().size());
        model.addAttribute("usersCount", userService.findAll().size());
        model.addAttribute("feesCount", feeService.findAll().size());
        model.addAttribute("communities", communityService.findAll());

        switch (view) {
            case "users" -> model.addAttribute("users", userService.findAll());
            case "communities" -> model.addAttribute("communities", communityService.findAll());
            case "fees" -> {
                model.addAttribute("fees", feeService.findAll());
                model.addAttribute("communities", communityService.findAll());
            }
            case "prices" -> {
                model.addAttribute("prices", priceService.findAll());
                model.addAttribute("fees", feeService.findAll());
                model.addAttribute("communities", communityService.findAll());
            }

            default -> {}
        }

        return "layout";
    }

}

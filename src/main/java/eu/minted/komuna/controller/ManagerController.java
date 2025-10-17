package eu.minted.komuna.controller;

import eu.minted.komuna.model.*;
import eu.minted.komuna.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard/manager")
public class ManagerController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private PriceService priceService;

    @GetMapping
    public String managerDashboard(
            @RequestParam(defaultValue = "overview") String view,
            Authentication authentication,
            Model model) {


        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }

        Optional<User> managerOpt = userService.findByEmail(authentication.getName());
        if (managerOpt.isEmpty()) {
            model.addAttribute("error", "Naudotojas nerastas.");
            return "error";
        }

        User manager = managerOpt.get();

        if (manager.getCommunity() == null) {
            model.addAttribute("error", "Vadybininkas nepriskirtas jokiai bendrijai.");
            return "error";
        }


        Community community = manager.getCommunity();
        List<User> residents = userService.findByCommunity(community);
        List<Fee> fees = feeService.findByCommunity(community);
        List<Price> prices = priceService.findByCommunity(community);


        model.addAttribute("community", community);
        model.addAttribute("communityName", community.getName());
        model.addAttribute("residentsCount", residents.size());
        model.addAttribute("feesCount", fees.size());
        model.addAttribute("pricesCount", prices.size());


        model.addAttribute("role", "MANAGER");
        model.addAttribute("view", view);
        model.addAttribute("pageTitle", "Vadybininko skydelis");
        model.addAttribute("contentTemplate", "dashboard-manager");


        switch (view) {
            case "residents" -> {
                model.addAttribute("residents", residents);
            }

            case "fees" -> {
                model.addAttribute("fees", fees);
            }

            case "prices" -> {
                model.addAttribute("prices", prices);
                model.addAttribute("fees", fees);
            }

            case "reports" -> {
                model.addAttribute("residents", residents);
                model.addAttribute("fees", fees);
                model.addAttribute("prices", prices);
                model.addAttribute("community", community);
            }

            default -> {
                model.addAttribute("residents", residents);
                model.addAttribute("fees", fees);
                model.addAttribute("prices", prices);
            }
        }

        return "layout";
    }
}

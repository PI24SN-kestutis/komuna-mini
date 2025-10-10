package eu.minted.komuna.controller;


import eu.minted.komuna.service.FeeService;
import eu.minted.komuna.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard/resident")
public class ResidentController {

    private final UserService userService;
    private final FeeService feeService;

    public ResidentController(UserService userService, FeeService feeService) {
        this.userService = userService;
        this.feeService = feeService;
    }

    @GetMapping
    public String residentDashboard(Authentication auth, Model model) {
        var userEmail = auth.getName();
        var user = userService.findByEmail(userEmail);

        var fees = feeService.findByUserId(user.getId());
        var total = feeService.sumByUserId(user.getId());
        var paid = feeService.sumPaidByUserId(user.getId());
        var balance = total - paid;

        model.addAttribute("pageTitle","Gyventojo skydelis");
        model.addAttribute("role","RESIDENT");
        model.addAttribute("fees", fees);
        model.addAttribute("totalFees", total);
        model.addAttribute("paidFees", paid);
        model.addAttribute("balance", balance);
        model.addAttribute("notifications", List.of());

        model.addAttribute("contentTemplate", "dashboard-resident");
        return "layout";
    }

    @PostMapping("/pay/{id}")
    public String pay(@PathVariable Long id) {
        feeService.markAsPaid(id);
        return "redirect:/dashboard/resident";
    }


}

package eu.minted.komuna.controller;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.model.User;
import eu.minted.komuna.service.CommunityService;
import eu.minted.komuna.service.FeeService;
import eu.minted.komuna.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/dashboard/manager")
public class ManagerController {

    private final UserService userService;
    private final FeeService feeService;
    private final CommunityService communityService;

    public ManagerController(UserService userService, FeeService feeService, CommunityService communityService) {
        this.userService = userService;
        this.feeService = feeService;
        this.communityService = communityService;
    }

    @GetMapping
    public String managerDashboard(
            @RequestParam(defaultValue = "overview") String view,
            Authentication authentication,
            Model model) {

        User manager = userService.findByEmail(authentication.getName());
        if (manager == null || manager.getCommunity() == null) {
            model.addAttribute("error", "Bendrija nerasta arba naudotojas nepriskirtas jokiai bendrijai.");
            return "error";
        }

        Community community = manager.getCommunity();
        model.addAttribute("pageTitle", "Valdytojo skydelis");
        model.addAttribute("role", "MANAGER");
        model.addAttribute("view", view);
        model.addAttribute("community", community);
        model.addAttribute("contentTemplate", "dashboard-manager");
        model.addAttribute("residentsCount", userService.countResidentsByCommunity(community));
        model.addAttribute("feesCount", feeService.countByCommunity(community));
        model.addAttribute("totalPaid", feeService.sumPaidByCommunity(community));
        model.addAttribute("totalUnpaid", feeService.sumUnpaidByCommunity(community));
        switch (view) {
            case "residents" -> model.addAttribute("residents", userService.findResidentsByCommunity(community));
            case "fees" -> model.addAttribute("fees", feeService.findByCommunity(community));
            case "reports" -> model.addAttribute("reports", feeService.buildReportForCommunity(community));
        }

        return "layout";
    }

    @GetMapping("/reports/export")
    public void exportCsv(Authentication authentication, HttpServletResponse response) throws IOException {
        User manager = userService.findByEmail(authentication.getName());
        if (manager == null || manager.getCommunity() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bendrija nerasta arba naudotojas nepriskirtas jokiai bendrijai.");
            return;
        }

        Community community = manager.getCommunity();
        List<Fee> fees = feeService.buildReportForCommunity(community);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"report_" + community.getCode() + ".csv\"");

        PrintWriter writer = response.getWriter();
        writer.write('\uFEFF');

        writer.println("Gyventojas,Tipas,Suma (€),Apmokėta");
        for (Fee f : fees) {
            String name = f.getUser() != null ? f.getUser().getName() : "-";
            String type = f.getType() != null ? f.getType() : "-";
            String amount = String.format("%.2f", f.getAmount());
            String paid = f.isPaid() ? "Taip" : "Ne";
            writer.printf("%s,%s,%s,%s%n", name, type, amount, paid);
        }

        writer.flush();
        writer.close();
    }
}

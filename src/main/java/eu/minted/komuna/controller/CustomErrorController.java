package eu.minted.komuna.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.web.servlet.error.ErrorController;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute("jakarta.servlet.error.status_code");

        String message = "Įvyko netikėta klaida.";
        if (statusCode != null) {
            switch (statusCode.toString()) {
                case "404" -> message = "Puslapis nerastas 😕";
                case "403" -> message = "Prieiga uždrausta 🚫";
                case "500" -> message = "Serverio klaida 💥";
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String dashboardLink = "/login"; // numatytasis

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            if (role.equals("ROLE_ADMIN")) dashboardLink = "/dashboard/admin";
            else if (role.equals("ROLE_MANAGER")) dashboardLink = "/dashboard/manager";
            else if (role.equals("ROLE_RESIDENT")) dashboardLink = "/dashboard/resident";
        }

        model.addAttribute("status", statusCode);
        model.addAttribute("message", message);
        model.addAttribute("dashboardLink", dashboardLink);

        return "error/404";
    }
}

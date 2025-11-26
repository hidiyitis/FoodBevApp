package com.foodbev.FoodBevApp.controller.home;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated (bukan anonymous user)
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // User sudah login
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAuthenticated", true);

            // Check role untuk menentukan tampilan
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            boolean isUser = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isUser", isUser);

        } else {
            // User belum login
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("isAdmin", false);
            model.addAttribute("isUser", false);
        }

        return "home";
    }
}
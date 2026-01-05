package com.foodbev.FoodBevApp.controller.auth;

import com.foodbev.FoodBevApp.dto.user.UserRegistrationDto;
import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Menampilkan halaman login - redirect jika sudah login
     */
    @GetMapping("/login")
    public String loginForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/dashboard";
            }
        }

        return "auth/login";
    }

    /**
     * Menampilkan halaman registrasi - redirect jika sudah login
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Jika user sudah login, redirect ke home
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/home";
        }

        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    /**
     * Memproses pendaftaran user baru
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validasi manual untuk konfirmasi password
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        // Validasi jika email sudah terdaftar
        if (!result.hasFieldErrors("email")) {
            try {
                User existingUser = userService.findByEmail(registrationDto.getEmail());
                if (existingUser != null) {
                    result.rejectValue("email", "error.email", "Email already registered");
                }
            } catch (Exception e) {
                // Email tidak ditemukan, artinya available
            }
        }

        // Jika ada error, kembali ke halaman registrasi
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration failed! Please try again.");
            return "auth/register";
        }

        try {
            // Simpan user baru
            userService.save(registrationDto);

            // Redirect ke halaman login dengan pesan sukses
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please login to your account.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            // Handle exception saat menyimpan user
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
            return "auth/register";
        }
    }
}
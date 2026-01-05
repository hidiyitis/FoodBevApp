package com.foodbev.FoodBevApp.controller.auth;

import com.foodbev.FoodBevApp.constants.RoleConstants;
import com.foodbev.FoodBevApp.dto.user.UserRegistrationDto;
import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.service.user.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final String REDIRECT_HOME = "redirect:/home";
    private static final String AUTH_REGISTER_VIEW = "auth/register";
    private static final String AUTH_LOGIN_REDIRECT = "redirect:/auth/login";

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Menampilkan halaman login - redirect jika sudah login
     */
    @GetMapping("/login")
    public String loginForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticated(auth)) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(RoleConstants.ROLE_ADMIN))) {
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
        if (isAuthenticated(auth)) {
            return REDIRECT_HOME;
        }

        model.addAttribute("user", new UserRegistrationDto());
        return AUTH_REGISTER_VIEW;
    }

    /**
     * Memproses pendaftaran user baru
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Custom validation
        validateRegistration(registrationDto, result);

        // Jika ada error, kembali ke halaman registrasi dengan pesan spesifik
        if (result.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            result.getFieldErrors().forEach(error -> {
                if (!errorMessages.isEmpty()) {
                    errorMessages.append(" ");
                }
                errorMessages.append(error.getDefaultMessage()).append(".");
            });
            model.addAttribute("errorMessage", errorMessages.toString());
            return AUTH_REGISTER_VIEW;
        }

        try {
            // Simpan user baru
            userService.save(registrationDto);

            // Redirect ke halaman login dengan pesan sukses
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please login to your account.");
            return AUTH_LOGIN_REDIRECT;

        } catch (Exception e) {
            return handleRegistrationException(e, model);
        }
    }

    private boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
    }

    private void validateRegistration(UserRegistrationDto registrationDto, BindingResult result) {
        // Validasi manual untuk konfirmasi password
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        // Validasi jika email sudah terdaftar
        validateEmailUniqueness(registrationDto, result);

        // Validasi jika phone sudah terdaftar
        validatePhoneUniqueness(registrationDto, result);
    }

    private void validateEmailUniqueness(UserRegistrationDto registrationDto, BindingResult result) {
        if (!result.hasFieldErrors("email")) {
            try {
                User existingUser = userService.findByEmail(registrationDto.getEmail());
                if (existingUser != null) {
                    result.rejectValue("email", "error.email", "Email already registered");
                }
            } catch (Exception e) {
                // Email not found, which means it's available
                log.debug("Email {} is available for registration", registrationDto.getEmail());
            }
        }
    }

    private void validatePhoneUniqueness(UserRegistrationDto registrationDto, BindingResult result) {
        if (!result.hasFieldErrors("phone") && registrationDto.getPhone() != null &&
                userService.existsByPhone(registrationDto.getPhone())) {
            result.rejectValue("phone", "error.phone", "Phone number already registered");
        }
    }

    private String handleRegistrationException(Exception e, Model model) {
        // Handle exception saat menyimpan user dengan pesan spesifik
        String errorMessage = "Registration failed. ";
        String exceptionMessage = e.getMessage();

        if (exceptionMessage != null) {
            if (exceptionMessage.contains("email")) {
                errorMessage += "Email address is already in use.";
            } else if (exceptionMessage.contains("phone")) {
                errorMessage += "Phone number is already in use.";
            } else if (exceptionMessage.contains("duplicate") || exceptionMessage.contains("unique")) {
                errorMessage += "Some information you provided is already registered.";
            } else {
                errorMessage += "Please check your information and try again.";
            }
        } else {
            errorMessage += "Please try again later.";
        }

        model.addAttribute("errorMessage", errorMessage);
        return AUTH_REGISTER_VIEW;
    }
}
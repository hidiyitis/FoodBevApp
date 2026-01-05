package com.foodbev.FoodBevApp.controller.admin;

import com.foodbev.FoodBevApp.dto.admin.AdminUserRegistrationDto;
import com.foodbev.FoodBevApp.dto.admin.UserResponseDto;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

  private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);
  private static final String ADMIN_USER_LIST_VIEW = "admin/user/list";
  private static final String REDIRECT_ADMIN_USERS = "redirect:/admin/users";

  private final UserService userService;

  @Autowired
  public AdminUserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Display list of all admins (excluding current logged in user)
   */
  @GetMapping
  public String listAdmins(Model model) {
    addCommonAttributes(model);
    model.addAttribute("users", getFilteredAdminList());
    model.addAttribute("adminDto", new AdminUserRegistrationDto());
    return ADMIN_USER_LIST_VIEW;
  }

  /**
   * Process admin registration
   */
  @PostMapping("/create")
  public String createAdmin(@Valid @ModelAttribute("adminDto") AdminUserRegistrationDto dto,
      BindingResult result,
      Model model,
      RedirectAttributes redirectAttributes) {

    validateAdminRegistration(dto, result);

    if (result.hasErrors()) {
      addCommonAttributes(model);
      model.addAttribute("users", getFilteredAdminList());
      model.addAttribute("showModal", true);
      return ADMIN_USER_LIST_VIEW;
    }

    try {
      userService.saveAdmin(dto);
      redirectAttributes.addFlashAttribute("successMessage", "Admin account created successfully!");
      return REDIRECT_ADMIN_USERS;
    } catch (Exception e) {
      log.error("Failed to create admin account for email: {}", dto.getEmail(), e);
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to create admin account. Please try again.");
      return REDIRECT_ADMIN_USERS;
    }
  }

  private void validateAdminRegistration(AdminUserRegistrationDto dto, BindingResult result) {
    // Validate password confirmation
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
      result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
    }

    // Check if email already exists
    if (!result.hasFieldErrors("email")) {
      try {
        User existingUser = userService.findByEmail(dto.getEmail());
        if (existingUser != null) {
          result.rejectValue("email", "error.email", "Email already registered");
        }
      } catch (Exception e) {
        log.debug("Email {} is available for registration", dto.getEmail());
      }
    }

    // Check if phone already exists
    if (!result.hasFieldErrors("phone") && userService.existsByPhone(dto.getPhone())) {
      result.rejectValue("phone", "error.phone", "Phone number already registered");
    }
  }

  /**
   * Get filtered admin list excluding current logged in user.
   * Extracted to avoid code duplication.
   */
  private List<UserResponseDto> getFilteredAdminList() {
    String currentUserEmail = getCurrentUserEmail();
    return userService.findAllAdmins().stream()
        .filter(user -> !user.getEmail().equals(currentUserEmail))
        .map(UserResponseDto::fromEntity)
        .toList(); // Java 16+ style
  }

  private String getCurrentUserEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getName();
  }

  private void addCommonAttributes(Model model) {
    model.addAttribute("username", getCurrentUserEmail());
    model.addAttribute("isAdmin", true);
  }
}

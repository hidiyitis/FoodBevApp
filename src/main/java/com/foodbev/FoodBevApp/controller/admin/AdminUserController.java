package com.foodbev.FoodBevApp.controller.admin;

import com.foodbev.FoodBevApp.dto.admin.AdminUserRegistrationDto;
import com.foodbev.FoodBevApp.dto.admin.UserResponseDto;
import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

  @Autowired
  private UserService userService;

  /**
   * Display list of all admins (excluding current logged in user)
   */
  @GetMapping
  public String listAdmins(Model model) {
    addCommonAttributes(model);

    // Get current logged in user's email
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = auth.getName();

    // Filter out current user from admin list
    List<UserResponseDto> admins = userService.findAllAdmins().stream()
        .filter(user -> !user.getEmail().equals(currentUserEmail))
        .map(UserResponseDto::fromEntity)
        .collect(Collectors.toList());

    model.addAttribute("users", admins);
    model.addAttribute("adminDto", new AdminUserRegistrationDto());
    return "admin/user/list";
  }

  /**
   * Process admin registration
   */
  @PostMapping("/create")
  public String createAdmin(@Valid @ModelAttribute("adminDto") AdminUserRegistrationDto dto,
      BindingResult result,
      Model model,
      RedirectAttributes redirectAttributes) {

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
        // Email not found, which means it's available
      }
    }

    // Check if phone already exists
    if (!result.hasFieldErrors("phone")) {
      if (userService.existsByPhone(dto.getPhone())) {
        result.rejectValue("phone", "error.phone", "Phone number already registered");
      }
    }

    if (result.hasErrors()) {
      addCommonAttributes(model);
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String currentUserEmail = auth.getName();
      List<UserResponseDto> admins = userService.findAllAdmins().stream()
          .filter(user -> !user.getEmail().equals(currentUserEmail))
          .map(UserResponseDto::fromEntity)
          .collect(Collectors.toList());
      model.addAttribute("users", admins);
      model.addAttribute("showModal", true);
      return "admin/user/list";
    }

    try {
      userService.saveAdmin(dto);
      redirectAttributes.addFlashAttribute("successMessage", "Admin account created successfully!");
      return "redirect:/admin/users";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to create admin account. Please try again.");
      return "redirect:/admin/users";
    }
  }

  /**
   * Toggle user active status
   */
  @PostMapping("/{id}/toggle-status")
  public String toggleUserStatus(@PathVariable Long id,
      @RequestParam(required = false) String returnTab,
      RedirectAttributes redirectAttributes) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = auth.getName();

    try {
      User user = userService.findById(id)
          .orElseThrow(() -> new RuntimeException("User not found"));

      // Prevent admin from deactivating themselves
      if (user.getEmail().equals(currentUserEmail)) {
        redirectAttributes.addFlashAttribute("errorMessage", "You cannot deactivate your own account!");
        return getRedirectUrl(returnTab);
      }

      boolean newStatus = !user.getIsActive();
      userService.updateUserStatus(id, newStatus);

      String statusText = newStatus ? "activated" : "deactivated";
      redirectAttributes.addFlashAttribute("successMessage",
          "User " + user.getName() + " has been " + statusText + " successfully!");

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user status.");
    }

    return getRedirectUrl(returnTab);
  }

  private String getRedirectUrl(String returnTab) {
    return "redirect:/admin/users";
  }

  private void addCommonAttributes(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    model.addAttribute("username", auth.getName());
    model.addAttribute("isAdmin", true);
  }
}

package com.foodbev.FoodBevApp.controller.admin.api;

import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserApiController {

  private static final Logger log = LoggerFactory.getLogger(AdminUserApiController.class);

  private final UserService userService;

  @Autowired
  public AdminUserApiController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Toggle user active status (REST API)
   */
  @PostMapping("/{id}/toggle-status")
  public Map<String, Object> toggleUserStatus(@PathVariable Long id) {
    Map<String, Object> response = new HashMap<>();

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = auth.getName();

    try {
      User user = userService.findById(id)
          .orElseThrow(() -> new RuntimeException("User not found"));

      // Prevent admin from deactivating themselves
      if (user.getEmail().equals(currentUserEmail)) {
        response.put("success", false);
        response.put("message", "You cannot deactivate your own account!");
        return response;
      }

      boolean newStatus = !user.getIsActive();
      userService.updateUserStatus(id, newStatus);

      String statusText = newStatus ? "activated" : "deactivated";
      response.put("success", true);
      response.put("data", newStatus);
      response.put("message", "User " + user.getName() + " has been " + statusText + " successfully!");

    } catch (Exception e) {
      log.error("Failed to toggle status for user id: {}", id, e);
      response.put("success", false);
      response.put("message", "Failed to update user status.");
    }

    return response;
  }
}

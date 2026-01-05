package com.foodbev.FoodBevApp.constants;

/**
 * Constants for user roles used throughout the application.
 * Centralizes role names to avoid hardcoded strings.
 */
public final class RoleConstants {

  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_USER = "ROLE_USER";

  /**
   * Number of super admins to skip when listing regular admins.
   * Super admins are protected and should not be shown in the admin list.
   */
  public static final int SUPER_ADMIN_COUNT = 1;

  private RoleConstants() {
    // Private constructor to prevent instantiation
  }
}

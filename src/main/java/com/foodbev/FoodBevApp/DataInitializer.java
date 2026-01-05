package com.foodbev.FoodBevApp;

import com.foodbev.FoodBevApp.constants.RoleConstants;
import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.default.admin.email:admin@foodbev.com}")
    private String defaultAdminEmail;

    @Value("${app.default.admin.password:#{null}}")
    private String defaultAdminPassword;

    @Value("${app.default.user.email:user@foodbev.com}")
    private String defaultUserEmail;

    @Value("${app.default.user.password:#{null}}")
    private String defaultUserPassword;

    @Autowired
    public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only initialize default users if the users table is empty
        if (userRepository.count() > 0) {
            log.info("Users table is not empty. Skipping default user initialization.");
            return;
        }

        log.info("Users table is empty. Initializing default users...");
        createDefaultAdmin();
        createDefaultUser();
    }

    private void createDefaultAdmin() {
        if (defaultAdminPassword == null || defaultAdminPassword.isEmpty()) {
            log.warn("No default admin password configured. Skipping admin user creation. " +
                    "Set 'app.default.admin.password' in application.properties or environment variable.");
            return;
        }

        User admin = new User();
        admin.setName("Administrator");
        admin.setPhone("1234567890");
        admin.setEmail(defaultAdminEmail);
        admin.setPasswordHash(passwordEncoder.encode(defaultAdminPassword));
        admin.setRole(RoleConstants.ROLE_ADMIN);
        admin.setIsActive(true);
        userRepository.save(admin);
        log.info("Admin user created: {}", defaultAdminEmail);
    }

    private void createDefaultUser() {
        if (defaultUserPassword == null || defaultUserPassword.isEmpty()) {
            log.warn("No default user password configured. Skipping user creation. " +
                    "Set 'app.default.user.password' in application.properties or environment variable.");
            return;
        }

        User user = new User();
        user.setName("Regular User");
        user.setPhone("0987654321");
        user.setEmail(defaultUserEmail);
        user.setPasswordHash(passwordEncoder.encode(defaultUserPassword));
        user.setRole(RoleConstants.ROLE_USER);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("Regular user created: {}", defaultUserEmail);
    }
}
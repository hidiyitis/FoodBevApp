package com.foodbev.FoodBevApp;

import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    // Inject melalui constructor untuk menghindari circular dependency
    @Autowired
    public DataInitializer(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (userRepository.findByEmail("admin@foodbev.com").isEmpty()) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setPhone("1234567890");
            admin.setEmail("admin@foodbev.com");
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setRole("ROLE_ADMIN");
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("Admin user created: admin@foodbev.com / password");
        }

        // Create regular user if not exists
        if (userRepository.findByEmail("user@foodbev.com").isEmpty()) {
            User user = new User();
            user.setName("Regular User");
            user.setPhone("0987654321");
            user.setEmail("user@foodbev.com");
            user.setPasswordHash(passwordEncoder.encode("password"));
            user.setRole("ROLE_USER");
            user.setIsActive(true);
            userRepository.save(user);
            System.out.println("Regular user created: user@foodbev.com / password");
        }
    }
}
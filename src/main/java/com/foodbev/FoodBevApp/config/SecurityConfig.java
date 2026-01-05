package com.foodbev.FoodBevApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import com.foodbev.FoodBevApp.constants.RoleConstants;

@Configuration
public class SecurityConfig {

        private static final String AUTH_LOGIN_URL = "/auth/login";
        private static final String AUTH_REGISTER_URL = "/auth/register";
        private static final String PAYMENT_NOTIFICATION_URL = "/payment/notification";
        private static final String USER_CART_PATTERN = "/user/cart/**";
        private static final String JSESSIONID = "JSESSIONID";

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**",
                                                                "/uploads/**")
                                                .permitAll()
                                                .requestMatchers("/", "/home").permitAll()
                                                .requestMatchers(AUTH_LOGIN_URL, AUTH_REGISTER_URL).anonymous()
                                                .requestMatchers(PAYMENT_NOTIFICATION_URL).permitAll()
                                                .requestMatchers("/payment/finish", "/payment/pending",
                                                                "/payment/error")
                                                .permitAll()
                                                .requestMatchers("/api/admin/**").hasAuthority(RoleConstants.ROLE_ADMIN)
                                                .requestMatchers("/admin/**").hasAuthority(RoleConstants.ROLE_ADMIN)
                                                .requestMatchers("/user/**")
                                                .hasAnyAuthority(RoleConstants.ROLE_USER, RoleConstants.ROLE_ADMIN)
                                                .requestMatchers(USER_CART_PATTERN).authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage(AUTH_LOGIN_URL)
                                                .loginProcessingUrl(AUTH_LOGIN_URL)
                                                .successHandler(customAuthenticationSuccessHandler())
                                                .failureUrl(AUTH_LOGIN_URL + "?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/auth/logout")
                                                .logoutSuccessUrl(AUTH_LOGIN_URL + "?logout=true")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies(JSESSIONID)
                                                .permitAll())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers(USER_CART_PATTERN, PAYMENT_NOTIFICATION_URL)
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
                return http.build();
        }

        @Bean
        public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
                return new CustomAuthenticationSuccessHandler();
        }
}
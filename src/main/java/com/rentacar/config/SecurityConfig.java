package com.rentacar.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity during development
            .authorizeHttpRequests(auth -> auth
                // Public resources - be more explicit with static resources
                .requestMatchers(
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/home"),
                    new AntPathRequestMatcher("/register"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/logout"),
                    new AntPathRequestMatcher("/error/**"),
                    new AntPathRequestMatcher("/error"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/webjars/**"),
                    new AntPathRequestMatcher("/favicon.ico"),
                    new AntPathRequestMatcher("/uploads/**"),
                    new AntPathRequestMatcher("/vehicles"),
                    new AntPathRequestMatcher("/vehicles/map"),
                    new AntPathRequestMatcher("/vehicles/{id}"),
                    new AntPathRequestMatcher("/vehicles/search"),
                    new AntPathRequestMatcher("/api/vehicles/public/**"),
                    new AntPathRequestMatcher("/test/**"),
                    new AntPathRequestMatcher("/login-error"),
                    new AntPathRequestMatcher("/api/diagnostic/**"), // Add diagnostic endpoint
                    new AntPathRequestMatcher("/fonts/**")
                ).permitAll()
                
                // Admin-only areas - use hasRole instead of hasAuthority for better consistency
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                
                // User areas
                .requestMatchers(
                    new AntPathRequestMatcher("/user/**"),
                    new AntPathRequestMatcher("/reservations/**")
                ).authenticated()
                
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login") // Changed back to /login for simplicity
                .defaultSuccessUrl("/", true)
                .failureUrl("/login-error") // Use custom error handler
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Add a handler for exceptions during authentication
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/access-denied")
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login?error=true&message=" + authException.getMessage());
                })
            );
            
        return http.build();
    }
}

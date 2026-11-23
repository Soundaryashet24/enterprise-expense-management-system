
package com.soundarya.enterpriseexpensemanagementsystem.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.soundarya.enterpriseexpensemanagementsystem.security.JwtRoleConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtRoleConverter jwtRoleConverter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/auth/login")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/auth/me")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET,
                                "/employees", "/employees/*")
                        .hasRole("MANAGER")

                        .requestMatchers("/employees/**")
                        .authenticated()

                        .requestMatchers("/expenses", "/expenses/**")
                        .authenticated()

                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtRoleConverter)));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            @Value("${app.auth.employee.username}") String employeeUsername,
            @Value("${app.auth.employee.password}") String employeePassword,
            @Value("${app.auth.manager.username}") String managerUsername,
            @Value("${app.auth.manager.password}") String managerPassword) {

        UserDetails employee = User.builder()
                .username(employeeUsername)
                .password(encoder.encode(employeePassword))
                .roles("EMPLOYEE")
                .build();

        UserDetails manager = User.builder()
                .username(managerUsername)
                .password(encoder.encode(managerPassword))
                .roles("MANAGER")
                .build();

        return new InMemoryUserDetailsManager(employee, manager);
    }

    @Bean
    AuthenticationManager authenticationManager(
            UserDetailsService users,
            PasswordEncoder encoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(users);

        provider.setPasswordEncoder(encoder);

        return new ProviderManager(provider);
    }

    @Bean
    JwtEncoder jwtEncoder(
            @Value("${app.jwt.secret}") String secret) {

        byte[] key = Base64.getDecoder().decode(secret);

        if (key.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 bytes");
        }

        return new NimbusJwtEncoder(
                new ImmutableSecret<>(key));
    }

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${app.jwt.secret}") String secret) {

        byte[] key = Base64.getDecoder().decode(secret);

        if (key.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 bytes");
        }

        return NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(key, "HmacSHA256"))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}

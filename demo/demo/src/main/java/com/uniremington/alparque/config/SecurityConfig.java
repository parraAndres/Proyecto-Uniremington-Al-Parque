package com.uniremington.alparque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uniremington.alparque.security.JwtFilter;
import com.uniremington.alparque.security.JwtUtil;
import com.uniremington.alparque.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil) {
        return new JwtFilter(jwtUtil);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsServiceImpl userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
                .disable())
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // ✅ Documentación Swagger + H2 Console (público)
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                // ✅ Autenticación (público)
                .requestMatchers("/api/auth/**").permitAll()
                // ✅ Sincronización (público)
                .requestMatchers("/api/sincronizacion/**").permitAll()
                // ✅ CRUD por rol
                .requestMatchers("/api/beneficiarios/**").hasRole("ADMIN")
                .requestMatchers("/api/estudiantes/**").hasRole("ADMIN")
                .requestMatchers("/api/servicios/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/diagnosticos/**").hasRole("ADMIN")
                .requestMatchers("/api/recursos/**").hasRole("ADMIN")
                .requestMatchers("/api/seguimientos/**").hasRole("ADMIN")
                .requestMatchers("/api/indicadores/**").hasAnyRole("ADMIN", "USER")
                // ✅ Resto requiere autenticación
                .anyRequest().authenticated()
            );
        return http.build();
    }
}

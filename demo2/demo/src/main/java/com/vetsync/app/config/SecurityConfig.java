package com.vetsync.app.config;

import com.vetsync.app.entity.Usuario;
import com.vetsync.app.repository.UsuarioRepository;
import com.vetsync.app.security.filters.JwtAuthFilter;
import com.vetsync.app.uniremington.security.UniJwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UniJwtAuthFilter uniJwtAuthFilter;
    private final UsuarioRepository usuarioRepository;

    // ── UserDetailsService de VetSync (por email) ────────────────────

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .roles(usuario.getRol().name())
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── Cadena de seguridad unificada ───────────────────────────────

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Rutas públicas ─────────────────────────────────
                .requestMatchers(
                    // Auth de VetSync y Uniremington (ambos bajo /auth/**)
                    "/auth/**",
                    "/usuarios/register",
                    // Swagger / OpenAPI
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // ── Rutas Uniremington (requieren JWT Uniremington) ─
                .requestMatchers("/sync/**").authenticated()

                // ── Rutas VetSync por rol ──────────────────────────
                .requestMatchers("/finanzas/**", "/usuarios/**", "/dashboard/admin").hasRole("ADMIN")
                .requestMatchers("/dashboard/**").authenticated()
                .requestMatchers("/citas/**", "/historias/**", "/formulas/**").hasAnyRole("ADMIN","VETERINARIO")
                .requestMatchers("/inventario/**").hasAnyRole("ADMIN","FARMACEUTICO")
                .requestMatchers("/clientes/**", "/mascotas/**", "/planes/**").hasAnyRole("ADMIN","VETERINARIO","AUXILIAR")
                .requestMatchers("/facturas/**", "/pagos/**").hasAnyRole("ADMIN","VETERINARIO")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            // El filtro de Uniremington va ANTES del de VetSync para manejar /sync/**
            .addFilterBefore(uniJwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── CORS global ──────────────────────────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        // Origen del frontend Angular (PWA Uniremington + VetSync)
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

package com.uniremington.alparque.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Map<String, UserDetails> users;

    public UserDetailsServiceImpl() {
        this.users = new HashMap<>();
        // Usuarios de demostración (en producción, cargar de BD)
        users.put("admin", User.builder()
            .username("admin")
            .password(new BCryptPasswordEncoder().encode("admin123"))
            .roles("ADMIN")
            .build());
        users.put("user", User.builder()
            .username("user")
            .password(new BCryptPasswordEncoder().encode("user123"))
            .roles("USER")
            .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.computeIfAbsent(username, k -> {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        });
    }
}

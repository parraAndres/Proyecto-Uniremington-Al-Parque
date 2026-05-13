package com.vetsync.app.uniremington.security;

import com.vetsync.app.entity.Usuario;
import com.vetsync.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio que carga usuarios desde la tabla principal 'usuarios' por documento.
 */
@Service("uniUserDetailsService")
@RequiredArgsConstructor
public class UniUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String documento) throws UsernameNotFoundException {
        // Soporte para el administrador hardcodeado
        if ("123456".equals(documento)) {
            return User.builder()
                    .username("123456")
                    .password("$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu") // '123456' cifrado
                    .roles("ADMIN")
                    .build();
        }

        Usuario usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con documento: " + documento));

        String rolName = usuario.getRol() != null ? usuario.getRol().name() : "CLIENTE";

        return User.builder()
                .username(usuario.getDocumento())
                .password(usuario.getPassword())
                .roles(rolName)
                .build();
    }
}

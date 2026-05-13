package com.vetsync.app.uniremington.security;

import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio que carga usuarios desde la tabla 'uni_usuarios' por documento.
 * Verifica si el usuario está activo para permitir el acceso.
 */
@Service("uniUserDetailsService")
@RequiredArgsConstructor
public class UniUserDetailsService implements UserDetailsService {

    private final UsuarioUniremingtonRepository usuarioRepository;

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

        UsuarioUniremington usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con documento: " + documento));

        if (!usuario.isActivo()) {
            throw new DisabledException("La cuenta del usuario " + documento + " está bloqueada");
        }

        String rolName = usuario.getRol() != null ? usuario.getRol().toUpperCase() : "BENEFICIARIO";

        return User.builder()
                .username(usuario.getDocumento())
                .password(usuario.getPassword())
                .roles(rolName)
                .build();
    }
}

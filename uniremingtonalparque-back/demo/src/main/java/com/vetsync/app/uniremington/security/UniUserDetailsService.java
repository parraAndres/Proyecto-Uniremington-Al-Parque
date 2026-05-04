package com.vetsync.app.uniremington.security;

import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio que carga usuarios Uniremington por documento para Spring Security.
 * El "username" en el contexto de Spring Security es el número de documento.
 */
@Service("uniUserDetailsService")
@RequiredArgsConstructor
public class UniUserDetailsService implements UserDetailsService {

    private final UsuarioUniremingtonRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String documento) throws UsernameNotFoundException {
        UsuarioUniremington usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario Uniremington no encontrado con documento: " + documento));

        return User.builder()
                .username(usuario.getDocumento())
                .password(usuario.getPassword())
                .roles("UNIREMINGTON")
                .build();
    }
}

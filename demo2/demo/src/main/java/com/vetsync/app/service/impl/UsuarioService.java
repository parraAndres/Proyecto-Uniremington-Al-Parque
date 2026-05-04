package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.RegisterRequest;
import com.vetsync.app.dto.request.UpdatePerfilRequest;
import com.vetsync.app.dto.response.PerfilResponse;
import com.vetsync.app.entity.Usuario;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilResponse getPerfil(String email) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNotFoundException("Usuario no encontrado: " + email));
        return toResponse(u);
    }

    @Transactional
    public PerfilResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ReglaDeNegocioException("El email ya está registrado: " + request.getEmail());
        }
        Usuario u = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .activo(true)
                .build();
        return toResponse(usuarioRepository.save(u));
    }

    @Transactional
    public PerfilResponse updatePerfil(String email, UpdatePerfilRequest request) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNotFoundException("Usuario no encontrado"));
        u.setNombre(request.getNombre());
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        return toResponse(usuarioRepository.save(u));
    }

    public List<PerfilResponse> findAll() {
        return usuarioRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private PerfilResponse toResponse(Usuario u) {
        return PerfilResponse.builder()
                .id(u.getId()).email(u.getEmail()).nombre(u.getNombre())
                .rol(u.getRol()).activo(u.isActivo()).creadoEn(u.getCreadoEn())
                .build();
    }
}

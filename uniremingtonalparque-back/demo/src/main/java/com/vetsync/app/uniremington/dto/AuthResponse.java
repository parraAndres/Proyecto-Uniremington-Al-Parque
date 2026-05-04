package com.vetsync.app.uniremington.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta de autenticación con token JWT y datos del usuario. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String documento;
    private String nombreCompleto;
    private String facultad;
    private String programa;
    private String tipo;      // "Bearer"
    private long expiresIn;   // milisegundos de expiración
}

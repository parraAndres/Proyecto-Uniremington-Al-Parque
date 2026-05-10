package com.vetsync.app.dto.response;

import com.vetsync.app.entity.Usuario;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PerfilResponse {
    private Long id;
    private String email;
    private String nombre;
    private Usuario.Rol rol;
    private boolean activo;
    private LocalDateTime creadoEn;
}

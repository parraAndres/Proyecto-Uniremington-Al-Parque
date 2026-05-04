# 🏫 Uniremington al Parque — Guía de API REST

## Base URL
```
http://localhost:8080/api
```

## Swagger UI
```
http://localhost:8080/api/swagger-ui.html
```

---

## 🔐 Autenticación

### POST `/auth/register` — Registrar usuario
**Body JSON:**
```json
{
  "documento": "1234567890",
  "nombreCompleto": "Juan Pérez López",
  "facultad": "Ingeniería de Sistemas",
  "programa": "Ingeniería de Software",
  "password": "MiPassword2025!"
}
```
**Respuesta exitosa (201):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "documento": "1234567890",
  "nombreCompleto": "Juan Pérez López",
  "facultad": "Ingeniería de Sistemas",
  "programa": "Ingeniería de Software",
  "tipo": "Bearer",
  "expiresIn": 86400000
}
```

---

### POST `/auth/login` — Iniciar sesión
**Body JSON:**
```json
{
  "documento": "1234567890",
  "password": "MiPassword2025!"
}
```
**Respuesta exitosa (200):** _Mismo formato que register_

> **Nota para el frontend:** Almacenar el campo `token` en localStorage/IndexedDB y enviarlo en todas las solicitudes como header:
> ```
> Authorization: Bearer <token>
> ```

---

## 🔄 Sincronización (requieren JWT en header)

> Todos los endpoints de `/sync/*` reciben un **arreglo** de objetos.
> Responden con un arreglo de `SyncItemResult` indicando el status de cada item:
> - `"CREATED"` — registro nuevo insertado
> - `"UPDATED"` — registro existente actualizado (upsert)
> - `"ERROR"` — error al procesar ese item específico

---

### POST `/sync/beneficiaries` — Sincronizar Beneficiarios
**Headers:** `Authorization: Bearer <token>`
**Body JSON:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "María García",
    "documento": "98765432",
    "edad": 35,
    "genero": "Femenino",
    "telefono": "3001234567",
    "municipio": "Medellín",
    "barrio": "El Poblado",
    "tipoPoblacion": "Adulto Mayor",
    "servicioSolicitado": "Odontología",
    "autorizaDatos": true,
    "fechaRegistro": "2026-05-03T10:00:00Z"
  }
]
```
**Respuesta (200):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "status": "CREATED",
    "message": "Registro creado"
  }
]
```

---

### POST `/sync/servicios` — Sincronizar Servicios Prestados
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "beneficiarioId": "550e8400-e29b-41d4-a716-446655440000",
    "tipoServicio": "Consulta odontológica",
    "facultadResponsable": "Odontología",
    "descripcion": "Limpieza dental completa",
    "tiempoAtencion": 45,
    "resultado": "Satisfactorio",
    "fechaAtencion": "2026-05-03T11:00:00Z"
  }
]
```

---

### POST `/sync/seguimientos`
```json
[
  {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "beneficiarioId": "550e8400-e29b-41d4-a716-446655440000",
    "estadoCaso": "ABIERTO",
    "evolucion": "Paciente en seguimiento mensual",
    "observaciones": "Requiere control en 30 días",
    "fechaSeguimiento": "2026-05-03T12:00:00Z",
    "datosExtra": "{\"prioridad\": \"alta\"}"
  }
]
```

---

### POST `/sync/diagnosticos`
```json
[
  {
    "id": "880e8400-e29b-41d4-a716-446655440003",
    "beneficiarioId": "550e8400-e29b-41d4-a716-446655440000",
    "tipo": "Nutricional",
    "descripcion": "Desnutrición leve",
    "datos": "{\"imc\": 18.2, \"peso\": 55, \"talla\": 1.74}",
    "fechaDiagnostico": "2026-05-03T10:30:00Z"
  }
]
```

---

### POST `/sync/academico`
```json
[
  {
    "id": "990e8400-e29b-41d4-a716-446655440004",
    "estudianteId": "EST-001",
    "nombreEstudiante": "Carlos Ruiz",
    "programa": "Odontología",
    "facultad": "Ciencias de la Salud",
    "horasReportadas": 8.5,
    "fechaActividad": "2026-05-03",
    "tipoParticipacion": "Atención directa"
  }
]
```

---

### POST `/sync/recursos`
```json
[
  {
    "id": "aa0e8400-e29b-41d4-a716-446655440005",
    "tipoAporte": "ESPECIE",
    "fuente": "Donación externa",
    "aportante": "Fundación XYZ",
    "descripcion": "Kit de higiene oral",
    "cantidad": 50,
    "unidadMedida": "unidades",
    "fechaRegistro": "2026-05-03T09:00:00Z",
    "facultadAsociada": "Odontología"
  }
]
```

---

## 🚨 Manejo de Errores

| HTTP | Código | Causa |
|------|--------|-------|
| 400 | `VALIDATION_ERROR` | Campos requeridos faltantes/inválidos |
| 400 | `CONFLICT` | Documento de usuario ya registrado |
| 401 | `INVALID_CREDENTIALS` | Contraseña incorrecta |
| 404 | `USER_NOT_FOUND` | Usuario con ese documento no existe |
| 500 | `INTERNAL_ERROR` | Error inesperado del servidor |

**Formato de error:**
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Error de validación en los campos",
  "details": {
    "documento": "El documento es obligatorio",
    "password": "La contraseña es obligatoria"
  },
  "timestamp": "2026-05-03T17:00:00"
}
```

---

## 🔑 JWT Payload

El token retornado contiene los siguientes claims:
```json
{
  "sub": "1234567890",
  "facultad": "Ingeniería de Sistemas",
  "programa": "Ingeniería de Software",
  "nombreCompleto": "Juan Pérez López",
  "iat": 1746295200,
  "exp": 1746381600
}
```

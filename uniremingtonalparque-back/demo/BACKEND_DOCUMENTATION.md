# Uniremington al Parque - Backend Documentation

## Objetivo
Diseñar una API RESTful offline-first para el proyecto **Uniremington al Parque**, enfocada en sincronización resiliente, analítica social y trazabilidad operativa.

## Stack Recomendado
- Spring Boot 3.x + Java 17
- PostgreSQL 15 (transaccional, reportes y consistencia)
- Spring Security + JWT
- Hibernate Validator (`jakarta.validation`)
- Flyway para versionado de esquema
- OpenAPI/Swagger para contrato API

---

## Arquitectura Backend

Arquitectura en capas:
1. **Controllers**: endpoints REST.
2. **Services**: reglas de negocio, deduplicación y sincronización.
3. **Repositories**: acceso a datos.
4. **Domain/Entities + DTOs**: contrato y modelo persistente.
5. **Security & Validation**: JWT, roles, filtros y manejo de errores.

Patrones operativos:
- **Idempotencia por evento/lote** para integraciones offline.
- **Last-Write-Wins por timestamp** para resolver conflicto de actualización.
- **Auditoría mínima** (`created_at`, `updated_at`, `created_by`).

---

## API Contract (v1)

Base path sugerido: `/api/v1`

### 1) Sincronización Offline

`POST /api/v1/sync`

Propósito:
- Recibir arreglos de registros capturados por la PWA sin conexión.
- Procesar en bloque (`bulk insert/update`) de manera idempotente.
- Evitar duplicidad usando `idempotencyKey` y `clientTimestamp`.

Body ejemplo:
```json
{
  "batchId": "e73a8747-4e74-42d8-953a-8a7a8f6f2783",
  "source": "pwa-android",
  "submittedAt": "2026-04-30T18:30:00Z",
  "records": [
    {
      "entityType": "BENEFICIARIO",
      "operation": "UPSERT",
      "idempotencyKey": "BEN-1001-20260430-01",
      "clientTimestamp": "2026-04-30T13:00:00Z",
      "payload": {
        "documento": "1032456789",
        "nombres": "Ana Lucía",
        "apellidos": "Rojas",
        "telefono": "3001234567",
        "consentimientoDatos": true
      }
    }
  ]
}
```

Reglas de sincronización:
- Si `idempotencyKey` ya existe, el registro se marca **DUPLICATE**.
- Si la entidad existe y el `clientTimestamp` es más antiguo o igual al último aplicado, se ignora como **STALE_EVENT**.
- Si el timestamp es más reciente, se aplica actualización (**Last-Write-Wins**).

Respuesta ejemplo:
```json
{
  "batchId": "e73a8747-4e74-42d8-953a-8a7a8f6f2783",
  "status": "PARTIAL_SUCCESS",
  "summary": {
    "received": 1,
    "processed": 1,
    "duplicates": 0,
    "stale": 0,
    "errors": 0
  },
  "results": [
    {
      "idempotencyKey": "BEN-1001-20260430-01",
      "status": "PROCESSED",
      "message": "Registro aplicado correctamente"
    }
  ]
}
```

### 2) Módulos de Dominio

- `GET|POST|PUT /beneficiarios`
- `GET|POST|PUT /servicios`
- `GET|POST|PUT /seguimientos`
- `GET|POST|PUT /academico/participaciones`
- `GET|POST|PUT /recursos`

### 3) Analítica (Panel de Control)

- `GET /api/v1/analytics/facultades`
  - atenciones, beneficiarios únicos, horas estudiante por facultad.
- `GET /api/v1/analytics/cobertura-territorial`
  - cobertura por municipio/barrio/comuna.
- `GET /api/v1/analytics/problematicas-frecuentes`
  - ranking de problemáticas por periodo y territorio.
- `GET /api/v1/analytics/resumen`
  - KPIs consolidados para dashboard ejecutivo.

Filtros transversales sugeridos:
- `fechaInicio`, `fechaFin`, `facultad`, `municipio`, `programa`.

---

## Modelo de Datos (SQL propuesto)

### Tablas principales

```sql
CREATE TABLE beneficiario (
  id BIGSERIAL PRIMARY KEY,
  documento VARCHAR(30) UNIQUE NOT NULL,
  nombres VARCHAR(120) NOT NULL,
  apellidos VARCHAR(120) NOT NULL,
  fecha_nacimiento DATE,
  genero VARCHAR(20),
  telefono VARCHAR(30),
  email VARCHAR(120),
  direccion VARCHAR(180),
  municipio VARCHAR(100) NOT NULL,
  barrio VARCHAR(100),
  latitud NUMERIC(10,7),
  longitud NUMERIC(10,7),
  consentimiento_datos BOOLEAN NOT NULL DEFAULT FALSE,
  fecha_consentimiento TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE servicio (
  id BIGSERIAL PRIMARY KEY,
  beneficiario_id BIGINT NOT NULL REFERENCES beneficiario(id),
  facultad VARCHAR(80) NOT NULL,
  tipo_servicio VARCHAR(80) NOT NULL,
  resultado_atencion VARCHAR(80),
  fecha_servicio TIMESTAMP NOT NULL,
  observaciones TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE seguimiento (
  id BIGSERIAL PRIMARY KEY,
  beneficiario_id BIGINT NOT NULL REFERENCES beneficiario(id),
  caso_id VARCHAR(60) NOT NULL,
  estado_caso VARCHAR(20) NOT NULL CHECK (estado_caso IN ('ABIERTO','CERRADO')),
  evolucion TEXT,
  fecha_estado TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE academico_participacion (
  id BIGSERIAL PRIMARY KEY,
  estudiante_id VARCHAR(60) NOT NULL,
  nombre_estudiante VARCHAR(150) NOT NULL,
  programa VARCHAR(120) NOT NULL,
  facultad VARCHAR(80) NOT NULL,
  horas_reportadas NUMERIC(8,2) NOT NULL CHECK (horas_reportadas >= 0),
  fecha_actividad DATE NOT NULL,
  tipo_participacion VARCHAR(80),
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE recurso (
  id BIGSERIAL PRIMARY KEY,
  tipo_aporte VARCHAR(20) NOT NULL CHECK (tipo_aporte IN ('DINERO','ESPECIE')),
  fuente VARCHAR(120) NOT NULL,
  aportante VARCHAR(150) NOT NULL,
  descripcion TEXT,
  valor_monetario NUMERIC(14,2),
  unidad_medida VARCHAR(40),
  cantidad NUMERIC(12,2),
  fecha_registro TIMESTAMP NOT NULL,
  facultad_asociada VARCHAR(80),
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### Tablas de sincronización

```sql
CREATE TABLE sync_batch (
  id BIGSERIAL PRIMARY KEY,
  batch_id UUID UNIQUE NOT NULL,
  source VARCHAR(80) NOT NULL,
  submitted_at TIMESTAMP NOT NULL,
  received_at TIMESTAMP NOT NULL DEFAULT NOW(),
  status VARCHAR(30) NOT NULL
);

CREATE TABLE sync_event (
  id BIGSERIAL PRIMARY KEY,
  batch_id UUID NOT NULL REFERENCES sync_batch(batch_id),
  entity_type VARCHAR(40) NOT NULL,
  idempotency_key VARCHAR(120) UNIQUE NOT NULL,
  client_timestamp TIMESTAMP NOT NULL,
  applied_at TIMESTAMP,
  status VARCHAR(30) NOT NULL,
  error_detail TEXT
);

CREATE INDEX idx_servicio_facultad_fecha ON servicio (facultad, fecha_servicio);
CREATE INDEX idx_beneficiario_municipio ON beneficiario (municipio);
CREATE INDEX idx_seguimiento_estado_fecha ON seguimiento (estado_caso, fecha_estado);
CREATE INDEX idx_academico_facultad_fecha ON academico_participacion (facultad, fecha_actividad);
```

---

## Seguridad y Calidad

### JWT
- `POST /api/v1/auth/login` devuelve `accessToken` con expiración corta (ej. 4h).
- Filtro JWT valida firma, expiración y roles.
- Endpoints de escritura (`POST`, `PUT`, `/sync`) requieren token válido.

### Roles sugeridos
- `ADMIN`: acceso total.
- `OPERADOR`: CRUD operativo + sync.
- `ANALISTA`: acceso de solo lectura a analytics y consultas.

### Validación obligatoria (middleware)
- Validación de request con `@Valid`.
- Errores uniformes `400` para campos faltantes.
- Reglas mínimas:
  - `consentimientoDatos` requerido para registrar beneficiario.
  - `facultad` y `fechaServicio` requeridos en servicios.
  - `estadoCaso` requerido en seguimiento.
  - `horasReportadas >= 0` en académico.
  - `tipoAporte` requerido en recursos.

Formato de error recomendado:
```json
{
  "timestamp": "2026-04-30T18:40:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Existen campos obligatorios sin diligenciar",
  "errors": [
    { "field": "records[0].idempotencyKey", "message": "no debe estar vacío" }
  ]
}
```

---

## Consultas de Analítica (referencia)

Top problemáticas:
```sql
SELECT s.tipo_servicio AS problematica, COUNT(*) AS total
FROM servicio s
WHERE s.fecha_servicio BETWEEN :inicio AND :fin
GROUP BY s.tipo_servicio
ORDER BY total DESC
LIMIT 10;
```

Cobertura territorial:
```sql
SELECT b.municipio, COUNT(DISTINCT b.id) AS beneficiarios_unicos, COUNT(s.id) AS total_atenciones
FROM beneficiario b
LEFT JOIN servicio s ON s.beneficiario_id = b.id
  AND s.fecha_servicio BETWEEN :inicio AND :fin
GROUP BY b.municipio
ORDER BY total_atenciones DESC;
```

---

## Recomendaciones de implementación
- Versionar contrato en `/api/v1`.
- Añadir `requestId` por petición para trazabilidad.
- Configurar reintentos del lado PWA solo para errores `5xx`.
- Incluir pruebas de concurrencia en `/sync` para verificar idempotencia real.

---

**Versión:** 1.1.0  
**Última actualización:** Abril 2026  
**Autor:** Equipo Uniremington al Parque

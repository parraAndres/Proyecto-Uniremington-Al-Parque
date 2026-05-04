# Uniremington al Parque — Documento de Diseño Backend

## Objetivo de la API
Diseñar un backend RESTful para operación social offline con sincronización segura, control de duplicados y analítica para panel de control institucional.

## Requisitos implementados en el diseño

### 1) Sincronización y resiliencia
- Endpoint principal: `POST /api/v1/sync`.
- El endpoint recibe arreglos (`records[]`) enviados desde almacenamiento local PWA.
- Cada registro incluye:
  - `idempotencyKey`
  - `clientTimestamp`
  - `entityType`
  - `operation`
  - `payload`
- Estrategia anti-duplicidad:
  - deduplicación por `idempotencyKey` única.
  - resolución de conflictos por timestamp con **Last-Write-Wins**.
- Respuesta de lote con conteos (`processed`, `duplicates`, `stale`, `errors`).

### 2) Modelo de datos (SQL recomendado)
Entidades base:
- `beneficiario`: datos personales, ubicación y consentimiento de datos.
- `servicio`: relación con beneficiario, facultad responsable y resultado de atención.
- `seguimiento`: historial y estados de caso (`ABIERTO`/`CERRADO`) con evolución.
- `academico_participacion`: horas y participación de estudiantes por programa/facultad.
- `recurso`: inversiones y aportes en dinero o especie.

Soporte de sincronización:
- `sync_batch`: cabecera por lote recibido.
- `sync_event`: detalle por ítem procesado.

### 3) Analítica para panel de control
Endpoints de agregación:
- `GET /api/v1/analytics/resumen`
- `GET /api/v1/analytics/facultades`
- `GET /api/v1/analytics/cobertura-territorial`
- `GET /api/v1/analytics/problematicas-frecuentes`

Dimensiones de análisis:
- facultad
- territorio (municipio/barrio/comuna)
- período (rango de fechas)
- tipo de problemática/servicio

### 4) Seguridad y calidad
- Autenticación JWT (`POST /api/v1/auth/login`).
- Protección de endpoints de escritura con token y roles.
- Middleware de validación (`@Valid`) para campos obligatorios antes de persistir.
- Formato estándar de errores de validación (`400 VALIDATION_ERROR`).

## Contrato mínimo de `POST /api/v1/sync`
```json
{
  "batchId": "uuid",
  "source": "pwa-web",
  "submittedAt": "2026-04-30T18:30:00Z",
  "records": [
    {
      "entityType": "BENEFICIARIO",
      "operation": "UPSERT",
      "idempotencyKey": "BEN-123-001",
      "clientTimestamp": "2026-04-30T13:00:00Z",
      "payload": {}
    }
  ]
}
```

## Notas de implementación recomendadas
- Versionar API en `/api/v1`.
- Registrar auditoría base (`created_at`, `updated_at`, `created_by`).
- Índices por fecha/facultad/territorio para acelerar analítica.
- Pruebas obligatorias: idempotencia, concurrencia y validaciones.

---

Para el detalle técnico completo (DDL SQL, ejemplos de respuesta y consultas de agregación), ver:
- `demo/demo/BACKEND_DOCUMENTATION.md`

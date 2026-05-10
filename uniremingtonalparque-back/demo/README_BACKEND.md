# 🎓 Uniremington al Parque - Backend

Backend RESTful orientado a impacto social con operación **offline-first** para jornadas y seguimiento territorial.

## Enfoque Arquitectónico
- API versionada en `/api/v1`.
- Endpoint principal de sincronización: `POST /api/v1/sync`.
- Persistencia SQL (PostgreSQL) con tablas operativas y tablas de sincronización.
- Seguridad por JWT + control de acceso por roles.
- Validación obligatoria de payloads con `@Valid`.

## Módulos de Dominio
- **Beneficiarios**: datos personales, ubicación y consentimiento de datos.
- **Servicios**: atención prestada, facultad responsable y resultado.
- **Seguimiento**: evolución del caso y estado (abierto/cerrado).
- **Académico**: horas y participación estudiantil por programa.
- **Recursos**: aportes en dinero/especie de universidad y aliados.
- **Analítica**: agregaciones para panel de control.

## Endpoints Clave
```http
POST /api/v1/auth/login
POST /api/v1/sync

GET  /api/v1/analytics/resumen
GET  /api/v1/analytics/facultades
GET  /api/v1/analytics/cobertura-territorial
GET  /api/v1/analytics/problematicas-frecuentes
```

## Sincronización (`POST /api/v1/sync`)
Reglas operativas:
- Ingesta por lotes (`records[]`) enviada desde la PWA.
- Deduplicación por `idempotencyKey`.
- Resolución de conflictos por `clientTimestamp` (Last-Write-Wins).
- Resumen por lote: `processed`, `duplicates`, `stale`, `errors`.

Consultar detalles técnicos completos, contrato JSON y esquema SQL en:
- [`BACKEND_DOCUMENTATION.md`](./BACKEND_DOCUMENTATION.md)

## Seguridad y Calidad
- JWT requerido para endpoints protegidos.
- Roles sugeridos: `ADMIN`, `OPERADOR`, `ANALISTA`.
- Middleware de validación para campos obligatorios.
- Formato estándar de errores de validación (`400 VALIDATION_ERROR`).

## Inicio Rápido
### Requisitos
- Java 17+
- Maven 3.8+
- PostgreSQL 13+

### Ejecutar
```bash
mvn clean compile
mvn spring-boot:run
```

### Swagger/OpenAPI
`http://localhost:8080/swagger-ui.html`

---

**Versión:** 1.1.0  
**Última actualización:** Abril 2026

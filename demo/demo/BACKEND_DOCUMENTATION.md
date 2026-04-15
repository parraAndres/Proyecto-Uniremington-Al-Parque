# Uniremington al Parque - Backend Documentation

## 📋 Project Overview
Sistema de gestión de jornadas sociales para Uniremington, diseñado con arquitectura por capas y sincronización offline robusto.

**Stack Tecnológico:**
- Spring Boot 3.4.1 con Java 17
- Spring Data JPA + PostgreSQL (persistencia)
- Spring Security + JWT (autenticación)
- Spring Batch (procesamiento asincrónico)
- MapStruct (mapeo DTO/entidades)
- Swagger/OpenAPI (documentación API)

---

## 🏗️ Arquitectura

### Patrón por Capas
```
┌─────────────────────────────────────┐
│   Controllers (REST endpoints)       │
├─────────────────────────────────────┤
│   Services (lógica de negocio)      │
├─────────────────────────────────────┤
│   Repositories (acceso a datos)     │
├─────────────────────────────────────┤
│   Entities + DTOs + Mappers         │
├─────────────────────────────────────┤
│   Database (PostgreSQL + Flyway)    │
└─────────────────────────────────────┘
```

### Componentes Clave

#### 1. **Autenticación + Autorización (JWT)**
- **JwtUtil:** Generación y validación de tokens con claims (username, roles)
- **JwtFilter:** Intercepta requests, extrae y valida tokens
- **SecurityConfig:** Configuración de roles y acceso por endpoint
- **AuthController:** POST `/api/auth/login` con credenciales

**Usuarios de Prueba:**
```
admin / admin123    (rol: ADMIN)
user / user123      (rol: USER)
```

**Token Expiration:** 24 horas

#### 2. **CRUD Principales (6 módulos)**
Cada módulo sigue el patrón:
- **Entity** → Modelo JPA con anotaciones de validación
- **DTO Request/Response** → Contrato de entrada/salida
- **Mapper** → Transformación Entity ↔ DTO (MapStruct)
- **Repository** → Acceso a datos (Spring Data JPA)
- **Service** → Lógica de negocio + validaciones
- **Controller** → REST endpoints

**Módulos:**
1. **Beneficiario** - Receptores de servicios sociales
   - Validación: Documento único por registor
   - Endpoints: CRUD + filtros por facultad/municipio

2. **Servicio** - Servicios prestados a beneficiarios
   - Relación: Muchos servicios → 1 beneficiario
   - Filtro: Por rango de fechas

3. **Estudiante** - Estudiantes participantes en actividades
   - Validación: Documento único con deduplicación
   - Registro: Con facultad y programa

4. **Diagnóstico** - Diagnósticos de problematica
   - Campos: Municipio, problemática, clasificación, prioridad
   - Historial con fechaRegistro

5. **Recurso** - Aportes y recursos invertidos
   - Tipos: Económicos, físicos, logísticos
   - Tracking: Valor, fuente aporte, descripción

6. **Seguimiento** - Seguimiento a casos
   - Relación: Vinculado a Caso
   - Registro: Avances y fecha programada

#### 3. **Indicadores (Analytics)**
- **GET /api/indicadores/resumen** - Resumen global (beneficiarios, servicios, estudiantes)
- **GET /api/indicadores/rango?fechaInicio=&fechaFin=** - Filtro por rango de fechas
- **GET /api/indicadores/tendencia** - Tendencia mensual de servicios
- **GET /api/indicadores/por-facultad** - Agregación por facultad de estudiantes

#### 4. **Sincronización Offline (Batch Robusto)**
Contrato para ingesta batch de datos cuando no hay internet:

**Especificación:**
- **Idempotency:** Cada item tiene `claveIdempotencia` única
- **Deduplicación:** Si clave ya existe, se marca como duplicado
- **Last-Write-Wins:** Última actualización prevalece
- **Persistencia:** Lotes guardados para auditoría + historial

**Endpoints:**
```
POST /api/sincronizacion/batch
└─ Body: SincronizacionBatchRequestDTO
   ├─ loteId: UUID del batch
   ├─ items[]: Array de operaciones (CREATE/UPDATE)
   │  ├─ tipo: "BENEFICIARIO", "SERVICIO", "ESTUDIANTE", etc.
   │  ├─ claveIdempotencia: ID único para deduplicación
   │  ├─ operacion: "CREATE" | "UPDATE"
   │  └─ datos: JSON con atributos de la entidad
   └─ Respuesta:
      ├─ loteId: UUID procesado
      ├─ resultados[]: Estado de cada item
      │  ├─ claveIdempotencia
      │  ├─ operacion
      │  ├─ estado: "EXITOSO", "DUPLICADO", "ERROR"
      │  └─ mensaje
      └─ resumen:
         ├─ exitosos: Contador
         ├─ duplicados: Contador
         ├─ errores: Contador

GET /api/sincronizacion/lotes
└─ Paginado: Historial de lotes con estado

GET /api/sincronizacion/lotes?fechaInicio=&fechaFin=
└─ Filtro por fechas: Auditoría temporal
```

**Ejemplo Request:**
```json
{
  "loteId": "550e8400-e29b-41d4-a716-446655440000",
  "items": [
    {
      "tipo": "BENEFICIARIO",
      "claveIdempotencia": "BEN-20260101-001",
      "operacion": "CREATE",
      "datos": {
        "nombre": "Juan Pérez",
        "documento": "1234567890",
        "facultad": "Ingeniería",
        "municipio": "Medellín"
      }
    }
  ]
}
```

---

## 🔐 Seguridad

### Autenticación
1. **POST /api/auth/login** - Obtener JWT token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresAt": "2026-04-13T22:00:00"
}
```

2. **Usar token en requests:**
```bash
curl -X GET http://localhost:8080/api/beneficiarios \
  -H "Authorization: Bearer <token>"
```

### Control de Acceso por Rol

| Endpoint | ADMIN | USER | Anonimo |
|----------|-------|------|---------|
| POST /api/auth/login | ✅ | ✅ | ✅ |
| GET /api/beneficiarios | ✅ | ✅ | ❌ |
| POST /api/beneficiarios | ✅ | ❌ | ❌ |
| DELETE /api/beneficiarios/{id} | ✅ | ❌ | ❌ |
| POST /api/sincronizacion/batch | ✅ | ✅ | ✅ |
| GET /api/indicadores/* | ✅ | ✅ | ❌ |

---

## 📨 Endpoints Principales

### Autenticación
```
POST /api/auth/login
```

### Beneficiarios
```
GET    /api/beneficiarios
POST   /api/beneficiarios
GET    /api/beneficiarios/{id}
PUT    /api/beneficiarios/{id}
DELETE /api/beneficiarios/{id}
```

### Servicios
```
GET    /api/servicios
POST   /api/servicios
GET    /api/servicios/{id}
PUT    /api/servicios/{id}
DELETE /api/servicios/{id}
GET    /api/servicios/beneficiario/{beneficiarioId}
```

### Estudiantes
```
GET    /api/estudiantes
POST   /api/estudiantes
GET    /api/estudiantes/{id}
PUT    /api/estudiantes/{id}
DELETE /api/estudiantes/{id}
```

### Diagnósticos
```
GET    /api/diagnosticos
POST   /api/diagnosticos
GET    /api/diagnosticos/{id}
PUT    /api/diagnosticos/{id}
DELETE /api/diagnosticos/{id}
```

### Recursos
```
GET    /api/recursos
POST   /api/recursos
GET    /api/recursos/{id}
PUT    /api/recursos/{id}
DELETE /api/recursos/{id}
```

### Seguimientos
```
GET    /api/seguimientos
POST   /api/seguimientos
GET    /api/seguimientos/{id}
PUT    /api/seguimientos/{id}
DELETE /api/seguimientos/{id}
GET    /api/seguimientos/caso/{casoId}
```

### Indicadores
```
GET    /api/indicadores/resumen
GET    /api/indicadores/rango?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD
GET    /api/indicadores/tendencia
GET    /api/indicadores/por-facultad
```

### Sincronización
```
POST   /api/sincronizacion/batch
GET    /api/sincronizacion/lotes
GET    /api/sincronizacion/lotes?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD
```

---

## 🧪 Tests

### Ejecutar todos los tests:
```bash
mvn test
```

### Ejecutar test específico:
```bash
mvn test -Dtest=SincronizacionControllerTest
```

### Test Coverage:
```bash
mvn clean test jacoco:report
```

**Tests Incluidos:**
- `DemoApplicationTests` - Smoke test del contexto Spring
- `SincronizacionServiceImplTest` - Lógica de sincronización batch
- `SincronizacionControllerTest` - Endpoints de sincronización

---

## 🚀 Ejecución

### Compilar
```bash
mvn clean compile
```

### Correr con Maven
```bash
mvn spring-boot:run
```

### Generar WAR para deploy
```bash
mvn clean package
```

### Puerto por defecto
```
http://localhost:8080
```

### Swagger/OpenAPI
```
http://localhost:8080/swagger-ui.html
```

---

## 🗄️ Base de Datos

### Migraciones (Flyway)
Ubicadas en: `src/main/resources/db/migration/`

```sql
V1__sync_lotes_indexes.sql
  ├─ Índices para SincronizacionLote
  ├─ Índices para SincronizacionResultadoItem
  └─ Índices para SincronizacionEvento
```

### Tabla de Sincronización
```sql
sincronizacion_lote:
  ├─ id (UUID, PK)
  ├─ lote_id (UUID, unique, businesskey)
  ├─ estado (String)
  ├─ resumen (JSON)
  └─ fecha_registro (Timestamp)

sincronizacion_resultado_item:
  ├─ id (UUID, PK)
  ├─ lote_id (FK)
  ├─ clave_idempotencia (String, unique)
  ├─ operacion (String)
  ├─ estado (String)
  └─ mensaje (Text)

sincronizacion_evento:
  ├─ id (UUID, PK)
  ├─ lote_id (FK)
  ├─ tipo_evento (String)
  ├─ descripcion (String)
  └─ fecha (Timestamp)
```

---

## 🔧 Configuración (application.properties)

```properties
# Base de Datos
spring.datasource.url=jdbc:postgresql://localhost:5432/alparque
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT
jwt.secret=tu-clave-secreta-fuerte-para-produccion
jwt.expiration=86400000  (24 horas)

# Logging
logging.level.root=INFO
logging.level.com.uniremington.alparque=DEBUG

# Flyway
spring.flyway.enabled=true
spring.flyway.baselineOnMigrate=true
```

---

## 📊 Mappers (MapStruct)

Todos los mappers automatizados con MapStruct:

```
BeneficiarioMapper → @Mapping configuraciones personalizadas
ServicioMapper → Mapeo bidireccional
EstudianteMapper → Deduplicación en toEntity()
DiagnosticoMapper → toEntity(), toResponse(), updateEntity()
RecursoMapper → toEntity(), toResponse(), updateEntity()
SeguimientoMapper → Mapeo de casoId + toEntity/toResponse/updateEntity()
```

---

## 🎯 Próximos Pasos Opcionales

1. **Frontend (React/Angular)** - Consumir estos endpoints
2. **Mobile App** - Sincronización offline en tiempo real
3. **Analytics Dashboard** - Visualizar indicadores en tiempo real
4. **Auditoría** - Log de cambios por usuario
5. **Exportación** - Excel/PDF de reportes (Apache POI configurado)

---

## 📝 Notas de Desarrollo

- **Validación:** Todas las entidades usan anotaciones `@NotBlank`, `@NotNull`
- **Transaccionalidad:** Services marcados con `@Transactional`
- **Logging:** Use SLF4J + Logback (por defecto en Spring Boot)
- **Excepciones:** GlobalExceptionHandler captura todas las excepciones
- **Versionado:** APIs sin versioning en URL (considerar `/api/v1/` a futuro)

---

**Versión:** 1.0.0  
**Última actualización:** Abril 2026
**Autor:** Equipo Uniremington al Parque

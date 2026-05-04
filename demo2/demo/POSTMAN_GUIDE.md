# Guía de Pruebas con Postman - Uniremington al Parque

## Configuración Inicial

### URL Base
```
http://localhost:8080
```

### Iniciar el servidor
```bash
cd "d:\Zulma\Escritorio\PROYECTOS UNIREMINGTON\UNIREMINGTON AL PARQUE\BACK\demo\demo"
mvn spring-boot:run
```

Si el puerto está ocupado:
```powershell
Get-Process java | Stop-Process -Force
```

---

## Paso 1: Obtener Token JWT

**POST** `http://localhost:8080/api/auth/login`

Headers:
```
Content-Type: application/json
```

Body (raw JSON):
```json
{
  "username": "admin",
  "password": "admin123"
}
```

Usuario alternativo:
```json
{
  "username": "user",
  "password": "user123"
}
```

La respuesta devuelve un `token`. Cópialo para usarlo en los demás endpoints.

---

## Paso 2: Autenticación en cada Request

En Postman, para cada endpoint:
- Pestaña **Authorization**
- Type: **Bearer Token**
- Token: pega el token obtenido en el paso anterior

---

## Endpoints por Entidad

### BENEFICIARIOS
Rol requerido: **ADMIN**

**Crear** — POST `http://localhost:8080/api/beneficiarios`
```json
{
  "nombre": "Juan Pérez",
  "tipoDocumento": "CC",
  "numeroDocumento": "1234567890",
  "email": "juan@email.com",
  "telefono": "3001234567",
  "direccion": "Calle 1 #2-3"
}
```

**Listar todos** — GET `http://localhost:8080/api/beneficiarios`

**Buscar por ID** — GET `http://localhost:8080/api/beneficiarios/1`

**Actualizar** — PUT `http://localhost:8080/api/beneficiarios/1`
```json
{
  "nombre": "Juan Pérez Actualizado",
  "tipoDocumento": "CC",
  "numeroDocumento": "1234567890",
  "email": "juan.nuevo@email.com",
  "telefono": "3009876543",
  "direccion": "Calle 5 #10-20"
}
```

**Eliminar** — DELETE `http://localhost:8080/api/beneficiarios/1`

---

### ESTUDIANTES

**Crear** — POST `http://localhost:8080/api/estudiantes`
```json
{
  "nombre": "María López",
  "codigo": "EST001",
  "programa": "Ingeniería de Sistemas",
  "semestre": 5,
  "email": "maria@uniremington.edu.co"
}
```

**Listar todos** — GET `http://localhost:8080/api/estudiantes`

**Buscar por ID** — GET `http://localhost:8080/api/estudiantes/1`

**Actualizar** — PUT `http://localhost:8080/api/estudiantes/1`
```json
{
  "nombre": "María López",
  "codigo": "EST001",
  "programa": "Ingeniería de Sistemas",
  "semestre": 6,
  "email": "maria@uniremington.edu.co"
}
```

**Eliminar** — DELETE `http://localhost:8080/api/estudiantes/1`

---

### SERVICIOS

**Crear** — POST `http://localhost:8080/api/servicios`
```json
{
  "nombre": "Orientación Psicológica",
  "descripcion": "Servicio de apoyo emocional y psicológico",
  "activo": true
}
```

**Listar todos** — GET `http://localhost:8080/api/servicios`

**Buscar por ID** — GET `http://localhost:8080/api/servicios/1`

**Actualizar** — PUT `http://localhost:8080/api/servicios/1`
```json
{
  "nombre": "Orientación Psicológica",
  "descripcion": "Servicio actualizado",
  "activo": false
}
```

**Eliminar** — DELETE `http://localhost:8080/api/servicios/1`

---

### DIAGNÓSTICOS

**Crear** — POST `http://localhost:8080/api/diagnosticos`
```json
{
  "descripcion": "Diagnóstico inicial del paciente",
  "fecha": "2026-04-12",
  "beneficiarioId": 1
}
```

**Listar todos** — GET `http://localhost:8080/api/diagnosticos`

**Buscar por ID** — GET `http://localhost:8080/api/diagnosticos/1`

**Actualizar** — PUT `http://localhost:8080/api/diagnosticos/1`
```json
{
  "descripcion": "Diagnóstico revisado",
  "fecha": "2026-04-12",
  "beneficiarioId": 1
}
```

**Eliminar** — DELETE `http://localhost:8080/api/diagnosticos/1`

---

### SEGUIMIENTOS

**Crear** — POST `http://localhost:8080/api/seguimientos`
```json
{
  "fecha": "2026-04-12",
  "observaciones": "Primera sesión de seguimiento completada",
  "beneficiarioId": 1
}
```

**Listar todos** — GET `http://localhost:8080/api/seguimientos`

**Buscar por ID** — GET `http://localhost:8080/api/seguimientos/1`

**Actualizar** — PUT `http://localhost:8080/api/seguimientos/1`
```json
{
  "fecha": "2026-04-15",
  "observaciones": "Segunda sesión realizada con avances",
  "beneficiarioId": 1
}
```

**Eliminar** — DELETE `http://localhost:8080/api/seguimientos/1`

---

### RECURSOS

**Crear** — POST `http://localhost:8080/api/recursos`
```json
{
  "nombre": "Manual de Convivencia",
  "tipo": "PDF",
  "url": "https://ejemplo.com/manual.pdf"
}
```

**Listar todos** — GET `http://localhost:8080/api/recursos`

**Buscar por ID** — GET `http://localhost:8080/api/recursos/1`

**Actualizar** — PUT `http://localhost:8080/api/recursos/1`
```json
{
  "nombre": "Manual de Convivencia Actualizado",
  "tipo": "PDF",
  "url": "https://ejemplo.com/manual-v2.pdf"
}
```

**Eliminar** — DELETE `http://localhost:8080/api/recursos/1`

---

### INDICADORES

**Listar todos** — GET `http://localhost:8080/api/indicadores`

---

## Herramientas Adicionales

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```
Permite probar los endpoints directamente desde el navegador (sin Postman).

### H2 Console (Base de datos)
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: (dejar vacío)

**Nota:** Los datos se borran cuando el servidor se detiene (base de datos en memoria).

---

## Flujo de Prueba Recomendado

1. Iniciar servidor con `mvn spring-boot:run`
2. `POST /api/auth/login` → copiar token
3. Crear un **Beneficiario** → anotar el ID devuelto (ej: `1`)
4. Crear un **Diagnóstico** usando `beneficiarioId: 1`
5. Crear un **Seguimiento** usando `beneficiarioId: 1`
6. Verificar datos en H2 Console con `SELECT * FROM BENEFICIARIO`

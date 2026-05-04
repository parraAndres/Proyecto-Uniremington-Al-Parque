-- ============================================================
--   VetSync Pro - Script de Migración v1.0
--   Motor: MySQL 8.0 InnoDB
--   Ejecutar en orden. Usar con: spring.jpa.hibernate.ddl-auto=validate
-- ============================================================

CREATE DATABASE IF NOT EXISTS VETERINARIA_DB2
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE vetsync_db;

-- -------------------------
-- TABLA: usuarios
-- -------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(80)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    nombre     VARCHAR(100) NOT NULL,
    rol        ENUM('ADMIN','VETERINARIO','FARMACEUTICO','AUXILIAR') NOT NULL,
    activo     BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: clientes
-- -------------------------
CREATE TABLE IF NOT EXISTS clientes (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    documento        VARCHAR(20)  NOT NULL UNIQUE,
    telefono         VARCHAR(15),
    direccion        VARCHAR(150),
    email            VARCHAR(80)  UNIQUE,
    fecha_registro   DATE
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: mascotas
-- -------------------------
CREATE TABLE IF NOT EXISTS mascotas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(80)  NOT NULL,
    especie         VARCHAR(50)  NOT NULL,
    raza            VARCHAR(50),
    edad            INT          NOT NULL CHECK (edad > 0),
    sexo            ENUM('MACHO','HEMBRA'),
    fecha_registro  DATE,
    cliente_id      BIGINT       NOT NULL,
    CONSTRAINT fk_mascota_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: citas
-- -------------------------
CREATE TABLE IF NOT EXISTS citas (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    mascota_id       BIGINT   NOT NULL,
    veterinario_id   BIGINT   NOT NULL,
    fecha_hora       DATETIME NOT NULL,
    motivo           VARCHAR(200),
    estado           ENUM('PROGRAMADA','EN_CURSO','COMPLETADA','CANCELADA') NOT NULL DEFAULT 'PROGRAMADA',
    CONSTRAINT fk_cita_mascota     FOREIGN KEY (mascota_id)     REFERENCES mascotas(id),
    CONSTRAINT fk_cita_veterinario FOREIGN KEY (veterinario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: historias_clinicas
-- -------------------------
CREATE TABLE IF NOT EXISTS historias_clinicas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    cita_id         BIGINT NOT NULL UNIQUE,
    anamnesis       TEXT,
    diagnostico     TEXT,
    tratamiento     TEXT,
    fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historia_cita FOREIGN KEY (cita_id) REFERENCES citas(id)
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: planes_sanitarios
-- -------------------------
CREATE TABLE IF NOT EXISTS planes_sanitarios (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    mascota_id          BIGINT      NOT NULL,
    vacuna              VARCHAR(100) NOT NULL,
    fecha_aplicacion    DATE        NOT NULL,
    proxima_aplicacion  DATE,
    estado              ENUM('VIGENTE','VENCIDA','PENDIENTE') NOT NULL DEFAULT 'VIGENTE',
    observaciones       TEXT,
    CONSTRAINT fk_plan_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id)
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: productos (inventario)
-- -------------------------
CREATE TABLE IF NOT EXISTS productos (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(20)    NOT NULL UNIQUE,
    nombre          VARCHAR(150)   NOT NULL,
    descripcion     TEXT,
    stock_actual    INT            NOT NULL,
    stock_minimo    INT            NOT NULL,
    precio          DECIMAL(10,2)  NOT NULL,
    unidad_medida   VARCHAR(50),
    activo          BOOLEAN        NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: formulas_medicas
-- -------------------------
CREATE TABLE IF NOT EXISTS formulas_medicas (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    historia_clinica_id BIGINT NOT NULL,
    veterinario_id      BIGINT NOT NULL,
    farmaceutico_id     BIGINT,
    medicamentos        TEXT   NOT NULL,
    estado              ENUM('PENDIENTE','VALIDADA','DISPENSADA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_emision       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_dispensacion  DATETIME,
    CONSTRAINT fk_formula_historia    FOREIGN KEY (historia_clinica_id) REFERENCES historias_clinicas(id),
    CONSTRAINT fk_formula_veterinario FOREIGN KEY (veterinario_id)      REFERENCES usuarios(id),
    CONSTRAINT fk_formula_farmaceut   FOREIGN KEY (farmaceutico_id)     REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: items_formula
-- -------------------------
CREATE TABLE IF NOT EXISTS items_formula (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    formula_id    BIGINT NOT NULL,
    producto_id   BIGINT NOT NULL,
    cantidad      INT    NOT NULL,
    instrucciones VARCHAR(200),
    CONSTRAINT fk_item_formula  FOREIGN KEY (formula_id)  REFERENCES formulas_medicas(id),
    CONSTRAINT fk_item_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: facturas
-- -------------------------
CREATE TABLE IF NOT EXISTS facturas (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero         VARCHAR(20)    NOT NULL UNIQUE,
    cliente_id     BIGINT         NOT NULL,
    cita_id        BIGINT,
    subtotal       DECIMAL(12,2)  NOT NULL,
    impuesto       DECIMAL(10,2),
    total          DECIMAL(12,2)  NOT NULL,
    estado         ENUM('PENDIENTE','PAGADA','ANULADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_emision  DATE,
    creado_en      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_factura_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_factura_cita    FOREIGN KEY (cita_id)    REFERENCES citas(id)
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: historial_pagos
-- -------------------------
CREATE TABLE IF NOT EXISTS historial_pagos (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    factura_id     BIGINT         NOT NULL,
    admin_id       BIGINT         NOT NULL,
    monto          DECIMAL(12,2)  NOT NULL,
    metodo_pago    ENUM('EFECTIVO','TRANSFERENCIA','TARJETA_CREDITO','TARJETA_DEBITO'),
    fecha_pago     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones  VARCHAR(200),
    CONSTRAINT fk_pago_factura FOREIGN KEY (factura_id) REFERENCES facturas(id),
    CONSTRAINT fk_pago_admin   FOREIGN KEY (admin_id)   REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -------------------------
-- TABLA: lotes_sincronizacion (Auditoría Batch)
-- -------------------------
CREATE TABLE IF NOT EXISTS lotes_sincronizacion (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    clave_idempotencia  VARCHAR(36)  NOT NULL UNIQUE,
    origen              VARCHAR(100) NOT NULL,
    total_registros     INT          NOT NULL,
    procesados          INT          NOT NULL,
    duplicados          INT          NOT NULL,
    errores             INT          NOT NULL,
    estado              ENUM('EXITOSO','DUPLICADO','ERROR','PARCIAL') NOT NULL,
    fecha_procesado     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified       DATETIME,
    detalle_error       TEXT
) ENGINE=InnoDB;

-- -------------------------
-- DATOS INICIALES - Usuario ADMIN
-- -------------------------
INSERT IGNORE INTO usuarios (email, password, nombre, rol, activo)
VALUES ('admin@vetsync.com',
        '$2a$10$kNqHTHFrXS8mUB7OhXbcPO0g5YXl1E7v6J2I.0vMoiXUFWGu9/YHW', -- password: admin123
        'Administrador VetSync', 'ADMIN', TRUE);

-- -------------------------
-- TABLA: notificaciones
-- -------------------------
CREATE TABLE IF NOT EXISTS notificaciones (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  BIGINT       NOT NULL,
    titulo      VARCHAR(200) NOT NULL,
    mensaje     TEXT,
    tipo        ENUM('INFO','ALERTA','EXITO','ERROR') NOT NULL DEFAULT 'INFO',
    leida       BOOLEAN NOT NULL DEFAULT FALSE,
    creada_en   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- ============================================================
--   Uniremington al Parque - Módulo Social Offline
-- ============================================================
CREATE TABLE IF NOT EXISTS beneficiarios_social (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    documento             VARCHAR(30) NOT NULL UNIQUE,
    nombres               VARCHAR(120) NOT NULL,
    apellidos             VARCHAR(120) NOT NULL,
    telefono              VARCHAR(30),
    direccion             VARCHAR(160),
    municipio             VARCHAR(100) NOT NULL,
    barrio                VARCHAR(100),
    consentimiento_datos  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS servicios_social (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    beneficiario_id     BIGINT NOT NULL,
    facultad            VARCHAR(80) NOT NULL,
    tipo_servicio       VARCHAR(80) NOT NULL,
    resultado_atencion  VARCHAR(80),
    fecha_servicio      DATETIME NOT NULL,
    observaciones       TEXT,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_servicio_beneficiario FOREIGN KEY (beneficiario_id) REFERENCES beneficiarios_social(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seguimientos_caso (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    beneficiario_id  BIGINT NOT NULL,
    caso_id          VARCHAR(60) NOT NULL,
    estado_caso      ENUM('ABIERTO','CERRADO') NOT NULL,
    evolucion        TEXT,
    fecha_estado     DATETIME NOT NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seguimiento_beneficiario FOREIGN KEY (beneficiario_id) REFERENCES beneficiarios_social(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS participaciones_academicas (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id      VARCHAR(60) NOT NULL,
    nombre_estudiante  VARCHAR(150) NOT NULL,
    programa           VARCHAR(120) NOT NULL,
    facultad           VARCHAR(80) NOT NULL,
    horas_reportadas   DECIMAL(8,2) NOT NULL,
    fecha_actividad    DATE NOT NULL,
    tipo_participacion VARCHAR(80),
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recursos_aporte (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_aporte        ENUM('DINERO','ESPECIE') NOT NULL,
    fuente             VARCHAR(120) NOT NULL,
    aportante          VARCHAR(150) NOT NULL,
    descripcion        TEXT,
    valor_monetario    DECIMAL(14,2),
    cantidad           DECIMAL(12,2),
    unidad_medida      VARCHAR(40),
    fecha_registro     DATETIME NOT NULL,
    facultad_asociada  VARCHAR(80),
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sync_batches (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id     CHAR(36) NOT NULL UNIQUE,
    source       VARCHAR(80) NOT NULL,
    submitted_at DATETIME NOT NULL,
    received_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status       ENUM('SUCCESS','PARTIAL_SUCCESS','FAILED') NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sync_events (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id         CHAR(36) NOT NULL,
    entity_type      VARCHAR(40) NOT NULL,
    idempotency_key  VARCHAR(120) NOT NULL UNIQUE,
    client_timestamp DATETIME NOT NULL,
    applied_at       DATETIME,
    status           ENUM('PROCESSED','DUPLICATE','STALE_EVENT','ERROR') NOT NULL,
    error_detail     TEXT,
    CONSTRAINT fk_sync_event_batch FOREIGN KEY (batch_id) REFERENCES sync_batches(batch_id)
) ENGINE=InnoDB;

CREATE INDEX idx_servicio_facultad_fecha_social ON servicios_social (facultad, fecha_servicio);
CREATE INDEX idx_beneficiario_municipio_social ON beneficiarios_social (municipio);
CREATE INDEX idx_seguimiento_estado_fecha_social ON seguimientos_caso (estado_caso, fecha_estado);
CREATE INDEX idx_academico_facultad_fecha_social ON participaciones_academicas (facultad, fecha_actividad);

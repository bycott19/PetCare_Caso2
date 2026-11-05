BEGIN;

CREATE TABLE cliente (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    rut VARCHAR(12) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL CHECK (btrim(nombre) <> ''),
    correo VARCHAR(254) NOT NULL CHECK (btrim(correo) <> ''),
    telefono VARCHAR(20) NOT NULL CHECK (btrim(telefono) <> ''),
    CONSTRAINT ck_cliente_rut_formato CHECK (rut ~ '^[0-9]{7,8}-[0-9K]$')
);

CREATE TABLE mascota (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES cliente(id) ON DELETE RESTRICT,
    nombre VARCHAR(80) NOT NULL CHECK (btrim(nombre) <> ''),
    especie VARCHAR(60) NOT NULL CHECK (btrim(especie) <> ''),
    raza VARCHAR(80) NOT NULL CHECK (btrim(raza) <> ''),
    fecha_nacimiento DATE NOT NULL,
    peso NUMERIC(7,3) NOT NULL CHECK (peso > 0)
);
CREATE INDEX ix_mascota_cliente ON mascota(cliente_id);

CREATE TABLE veterinario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL CHECK (btrim(nombre) <> '')
);

-- Cada fila representa una hora que el profesional ofrece semanalmente.
-- No se impone una jornada ni una duracion de consulta que el caso no define.
CREATE TABLE horario_veterinario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    veterinario_id BIGINT NOT NULL REFERENCES veterinario(id) ON DELETE RESTRICT,
    dia_semana SMALLINT NOT NULL CHECK (dia_semana BETWEEN 1 AND 7),
    hora TIME(0) NOT NULL,
    CONSTRAINT uq_horario_veterinario UNIQUE (veterinario_id, dia_semana, hora)
);

CREATE TABLE reserva (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mascota_id BIGINT NOT NULL REFERENCES mascota(id) ON DELETE RESTRICT,
    veterinario_id BIGINT NOT NULL REFERENCES veterinario(id) ON DELETE RESTRICT,
    fecha DATE NOT NULL,
    hora TIME(0) NOT NULL,
    estado VARCHAR(10) NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT ck_reserva_estado
        CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'ATENDIDA'))
);
CREATE INDEX ix_reserva_mascota ON reserva(mascota_id);
CREATE INDEX ix_reserva_veterinario_fecha ON reserva(veterinario_id, fecha);
-- Regla del enunciado: un solo cupo por fecha/hora en toda la clinica.
-- Tambien evita duplicados cuando llegan dos solicitudes simultaneamente.
CREATE UNIQUE INDEX uq_reserva_fecha_hora_ocupada
    ON reserva(fecha, hora) WHERE estado <> 'CANCELADA';

-- Una atencion por reserva; su mascota y profesional se obtienen de reserva.
CREATE TABLE tratamiento (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reserva_id BIGINT NOT NULL UNIQUE REFERENCES reserva(id) ON DELETE RESTRICT,
    diagnostico TEXT NOT NULL CHECK (btrim(diagnostico) <> ''),
    descripcion TEXT NOT NULL CHECK (btrim(descripcion) <> '')
);

-- Un tratamiento puede tener cero, uno o varios medicamentos.
CREATE TABLE medicamento_prescrito (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tratamiento_id BIGINT NOT NULL REFERENCES tratamiento(id) ON DELETE RESTRICT,
    nombre VARCHAR(160) NOT NULL CHECK (btrim(nombre) <> ''),
    indicaciones TEXT NOT NULL CHECK (btrim(indicaciones) <> '')
);
CREATE INDEX ix_medicamento_tratamiento ON medicamento_prescrito(tratamiento_id);

COMMIT;


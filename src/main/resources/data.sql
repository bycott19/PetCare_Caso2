-- Datos ficticios para desarrollo. Se cargan al crear la base por primera vez.
BEGIN;

INSERT INTO cliente (rut, nombre, correo, telefono)
VALUES ('11111111-1', 'Cliente de prueba', 'cliente@example.invalid', '000000000');

INSERT INTO mascota (cliente_id, nombre, especie, raza, fecha_nacimiento, peso)
VALUES (
    (SELECT id FROM cliente WHERE rut = '11111111-1'),
    'Luna', 'Perro', 'Mestiza', '2022-03-15', 12.500
);

INSERT INTO veterinario (nombre)
VALUES ('Veterinario de prueba');

-- Horas de ejemplo para los lunes.
INSERT INTO horario_veterinario (veterinario_id, dia_semana, hora)
SELECT id, 1, TIME '10:00' FROM veterinario WHERE nombre = 'Veterinario de prueba';

INSERT INTO horario_veterinario (veterinario_id, dia_semana, hora)
SELECT id, 1, TIME '10:30' FROM veterinario WHERE nombre = 'Veterinario de prueba';

COMMIT;

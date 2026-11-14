import { crearUbicacion } from '../models/ubicacion.model.js';

export function obtenerUbicacion() {
  return crearUbicacion({
    nombre: process.env.CLINICA_NOMBRE ?? 'Clínica Veterinaria PetCare',
    direccion: process.env.CLINICA_DIRECCION ?? 'Barros Arana 1000',
    comuna: process.env.CLINICA_COMUNA ?? 'Concepción',
    region: process.env.CLINICA_REGION ?? 'Región del Biobío',
    telefono: process.env.CLINICA_TELEFONO ?? '+56 41 000 0000',
    horario: process.env.CLINICA_HORARIO ?? 'Lunes a viernes 09:00 - 19:00 · Sábado 10:00 - 14:00',
    latitud: Number(process.env.CLINICA_LATITUD ?? -36.8270),
    longitud: Number(process.env.CLINICA_LONGITUD ?? -73.0503),
  });
}

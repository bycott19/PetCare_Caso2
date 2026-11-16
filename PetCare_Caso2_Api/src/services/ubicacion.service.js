import { crearUbicacion } from '../models/ubicacion.model.js';

export function obtenerUbicacion() {
  return crearUbicacion({
    nombre: process.env.CLINICA_NOMBRE ?? 'Clínica Veterinaria PetCare',
    direccion: process.env.CLINICA_DIRECCION ?? 'Sector Plaza de Armas',
    comuna: process.env.CLINICA_COMUNA ?? 'Chillán',
    region: process.env.CLINICA_REGION ?? 'Región de Ñuble',
    telefono: process.env.CLINICA_TELEFONO ?? '+56 42 000 0000',
    horario: process.env.CLINICA_HORARIO ?? 'Lunes a viernes 09:00 - 19:00 · Sábado 10:00 - 14:00',
    latitud: Number(process.env.CLINICA_LATITUD ?? -36.60669),
    longitud: Number(process.env.CLINICA_LONGITUD ?? -72.10335),
  });
}

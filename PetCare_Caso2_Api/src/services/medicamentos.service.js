import { crearMedicamento } from '../models/medicamento.model.js';

const CATALOGO = [
  {
    nombre: 'Amoxicilina con ácido clavulánico',
    principioActivo: 'Amoxicilina + ácido clavulánico',
    tipo: 'Antibiótico',
    presentaciones: ['Comprimidos', 'Suspensión oral'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Infecciones bacterianas de piel, vías respiratorias y urinarias.',
    advertencias: 'No usar en conejos ni roedores. Puede causar molestias digestivas.',
  },
  {
    nombre: 'Meloxicam',
    principioActivo: 'Meloxicam',
    tipo: 'Antiinflamatorio no esteroidal',
    presentaciones: ['Suspensión oral', 'Inyectable', 'Comprimidos'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Dolor e inflamación asociados a cirugías y problemas articulares.',
    advertencias: 'Precaución en animales con daño renal o deshidratados.',
  },
  {
    nombre: 'Carprofeno',
    principioActivo: 'Carprofeno',
    tipo: 'Antiinflamatorio no esteroidal',
    presentaciones: ['Comprimidos masticables', 'Inyectable'],
    especies: ['Perro'],
    indicaciones: 'Dolor por artrosis y manejo del dolor postoperatorio.',
    advertencias: 'No usar en gatos. Suspender si hay vómitos o diarrea.',
  },
  {
    nombre: 'Metronidazol',
    principioActivo: 'Metronidazol',
    tipo: 'Antibiótico / antiprotozoario',
    presentaciones: ['Comprimidos', 'Suspensión oral'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Diarreas por giardias y algunas infecciones bacterianas intestinales.',
    advertencias: 'Sabor amargo. Puede producir signos neurológicos en tratamientos prolongados.',
  },
  {
    nombre: 'Enrofloxacino',
    principioActivo: 'Enrofloxacino',
    tipo: 'Antibiótico',
    presentaciones: ['Comprimidos', 'Inyectable'],
    especies: ['Perro', 'Gato', 'Conejo', 'Ave'],
    indicaciones: 'Infecciones urinarias, respiratorias y de piel.',
    advertencias: 'Evitar en cachorros en crecimiento. En gatos respetar la dosis indicada.',
  },
  {
    nombre: 'Cefalexina',
    principioActivo: 'Cefalexina',
    tipo: 'Antibiótico',
    presentaciones: ['Comprimidos', 'Cápsulas', 'Suspensión oral'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Infecciones de piel (pioderma) y tejidos blandos.',
    advertencias: 'Puede causar vómitos si se administra en ayunas.',
  },
  {
    nombre: 'Doxiciclina',
    principioActivo: 'Doxiciclina',
    tipo: 'Antibiótico',
    presentaciones: ['Comprimidos', 'Suspensión oral'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Enfermedades transmitidas por garrapatas e infecciones respiratorias.',
    advertencias: 'En gatos administrar con agua para evitar lesiones en el esófago.',
  },
  {
    nombre: 'Prednisolona',
    principioActivo: 'Prednisolona',
    tipo: 'Corticoide',
    presentaciones: ['Comprimidos'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Alergias, enfermedades inflamatorias y autoinmunes.',
    advertencias: 'No suspender de golpe en tratamientos largos. No combinar con antiinflamatorios no esteroidales.',
  },
  {
    nombre: 'Ivermectina',
    principioActivo: 'Ivermectina',
    tipo: 'Antiparasitario',
    presentaciones: ['Inyectable', 'Solución oral'],
    especies: ['Perro', 'Conejo', 'Ave'],
    indicaciones: 'Control de ácaros, sarna y parásitos internos.',
    advertencias: 'Tóxica en razas sensibles (Collie, Pastor Australiano y similares).',
  },
  {
    nombre: 'Praziquantel',
    principioActivo: 'Praziquantel',
    tipo: 'Antiparasitario interno',
    presentaciones: ['Comprimidos', 'Pipeta'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Desparasitación contra tenias (gusanos planos).',
    advertencias: 'Repetir según el calendario de desparasitación indicado.',
  },
  {
    nombre: 'Fipronil',
    principioActivo: 'Fipronil',
    tipo: 'Antiparasitario externo',
    presentaciones: ['Pipeta', 'Spray'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Prevención y control de pulgas y garrapatas.',
    advertencias: 'No usar en conejos. Evitar el baño 48 horas después de aplicar.',
    requiereReceta: false,
  },
  {
    nombre: 'Tramadol',
    principioActivo: 'Tramadol',
    tipo: 'Analgésico',
    presentaciones: ['Comprimidos', 'Gotas', 'Inyectable'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Dolor moderado a severo.',
    advertencias: 'Puede causar sedación. Uso controlado bajo receta.',
  },
  {
    nombre: 'Omeprazol',
    principioActivo: 'Omeprazol',
    tipo: 'Protector gástrico',
    presentaciones: ['Cápsulas', 'Comprimidos'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Gastritis, úlceras y protección gástrica durante tratamientos.',
    advertencias: 'Administrar antes de la comida.',
  },
  {
    nombre: 'Maropitant',
    principioActivo: 'Maropitant',
    tipo: 'Antiemético',
    presentaciones: ['Comprimidos', 'Inyectable'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Prevención y tratamiento de vómitos y mareo por viaje.',
    advertencias: 'No usar en cachorros muy pequeños sin indicación.',
  },
  {
    nombre: 'Furosemida',
    principioActivo: 'Furosemida',
    tipo: 'Diurético',
    presentaciones: ['Comprimidos', 'Inyectable'],
    especies: ['Perro', 'Gato'],
    indicaciones: 'Edema y apoyo en insuficiencia cardiaca.',
    advertencias: 'Asegurar acceso permanente a agua. Controlar función renal.',
  },
].map((m, i) => crearMedicamento({ id: i + 1, ...m }));

function normalizar(texto) {
  return (texto ?? '')
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .toLowerCase()
    .trim();
}

export function buscarMedicamentos({ nombre, especie } = {}) {
  const q = normalizar(nombre);
  const esp = normalizar(especie);
  return CATALOGO.filter((m) =>
    (!q || normalizar(m.nombre).includes(q) || normalizar(m.principioActivo).includes(q))
    && (!esp || m.especies.some((e) => normalizar(e) === esp)));
}

export function obtenerMedicamento(id) {
  return CATALOGO.find((m) => m.id === id) ?? null;
}


export const TIPOS_NOTIFICACION = ['CONFIRMACION', 'CANCELACION', 'MODIFICACION', 'RECORDATORIO'];

export const ESTADOS_NOTIFICACION = ['ENVIADA', 'SIMULADA', 'FALLIDA'];

const CORREO_VALIDO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const FECHA_VALIDA = /^\d{4}-\d{2}-\d{2}$/;
const HORA_VALIDA = /^\d{2}:\d{2}(:\d{2})?$/;

export function validarNotificacion(datos) {
  const errores = [];
  if (!datos || typeof datos !== 'object' || Array.isArray(datos)) {
    return ['El cuerpo de la solicitud debe ser un objeto JSON'];
  }
  if (!TIPOS_NOTIFICACION.includes(datos.tipo)) {
    errores.push(`tipo debe ser uno de: ${TIPOS_NOTIFICACION.join(', ')}`);
  }
  if (typeof datos.destinatario !== 'string' || !CORREO_VALIDO.test(datos.destinatario)) {
    errores.push('destinatario debe ser un correo válido');
  }
  if (!texto(datos.nombreCliente)) errores.push('nombreCliente es obligatorio');
  if (!texto(datos.nombreMascota)) errores.push('nombreMascota es obligatorio');
  if (typeof datos.fecha !== 'string' || !FECHA_VALIDA.test(datos.fecha)) {
    errores.push('fecha debe tener formato AAAA-MM-DD');
  }
  if (typeof datos.hora !== 'string' || !HORA_VALIDA.test(datos.hora)) {
    errores.push('hora debe tener formato HH:mm');
  }
  if (datos.reservaId !== undefined && !Number.isInteger(datos.reservaId)) {
    errores.push('reservaId debe ser un número entero');
  }
  return errores;
}

export function crearNotificacion(id, datos, asunto, mensaje) {
  return {
    id,
    reservaId: datos.reservaId ?? null,
    tipo: datos.tipo,
    destinatario: datos.destinatario.trim().toLowerCase(),
    nombreCliente: datos.nombreCliente.trim(),
    nombreMascota: datos.nombreMascota.trim(),
    nombreVeterinario: texto(datos.nombreVeterinario) ? datos.nombreVeterinario.trim() : null,
    fecha: datos.fecha,
    hora: datos.hora.slice(0, 5),
    asunto,
    mensaje,
    estado: 'SIMULADA',
    detalleEnvio: null,
    creadaEn: new Date().toISOString(),
  };
}

function texto(valor) {
  return typeof valor === 'string' && valor.trim() !== '';
}

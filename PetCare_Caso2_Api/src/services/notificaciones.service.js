import { crearNotificacion } from '../models/notificacion.model.js';

const historial = [];
let siguienteId = 1;

const ASUNTOS = {
  CONFIRMACION: 'Tu hora en PetCare fue confirmada',
  CANCELACION: 'Tu hora en PetCare fue cancelada',
  MODIFICACION: 'Tu hora en PetCare fue modificada',
  RECORDATORIO: 'Recordatorio de tu hora en PetCare',
};

function armarMensaje(d) {
  const fecha = d.fecha.split('-').reverse().join('/');
  const hora = d.hora.slice(0, 5);
  const conVet = d.nombreVeterinario ? ` con ${d.nombreVeterinario}` : '';
  const detalle = `la hora de ${d.nombreMascota} del ${fecha} a las ${hora}${conVet}`;

  switch (d.tipo) {
    case 'CONFIRMACION':
      return `Hola ${d.nombreCliente}, te confirmamos ${detalle}. Te esperamos en PetCare.`;
    case 'CANCELACION':
      return `Hola ${d.nombreCliente}, te informamos que ${detalle} fue cancelada. Puedes reservar una nueva hora cuando quieras.`;
    case 'MODIFICACION':
      return `Hola ${d.nombreCliente}, actualizamos tu reserva: ahora ${detalle}.`;
    default:
      return `Hola ${d.nombreCliente}, te recordamos ${detalle}.`;
  }
}

async function enviarCorreo(notificacion) {
  if (!process.env.SMTP_HOST) {
    return { estado: 'SIMULADA', detalle: 'Sin SMTP configurado: el mensaje se registró sin enviar correo.' };
  }
  try {
    const { default: nodemailer } = await import('nodemailer');
    const transporte = nodemailer.createTransport({
      host: process.env.SMTP_HOST,
      port: Number(process.env.SMTP_PORT ?? 587),
      secure: process.env.SMTP_SECURE === 'true',
      auth: process.env.SMTP_USER ? { user: process.env.SMTP_USER, pass: process.env.SMTP_PASS } : undefined,
    });
    await transporte.sendMail({
      from: process.env.SMTP_FROM ?? process.env.SMTP_USER,
      to: notificacion.destinatario,
      subject: notificacion.asunto,
      text: notificacion.mensaje,
    });
    return { estado: 'ENVIADA', detalle: 'Correo enviado.' };
  } catch {
    return { estado: 'FALLIDA', detalle: 'No se pudo enviar el correo.' };
  }
}

export async function enviarNotificacion(datos) {
  const notificacion = crearNotificacion(siguienteId++, datos, ASUNTOS[datos.tipo], armarMensaje(datos));
  const resultado = await enviarCorreo(notificacion);
  notificacion.estado = resultado.estado;
  notificacion.detalleEnvio = resultado.detalle;
  historial.unshift(notificacion);
  console.log(`[notificacion #${notificacion.id}] ${notificacion.tipo} (${notificacion.estado})`);
  return notificacion;
}

export function listarNotificaciones({ destinatario, reservaId } = {}) {
  return historial.filter((n) =>
    (!destinatario || n.destinatario === destinatario.trim().toLowerCase())
    && (!reservaId || n.reservaId === Number(reservaId)));
}

export function obtenerNotificacion(id) {
  return historial.find((n) => n.id === id) ?? null;
}

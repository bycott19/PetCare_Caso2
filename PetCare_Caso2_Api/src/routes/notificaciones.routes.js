import { createHash, timingSafeEqual } from 'node:crypto';
import { json, Router } from 'express';
import { TIPOS_NOTIFICACION, validarNotificacion } from '../models/notificacion.model.js';
import { enviarNotificacion, listarNotificaciones, obtenerNotificacion } from '../services/notificaciones.service.js';

const router = Router();
const clave = process.env.NOTIFICACIONES_API_KEY?.trim();
const claveEsperada = clave?.length >= 32 ? huella(clave) : null;

function huella(texto) {
  return createHash('sha256').update(texto).digest();
}

router.use((req, res, next) => {
  res.set('Cache-Control', 'no-store');
  if (!claveEsperada) {
    return res.status(503).json({ error: 'Servicio de notificaciones no configurado' });
  }
  const token = /^Bearer\s+(\S+)$/i.exec(req.get('Authorization') ?? '')?.[1];
  if (!token || !timingSafeEqual(huella(token), claveEsperada)) {
    res.set('WWW-Authenticate', 'Bearer');
    return res.status(401).json({ error: 'Se requiere una credencial válida' });
  }
  next();
});

router.get('/tipos', (req, res) => {
  res.json(TIPOS_NOTIFICACION);
});

router.get('/', (req, res) => {
  const { destinatario, reservaId } = req.query;
  if ([destinatario, reservaId].some((valor) => valor !== undefined && typeof valor !== 'string')) {
    return res.status(400).json({ error: 'Cada filtro debe tener un solo valor de texto' });
  }
  const resultados = listarNotificaciones({ destinatario, reservaId });
  res.json({ total: resultados.length, resultados });
});

router.get('/:id', (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) {
    return res.status(400).json({ error: 'El id debe ser un número entero' });
  }
  const notificacion = obtenerNotificacion(id);
  if (!notificacion) {
    return res.status(404).json({ error: `No existe la notificación con id ${id}` });
  }
  res.json(notificacion);
});

router.post('/', json({ limit: '8kb' }), async (req, res, next) => {
  try {
    const errores = validarNotificacion(req.body);
    if (errores.length > 0) {
      return res.status(400).json({ error: 'Datos inválidos', detalles: errores });
    }
    const notificacion = await enviarNotificacion(req.body);
    res.status(201).json(notificacion);
  } catch (error) {
    next(error);
  }
});

export default router;

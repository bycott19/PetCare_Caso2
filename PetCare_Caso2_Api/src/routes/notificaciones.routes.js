import { Router } from 'express';
import { TIPOS_NOTIFICACION, validarNotificacion } from '../models/notificacion.model.js';
import { enviarNotificacion, listarNotificaciones, obtenerNotificacion } from '../services/notificaciones.service.js';

const router = Router();

router.get('/tipos', (req, res) => {
  res.json(TIPOS_NOTIFICACION);
});

router.get('/', (req, res) => {
  const resultados = listarNotificaciones({
    destinatario: req.query.destinatario,
    reservaId: req.query.reservaId,
  });
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

router.post('/', async (req, res, next) => {
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

import { Router } from 'express';
import { obtenerUbicacion } from '../services/ubicacion.service.js';

const router = Router();

router.get('/', (req, res) => {
  res.json(obtenerUbicacion());
});

router.get('/mapa', (req, res) => {
  const u = obtenerUbicacion();
  const esc = (t) => String(t).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  res.type('html').send(`<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${esc(u.nombre)} · Ubicación</title>
  <style>
    html, body { margin: 0; height: 100%; font-family: sans-serif; }
    iframe { border: 0; width: 100%; height: calc(100% - 48px); }
    p { margin: 0; padding: 14px 16px; font-size: 14px; }
  </style>
</head>
<body>
  <p><strong>${esc(u.nombre)}</strong> · ${esc(u.direccion)}, ${esc(u.comuna)}</p>
  <iframe src="${esc(u.mapa.embedUrl)}" title="Mapa de ${esc(u.nombre)}" loading="lazy"></iframe>
</body>
</html>`);
});

export default router;

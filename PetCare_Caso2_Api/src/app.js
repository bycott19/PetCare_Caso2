import express from 'express';
import medicamentosRoutes from './routes/medicamentos.routes.js';
import notificacionesRoutes from './routes/notificaciones.routes.js';
import ubicacionRoutes from './routes/ubicacion.routes.js';

const app = express();
const PORT = Number(process.env.PORT ?? 3000);
const HOST = process.env.HOST ?? '0.0.0.0';

app.use(express.json());

app.use((req, res, next) => {
  res.setHeader('Access-Control-Allow-Origin', process.env.CORS_ORIGIN ?? '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET,POST,OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  if (req.method === 'OPTIONS') return res.sendStatus(204);
  next();
});

app.use((req, res, next) => {
  const inicio = Date.now();
  res.on('finish', () => console.log(`${req.method} ${req.originalUrl} -> ${res.statusCode} (${Date.now() - inicio} ms)`));
  next();
});

app.get('/api/health', (req, res) => {
  res.json({ estado: 'ok', servicio: 'petcare-caso2-api', fecha: new Date().toISOString() });
});

app.use('/api/medicamentos', medicamentosRoutes);
app.use('/api/notificaciones', notificacionesRoutes);
app.use('/api/ubicacion', ubicacionRoutes);

app.use((req, res) => {
  res.status(404).json({ error: `No existe la ruta ${req.method} ${req.originalUrl}` });
});

app.use((err, req, res, next) => {
  if (err.type === 'entity.parse.failed') {
    return res.status(400).json({ error: 'El cuerpo de la solicitud no es un JSON válido' });
  }
  console.error(err);
  res.status(500).json({ error: 'Error interno de la API' });
});

app.listen(PORT, HOST, () => {
  console.log(`PetCare API escuchando en http://${HOST}:${PORT}`);
});

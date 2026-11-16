import { Router } from 'express';
import { buscarMedicamentos, obtenerMedicamento } from '../services/medicamentos.service.js';

const router = Router();

router.get('/', (req, res) => {
  const { nombre, especie } = req.query;
  if ([nombre, especie].some((valor) => valor !== undefined && typeof valor !== 'string')) {
    return res.status(400).json({ error: 'Cada filtro debe tener un solo valor de texto' });
  }
  const resultados = buscarMedicamentos({ nombre, especie });
  res.json({ total: resultados.length, resultados });
});

router.get('/:id', (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) {
    return res.status(400).json({ error: 'El id debe ser un número entero' });
  }
  const medicamento = obtenerMedicamento(id);
  if (!medicamento) {
    return res.status(404).json({ error: `No existe el medicamento con id ${id}` });
  }
  res.json(medicamento);
});

export default router;

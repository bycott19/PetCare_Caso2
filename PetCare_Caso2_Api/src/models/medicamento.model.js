
export function crearMedicamento({
  id,
  nombre,
  principioActivo,
  tipo,
  presentaciones = [],
  especies = [],
  indicaciones,
  advertencias,
  requiereReceta = true,
}) {
  return {
    id,
    nombre,
    principioActivo,
    tipo,
    presentaciones,
    especies,
    indicaciones,
    advertencias,
    requiereReceta,
  };
}

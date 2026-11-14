
export function crearUbicacion({ nombre, direccion, comuna, region, telefono, horario, latitud, longitud }) {
  const delta = 0.004;
  const bbox = [longitud - delta, latitud - delta, longitud + delta, latitud + delta]
    .map((n) => n.toFixed(5))
    .join(',');

  return {
    nombre,
    direccion,
    comuna,
    region,
    telefono,
    horario,
    coordenadas: { latitud, longitud },
    mapa: {
      embedUrl: `https://www.openstreetmap.org/export/embed.html?bbox=${bbox}&layer=mapnik&marker=${latitud},${longitud}`,
      verUrl: `https://www.openstreetmap.org/?mlat=${latitud}&mlon=${longitud}#map=17/${latitud}/${longitud}`,
      comoLlegarUrl: `https://www.google.com/maps/dir/?api=1&destination=${latitud},${longitud}`,
    },
  };
}

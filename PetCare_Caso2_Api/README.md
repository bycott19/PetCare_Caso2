# PetCare API (Node + Express)

Microservicio externo que consume el sistema principal (Spring Boot). Entrega:

- **Medicamentos**: búsqueda por nombre en un catálogo veterinario (para registrar tratamientos).
- **Notificaciones**: mensajes al cliente cuando una reserva se confirma, cancela o modifica.
- **Ubicación**: datos y mapa de la clínica (OpenStreetMap, sin clave de API).

## Levantar

Requiere Node 22 o superior.

```bash
cd PetCare_Caso2_Api
npm install
npm start
```

En Windows, si PowerShell bloquea `npm`, usar `npm.cmd install` y `npm.cmd start`.

La API queda en `http://localhost:3000`. Para modo desarrollo (se reinicia al guardar): `npm run dev`.

### Consumirla desde otro equipo de la red

1. La API escucha en `0.0.0.0`, así que acepta conexiones de la red local.
2. En el equipo de la API, ver su IP con `ipconfig` (ej: `192.168.1.50`).
3. Si Windows pregunta por el firewall, permitir Node en redes privadas.
4. Desde el otro equipo usar `http://192.168.1.50:3000/api/...`.

### Configuración (opcional)

Copiar `.env.example` como `.env`. Se puede cambiar el puerto, los datos de la clínica y activar el envío real de correos:

- Sin `SMTP_HOST`, las notificaciones se registran con estado `SIMULADA` (no se envía correo).
- Con `SMTP_HOST` configurado y `npm install nodemailer`, se envía el correo y el estado queda `ENVIADA` (o `FALLIDA` si el servidor lo rechaza).

## Endpoints

| Método | URL | Descripción |
| --- | --- | --- |
| GET | `/api/health` | Comprueba que la API está arriba |
| GET | `/api/medicamentos?nombre=amox&especie=perro` | Busca medicamentos por nombre o principio activo (sin distinguir mayúsculas ni tildes). Ambos filtros son opcionales |
| GET | `/api/medicamentos/{id}` | Detalle de un medicamento |
| POST | `/api/notificaciones` | Registra y envía una notificación a un cliente |
| GET | `/api/notificaciones?destinatario=correo&reservaId=7` | Historial de notificaciones (filtros opcionales) |
| GET | `/api/notificaciones/{id}` | Detalle de una notificación |
| GET | `/api/notificaciones/tipos` | Tipos válidos de notificación |
| GET | `/api/ubicacion` | Datos de la clínica, coordenadas y URLs de mapa |
| GET | `/api/ubicacion/mapa` | Página HTML con el mapa, para mostrar en un `<iframe>` |

Los errores siempre responden JSON: `{ "error": "mensaje" }` con código 400 (datos inválidos), 404 (no existe) o 500.

### Ejemplos

**Buscar medicamento**

```
GET /api/medicamentos?nombre=amoxi
```

```json
{
  "total": 1,
  "resultados": [
    {
      "id": 1,
      "nombre": "Amoxicilina con ácido clavulánico",
      "principioActivo": "Amoxicilina + ácido clavulánico",
      "tipo": "Antibiótico",
      "presentaciones": ["Comprimidos", "Suspensión oral"],
      "especies": ["Perro", "Gato"],
      "indicaciones": "Infecciones bacterianas de piel, vías respiratorias y urinarias.",
      "advertencias": "No usar en conejos ni roedores. Puede causar molestias digestivas.",
      "requiereReceta": true
    }
  ]
}
```

**Enviar notificación** (al confirmar o cancelar una reserva)

```
POST /api/notificaciones
Content-Type: application/json
```

```json
{
  "tipo": "CONFIRMACION",
  "reservaId": 7,
  "destinatario": "camila@mail.cl",
  "nombreCliente": "Camila Rojas",
  "nombreMascota": "Luna",
  "nombreVeterinario": "Dra. Valentina Pérez",
  "fecha": "2026-11-20",
  "hora": "10:30"
}
```

`tipo`: `CONFIRMACION`, `CANCELACION`, `MODIFICACION` o `RECORDATORIO`. `reservaId` y `nombreVeterinario` son opcionales.

Respuesta `201`:

```json
{
  "id": 1,
  "reservaId": 7,
  "tipo": "CONFIRMACION",
  "destinatario": "camila@mail.cl",
  "asunto": "Tu hora en PetCare fue confirmada",
  "mensaje": "Hola Camila Rojas, te confirmamos la hora de Luna del 20/11/2026 a las 10:30 con Dra. Valentina Pérez. Te esperamos en PetCare.",
  "estado": "SIMULADA",
  "creadaEn": "2026-11-14T22:19:20.729Z"
}
```

**Ubicación**

```
GET /api/ubicacion
```

```json
{
  "nombre": "Clínica Veterinaria PetCare",
  "direccion": "Barros Arana 1000",
  "comuna": "Concepción",
  "coordenadas": { "latitud": -36.827, "longitud": -73.0503 },
  "mapa": {
    "embedUrl": "https://www.openstreetmap.org/export/embed.html?...",
    "verUrl": "https://www.openstreetmap.org/?mlat=-36.827&mlon=-73.0503#map=17/-36.827/-73.0503",
    "comoLlegarUrl": "https://www.google.com/maps/dir/?api=1&destination=-36.827,-73.0503"
  }
}
```

Para mostrar el mapa en una vista Thymeleaf basta con:

```html
<iframe th:src="${ubicacion.mapa.embedUrl}" width="100%" height="380"></iframe>
```

## Estructura

```text
src/
├── app.js                 # Servidor Express, CORS, rutas y manejo de errores
├── models/                # Forma y validación de los datos
├── routes/                # Endpoints HTTP
└── services/              # Lógica (catálogo, envío de notificaciones, ubicación)
```

Las notificaciones se guardan en memoria: se pierden al reiniciar la API.

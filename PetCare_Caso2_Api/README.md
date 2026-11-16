# API de PetCare

Hecha con Node.js y Express. Tiene el catálogo de medicamentos, la ubicación de la clínica en Chillán y las notificaciones de reservas.

## Para correrla

Con Node 22 o superior, abre PowerShell en la raíz del proyecto.

```powershell
cd PetCare_Caso2_Api
npm.cmd install
npm.cmd start
```

La API queda en `http://localhost:3000`. Si corre en otro equipo, configura `PETCARE_API_BASE_URL` en el `.env` de Spring Boot con la IP y el puerto de ese equipo.

## Rutas

- `GET /api/health` comprueba que la API esté corriendo.
- `GET /api/medicamentos?nombre=amox` busca en el catálogo. También se puede filtrar por `especie` o consultar por ID en `/api/medicamentos/{id}`.
- `GET /api/ubicacion` devuelve la ficha de la clínica. `GET /api/ubicacion/mapa` muestra el mapa de OpenStreetMap.
- `POST /api/notificaciones` registra un aviso. `GET /api/notificaciones` muestra el historial y acepta los filtros `destinatario` y `reservaId`. También están `GET /api/notificaciones/{id}` y `GET /api/notificaciones/tipos`.

## Notificaciones

Si aún no tienes `.env`, copia `.env.example`. Genera una clave con `node -p "require('node:crypto').randomBytes(32).toString('hex')"`, guárdala en `NOTIFICACIONES_API_KEY` y reinicia Node. El backend debe enviar la misma clave en el encabezado `Authorization: Bearer <clave>`. Usa HTTPS si vas a enviar datos reales entre equipos.

Para crear un aviso, envía JSON con `tipo` (por ejemplo, `CONFIRMACION` o `CANCELACION`), `destinatario`, `nombreCliente`, `nombreMascota`, `fecha` (AAAA-MM-DD) y `hora` (HH:mm). `reservaId` y `nombreVeterinario` son opcionales.

Si dejas `SMTP_HOST` vacío, el aviso queda como `SIMULADA`. Para enviar correos, instala `nodemailer` y completa las variables SMTP. El historial se guarda en memoria hasta que reinicies la API.

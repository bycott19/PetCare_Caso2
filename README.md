# PetCare - Sistema de Gestión de Atención Veterinaria

## Integrantes

- Javier Baeza Toro
- Bairon Muñoz Sepúlveda

## Descripción

Sistema web desarrollado con Spring Boot y PostgreSQL para la administración de atención veterinaria (clientes, mascotas, veterinarios y citas), complementado con un microservicio en Node.js para servicios externos (catálogo de medicamentos, notificaciones y geolocalización).

## Estructura del Proyecto

```text
PetCare_Caso2/
├── docker-compose.yml          # Configuración de base de datos PostgreSQL
├── .env.example                # Plantilla de variables de entorno
├── build.gradle                # Dependencias y configuración de Gradle
├── PetCare_Caso2_Api/          # Microservicio en Node.js (APIs externas)
│   ├── package.json
│   └── src/
└── src/                        # Backend principal en Spring Boot
    ├── main/
    │   ├── java/cl/baeza_munoz/PetCare_Caso2/
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   ├── exception/
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── service/
    │   └── resources/
    │       ├── application.properties
    │       ├── schema.sql
    │       ├── data.sql
    │       ├── static/
    │       └── templates/
    └── test/
```

## Instrucciones de Ejecución

### 1. Base de Datos (PostgreSQL)
Asegurarse de tener Docker Desktop iniciado y ejecutar en la raíz:
```bash
docker compose up -d
```

### 2. Backend Principal (Spring Boot)
Copiar `.env.example` a `.env` si es necesario y ejecutar:
```bash
.\gradlew.bat bootRun
```
O iniciar la aplicación directamente desde el IDE (`PetCareCaso2Application.java`). La aplicación web estará disponible en `http://localhost:8081`.

### 3. API Externa (Node.js)
En una terminal separada:
```bash
cd PetCare_Caso2_Api
npm install
npm start
```
El microservicio escuchará en `http://localhost:3000`.

En la web principal, el menú **Ubicación** consulta el microservicio y muestra los datos de la clínica y el mapa de OpenStreetMap. Si ejecutas Node en otro equipo de la red, configura `PETCARE_API_BASE_URL=http://IP_DEL_EQUIPO:3000` en el `.env` del proyecto Spring Boot y vuelve a iniciar Spring Boot. El mapa necesita internet para cargar.

# PetCare - Caso 2

## Integrantes

- Javier Baeza Toro
- Bairon Muñoz Sepúlveda

## Estructura

```text
PetCare_Caso2/
├── docker-compose.yml
├── .env.example
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── src/
    ├── main/
    │   ├── java/cl/baeza_munoz/PetCare_Caso2/
    │   │   ├── PetCareCaso2Application.java
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   ├── exception/
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── service/
    │   └── resources/
    │       ├── application.properties
    │       ├── schema.sql
    │       └── data.sql
    └── test/
```

## Ejecutar

1. Copiar .env.example como .env y completar las credenciales sin comillas. Si ya existe .env, conservarlo.
2. Con Docker Desktop abierto, ejecutar desde la raiz: `docker compose up -d`.
3. Ejecutar `.\gradlew.bat bootRun` o iniciar la aplicacion desde el IDE con la raiz del proyecto como directorio de trabajo.

En DBeaver, usar el host, puerto, base, usuario y contrasena del .env. Ese archivo esta excluido de Git.

Los SQL se cargan solo al crear el volumen por primera vez. Para detener sin borrar datos: `docker compose down`. Cambiar la clave del .env no cambia la de una base ya creada.

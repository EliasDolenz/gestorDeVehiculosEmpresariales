# Gestor de Vehículos Empresariales

API REST para administrar la flota de vehículos de un grupo empresario: quién tiene cada auto,
cuándo se reservó, cuántos kilómetros lleva, qué fallas se reportaron y cuánto combustible se
cargó. Resuelve el problema de una flota compartida entre varias empresas del grupo, donde el
control se lleva hoy en planillas y grupo de whatsapp.

## Stack

| | |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.5.16 (Web, Data JPA, Validation) |
| Base de datos | PostgreSQL 16 |
| ORM | Hibernate |
| Documentación | springdoc-openapi (Swagger UI) |
| Infraestructura | Docker Compose |
| Build | Maven |

## Cómo levantarlo

Necesitás Docker y JDK 17. No hace falta instalar Maven ni PostgreSQL.

**1. Levantar la base de datos**

```bash
docker compose up -d
```

**2. Arrancar la aplicación**

```bash
./mvnw spring-boot:run
```

En Windows: `.\mvnw.cmd spring-boot:run`

La app queda en `http://localhost:8080` con el perfil `dev` activo, que crea el esquema
automáticamente (`ddl-auto=update`). No hace falta correr ningún script SQL.

> Las credenciales de la base están escritas en `application-dev.properties` y en
> `docker-compose.yml` a propósito: es un entorno local descartable. El perfil `prod` toma
> `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` de variables de entorno, sin valores por defecto, para
> que la aplicación no arranque si falta alguna.

## Probar la API

Con la aplicación corriendo:

**http://localhost:8080/swagger-ui.html**

Ahí están los 44 endpoints agrupados por dominio, con los campos de cada request, sus
validaciones y los códigos de error que devuelve cada uno. La definición OpenAPI en crudo está
en `/v3/api-docs`.

| Recurso | Ruta base |
|---|---|
| Vehículos | `/api/vehiculos` |
| Empleados | `/api/empleados` |
| Empresas | `/api/empresas` |
| Departamentos | `/api/departamentos` |
| Reservas | `/api/reservas` |
| Usos | `/api/usos` |
| Novedades | `/api/novedades` |
| Cargas de combustible | `/api/cargasDeCombustible` |

## Modelo de dominio

```
Empresa ──< Departamento ──< Empleado
                  │
                  └──< Vehículo ──< Reserva
                            ├──< Uso
                            ├──< Novedad
                            └──< CargaDeCombustible
```

Ocho entidades. Tanto los **vehículos** como los **empleados** cuelgan de un departamento, y
cada departamento de una empresa; por eso la flota puede compartirse dentro del grupo. La
empresa de un empleado se deriva de su departamento en lugar de guardarse aparte: tener las dos
referencias permitía que se contradijeran al mover a alguien de área.

Sobre cada vehículo se registran cuatro tipos de hecho:

- **Reserva** — un empleado aparta el vehículo para un período futuro. Se valida que no se
  superponga con otra reserva del mismo vehículo.
- **Uso** — el vehículo efectivamente en circulación. Se abre al retirarlo y se cierra al
  devolverlo, informando el kilometraje final. Valida que el empleado tenga registro vigente y
  que no tenga otro uso abierto.
- **Novedad** — una falla o observación, con un nivel de urgencia. Una novedad de urgencia
  inmediata deja el vehículo en reparación hasta que se resuelva.
- **Carga de combustible** — litros y kilometraje al momento de cargar. Admite registro
  retroactivo, porque en la práctica la carga se hace en la estación de servicio y se anota
  después.

## Decisiones técnicas

**Las entidades JPA no salen nunca por la API.** Cada dominio tiene DTOs propios: `CreateDTO` y
`UpdateDTO` de entrada, `ResponseDTO` y `SimpleDTO` de salida. Recibir la entidad directamente
en un `@RequestBody` permitiría que el cliente mande el `id` o relaciones arbitrarias
(*mass assignment*), algo especialmente sensible en `Empleado`, que guarda teléfono y PIN de
carga. Del lado de la salida, el `ResponseDTO` anida DTOs "hoja" que no vuelven a anidar: eso
evita el N+1 en los listados y la recursión infinita al serializar relaciones bidireccionales.

**El mapeo vive en mappers estáticos, uno por dominio.** Tener el mapeo duplicado ya había
causado un bug real: como los DTOs son records con constructores posicionales, dos campos
`String` contiguos quedaron invertidos en dos métodos distintos sin que el compilador dijera
nada. Con un solo mapper, el orden de los campos se escribe una sola vez.

**`spring.jpa.open-in-view=false`.** Por defecto Spring deja la sesión de Hibernate abierta
hasta que termina de renderizarse la respuesta, lo que oculta consultas perezosas disparadas
fuera de la capa de servicio. Desactivarlo obliga a mapear a DTO dentro del método
`@Transactional`, que es donde corresponde.

**Toda la aplicación corre en UTC.** El driver JDBC envía la zona horaria del sistema operativo
en el handshake de conexión, antes de cualquier consulta, y PostgreSQL rechaza
`America/Buenos_Aires` porque es un alias heredado que no compila en su base de datos de zonas
—el nombre IANA válido es `America/Argentina/Buenos_Aires`—. Como el problema ocurre antes de
que se pueda ejecutar SQL, no se arregla por configuración del datasource: la JVM se fija en UTC
en el `main`, y Hibernate y el contenedor de PostgreSQL se alinean a lo mismo. Guardar en UTC y
convertir en el borde es además la práctica estándar.

**Los errores se manejan en un solo lugar.** Un `@RestControllerAdvice` traduce las excepciones
de negocio a códigos HTTP: `RecursoNoEncontradoException` → 404, `ReglaDeNegocioException` → 409,
fallas de validación → 400 con el detalle por campo, y cualquier otra cosa → 500 con un mensaje
genérico y la traza en el log. El cliente recibe siempre la misma forma de error y el servidor
nunca filtra detalles internos.

**Inyección por constructor, sin `@Autowired` en campos.** Permite campos `final`, hace
imposible construir un objeto a medio inicializar y deja las clases instanciables en un test sin
levantar el contexto de Spring.

## Estado actual

La API está completa: los ocho dominios tienen CRUD, validaciones de negocio y documentación.

Lo que falta:

- **Tests.** Es la deuda principal del proyecto.
- **Autenticación y autorización.** Hoy todos los endpoints son públicos.
- **Avisos automáticos** de vencimiento de VTV, service y licencias de conducir, que estaban en
  el relevamiento original y requieren tareas programadas.
- **Migraciones de esquema.** El perfil `dev` usa `ddl-auto=update`, que solo agrega y nunca
  borra: al cambiar el modelo quedan columnas y restricciones huérfanas. `prod` ya usa
  `validate`, pero falta versionar los cambios con Flyway o Liquibase.
- **Frontend.**

El relevamiento que originó el proyecto está en [docs/requisitos.md](docs/requisitos.md).

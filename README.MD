![CI](https://github.com/EliasDolenz/gestorDeVehiculosEmpresariales/actions/workflows/ci.yml/badge.svg)

# Gestor de Vehículos Empresariales

API REST para administrar la flota de vehículos de un grupo empresarial: quién tiene cada auto,
cuándo se reservó, cuántos kilómetros lleva, qué fallas se reportaron y cuánto combustible se
cargó. Resuelve el problema de una flota compartida entre varias empresas del grupo, donde el
control se lleva hoy en planillas y grupo de WhatsApp.

## Stack

| | |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.5.16 (Web, Data JPA, Validation, Security) |
| Autenticación | JWT firmado con HS256 (Spring Security OAuth2 Resource Server) |
| Base de datos | PostgreSQL 16 |
| ORM | Hibernate |
| Migraciones | Flyway |
| Documentación | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, Spring Boot Test |
| Infraestructura | Docker (build multi-stage) + Docker Compose |
| CI | GitHub Actions |
| Build | Maven |

## Cómo levantarlo

Solo necesitás Docker. No hace falta instalar Java, Maven ni PostgreSQL.

```bash
docker compose up --build
```

Ese comando compila la aplicación dentro de un contenedor, levanta PostgreSQL, espera a que la
base acepte conexiones y recién entonces arranca la API. Al iniciar, Flyway crea el esquema
sobre la base vacía, así que no hace falta correr ningún script SQL.

La app queda en `http://localhost:8080/swagger-ui.html`.

### Modo desarrollo

Para trabajar sobre el código con el IDE, levantá solo la base y corré la aplicación con el
perfil `dev`:

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

En Windows: `.\mvnw.cmd spring-boot:run`. Este modo requiere JDK 17. Si el contenedor `app` está
corriendo, frenalo antes, porque ocupa el puerto 8080.

> Las credenciales de la base y el secreto con el que se firman los tokens están escritos en
> `application-dev.properties` y en `docker-compose.yml` a propósito: es un entorno local
> descartable. El perfil `prod`, que es el que usa el contenedor, toma `DB_URL`, `DB_USERNAME`,
> `DB_PASSWORD` y `JWT_SECRET` de variables de entorno, sin valores por defecto, para que la
> aplicación no arranque si falta alguna. `JWT_SECRET` debe tener al menos 32 caracteres, que es
> lo que exige HS256. La duración del token se ajusta con `JWT_EXPIRATION_MINUTOS` (60 por
> defecto).

## Probar la API

Con la aplicación corriendo:

**http://localhost:8080/swagger-ui.html**

Ahí están los 45 endpoints agrupados por dominio, con los campos de cada request, sus
validaciones y los códigos de error que devuelve cada uno. La definición OpenAPI en crudo está
en `/v3/api-docs`.

### Autenticación

Todos los endpoints piden un token, salvo el login y la documentación. Las migraciones crean
dos usuarios de demostración:

| Email | Contraseña | Rol |
|---|---|---|
| `admin@example.com` | `admin` | `ADMINISTRADOR` |
| `empleado@example.com` | `empleado` | `EMPLEADO` |

1. Pedí un token:

   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email": "admin@example.com", "contrasena": "admin"}'
   ```

   La respuesta trae el token y su tipo: `{"token": "eyJ...", "tipo": "Bearer"}`.

2. Mandalo en cada request con el header `Authorization: Bearer <token>`. En Swagger, pegalo
   en el botón **Authorize** y queda aplicado a todos los endpoints.

El token vence a los 60 minutos. Sin token, o con uno vencido o inválido, la API responde 401.

| Operación | `EMPLEADO` | `ADMINISTRADOR` |
|---|---|---|
| Consultar, crear y modificar (`GET`, `POST`, `PUT`) | Sí | Sí |
| Eliminar (`DELETE`) | No (403) | Sí |

### Fechas en UTC

La API interpreta todas las fechas y horas en UTC, sin zona horaria en el JSON
(`2026-12-01T14:30:00`). Las validaciones de "fecha futura" o "fecha pasada" comparan contra la
hora UTC del servidor, así que el cliente tiene que convertir su hora local antes de enviarla.

### Recursos

| Recurso | Ruta base |
|---|---|
| Autenticación | `/api/auth/login` |
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
a códigos HTTP: `RecursoNoEncontradoException` → 404, `ReglaDeNegocioException` → 409, fallas de
validación → 400 con el detalle por campo, JSON mal formado o con un valor de enum inexistente →
400, violaciones de restricciones de la base (un duplicado, o borrar un registro que otros
referencian) → 409, un id con formato inválido en la URL → 400, una ruta inexistente → 404, un
método HTTP no soportado → 405, credenciales incorrectas en el login → 401, y cualquier otra
cosa → 500 con un mensaje genérico y la traza en el log. Todos esos errores salen con la misma
forma y el servidor nunca filtra detalles internos. La excepción son los 401 y 403 que emite el
filtro de seguridad antes de llegar a los controllers (token ausente, inválido o sin permisos):
hoy salen sin cuerpo, con el detalle en el header `WWW-Authenticate`.

**Autenticación sin estado, con JWT.** El login valida email y contraseña contra la tabla
`usuarios` (contraseñas con BCrypt) y devuelve un token firmado que lleva el rol adentro. A
partir de ahí el servidor no guarda sesión: cada request se autentica verificando la firma y el
vencimiento del token. Para emitir y validar se usa el soporte de JWT que ya trae Spring
Security (OAuth2 Resource Server) en lugar de una librería aparte y un filtro escrito a mano.
Los permisos por rol están declarados en un solo lugar, la cadena de filtros de
`SecurityConfig`. Los usuarios son una tabla separada de los empleados: un usuario es quien
opera el sistema, un empleado es quien maneja un vehículo.

**El estado del vehículo lo calcula el sistema, no el cliente.** `estadoVehiculo` no aparece en
los DTOs de entrada: un vehículo nace `DISPONIBLE` y su estado solo cambia por efecto de un uso o
de una novedad. El recálculo según las novedades vive en un único método de `VehiculoService`,
que llaman tanto `NovedadService` como `UsoService`. Antes, cada service escribía ese campo con
sus propias reglas y se pisaban entre sí: una novedad urgente reportada durante un uso dejaba el
vehículo en un estado del que el uso ya no podía cerrarse.

**El esquema de la base está versionado con Flyway.** Cada cambio de estructura es un script
numerado en `src/main/resources/db/migration`, que se aplica una sola vez y queda registrado.
Hibernate ya no modifica tablas: arranca con `ddl-auto=validate` y solo verifica que las
entidades coincidan con el esquema. Por eso, si algo no coincide, la aplicación no arranca, en
lugar de fallar en medio de un request. El motivo concreto fue la migración V2, que
renombra un valor del enum de combustible: Hibernate genera un `CHECK` con los valores
permitidos, y `ddl-auto=update` nunca modifica restricciones existentes, así que cambiar el enum
solo en Java habría hecho fallar todos los inserts con el valor nuevo.

**La imagen de Docker se construye en dos etapas.** La primera usa el JDK completo para compilar;
la segunda solo lleva el JRE y el `.jar`, sin código fuente, sin Maven ni dependencias de build.
El `pom.xml` se copia antes que `src/` para que Docker reutilice la capa de dependencias cuando
solo cambia el código. La misma imagen sirve para cualquier entorno: el perfil y la conexión a
la base se le pasan por variables de entorno.

**Inyección por constructor, sin `@Autowired` en campos.** Permite campos `final`, hace
imposible construir un objeto a medio inicializar y deja las clases instanciables en un test sin
levantar el contexto de Spring.

## Tests

Hay tests unitarios con Mockito para las reglas de negocio de los services: apertura y cierre
de usos (licencia vigente, reservas de otros empleados, kilometraje final), superposición de
reservas, recálculo del estado del vehículo según sus novedades, y borrado de empresas con
departamentos asociados. Los repositorios se mockean, así que cada test corre en milisegundos
sin base de datos.

```bash
./mvnw test
```

El único test que levanta el contexto completo de Spring (`contextLoads`) necesita la base
corriendo (`docker compose up -d postgres`). Maven ejecuta los tests con la JVM en UTC, igual
que la aplicación.

Cada push a `main` dispara el pipeline de GitHub Actions, que levanta un PostgreSQL 16 como
servicio y corre `./mvnw verify`. Como esa base arranca vacía, el CI también comprueba que todas
las migraciones de Flyway se apliquen desde cero.

## Estado actual

La API está completa: los ocho dominios tienen CRUD, validaciones de negocio y documentación,
y todos los endpoints están protegidos con JWT y roles. El esquema está versionado con Flyway,
la aplicación corre en Docker y cada cambio pasa por CI.

Lo que falta:

- **Tests de integración** de las consultas de solapamiento y de las reglas de seguridad contra
  un PostgreSQL real, con Testcontainers.
- **Errores 401 y 403 con cuerpo JSON**, con la misma forma que el resto de los errores.
- **Vincular usuarios con empleados**, para tomar el empleado del token en lugar de recibirlo
  en el cuerpo del request.
- **Avisos automáticos** de vencimiento de VTV, service y licencias de conducir, que estaban en
  el relevamiento original y requieren tareas programadas.
- **Frontend.**

El relevamiento que originó el proyecto está en [docs/requisitos.md](docs/requisitos.md).

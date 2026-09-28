# Documento de requisitos

> Relevamiento original del proyecto, previo a la implementación. Se conserva como registro del
> punto de partida: algunos campos cambiaron de nombre o de tipo durante el desarrollo, y las
> tareas programadas de vencimientos todavía no están implementadas.

## Contexto

API RESTful para gestionar los vehículos de un grupo empresario conformado por dos razones
sociales. Cada empresa tiene su propia flota, pero los vehículos pueden prestarse entre ellas.

La aplicación debe permitir gestionar el uso de los vehículos: quién lo está usando, los
kilómetros que tiene, a qué empresa pertenece, poder reservarlos, avisar cuando esté por vencer
la VTV, avisar cuándo corresponde hacer el service, registrar observaciones sobre el vehículo y
solicitar mantenimiento. Debe poder guardarse la información de cada carga de combustible
(litros cargados y kilometraje al momento de la carga).

## Entidades

### Vehículo

    Patente (única); String
    Marca; String
    Modelo; String
    KmActual; Integer
    fechaDeService - kmDeService -> Avisar con anticipación. Scheduled tasks - Date
    verificacionVehicularVigente -> Boolean
    fechaDeVerificacionTecnicaVehicular -> Vencimiento Scheduled tasks - Date
    Departamento;
    novedades; List Novedades
    estadoVehiculoEnum; Disponible, en uso, en reparación. Enum
    combustible; ENUM -> Reserva, 1/4, 1/2, 3/4, lleno.
    numeroTarjetaNafta; String

### Empleado

    idEmpleado; Integer
    nombreEmpleado; String
    apellidoEmpleado; String
    nroTelefono; String
    empresa;
    departamento;
    puesto; String
    registroDeConducir: tiene o no tiene; Boolean
    vencimientoDeRegistro; Scheduled tasks - Date
    documentacion (adjuntarRegistro) - String URL
    pinDeCarga; String

### Empresa

    idEmpresa; Integer
    nombreEmpresa; String
    direccionEmpresa; String
    departamentos; Set

### Departamento

    idDepartamento; Integer
    nombreDepartamento; String
    empresa;
    empleados;

### Reserva

    idReserva; Integer
    vehiculo;
    empleado;
    fechaInicio; date
    fechaFinalizacion; date

### Uso

    idUso; Integer
    vehiculo;
    empleado;
    fechaInicio; date
    estadoUso; Enum
    alertaEnviada; Boolean

### Carga de combustible

    idCarga; Integer
    vehiculo;
    litros; Double
    kilometroActualVehiculo; Integer
    empleado;
    fecha; -> actualAlCrearLaCarga - DateNow

### Historial de novedades

    idNovedad; Integer
    vehiculo;
    empleado;
    fechaReporte; dateNow
    descripción; String
    estadoNovedad; Enum
    urgencia; Enum

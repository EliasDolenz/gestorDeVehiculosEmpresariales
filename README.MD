# Gestor de Vehiculos Empresariales.

    API RESTful para poder gestionar los vehiculos empresariales del Grupo CR. El grupo esta conformado por dos empresa: Canteras y Ragolia. Cada empresa tiene su flota de vehiculos, aunque se pueden prestar los vehiculos entre sí. 
    La aplicación debe poder gestionar el uso de los vehiculos, quien lo esta usando, los kilometros que tiene, a que empresa pertenece, poder reservarlos, anunciar cuando se este por vencer la VTV, anunciar cuando se debe realizar el service, poder realizar observaciones sobre el vehículo, y solicitar algun tipo de mantenimiento. Se debe poder guardar la información de cuando se cargo nafta (Litros que se hayan cargado y en que kilometraje fue).

### Vehiculos
    Cada vehículo debe tener:
    Patente (única); String
    Marca; String
    Modelo; String
    KmActual; Integer
    fechaDeService - kmDeService -> Avisar con anticipación. Scheduled tasks - Date
    verificacionVehicularVigente -> Boolean
    fechaDeVerificacionTecnicaVehicular -> Vencimiento Scheduled tasks - Date
    Departamento;
    Empleado;
    novedades; List Novedades
    estadoVehiculoEnum; Disponible, en uso, en reparación. Enum
    Reservas - Consultar a Departamento/Empleado;
    combustible; ENUM -> Reserva, 1/4, 1/2, 3/4, lleno.
    numeroTarjetaYPF; String

### Empleado
    Cada empleado debe tener:
    idEmpleado; Integer
    nombreEmpleado; String
    apellidoEmpleado; String
    nroTelefono; String
    empresa;
    departamento;
    puesto; "String"
    registroDeConducir: tiene o no tiene; Boolean
    vencimientoDeRegistro; Scheduled tasks - Date
    documentacion (adjuntarRegistro) - String URL
    pinDeCarga; String

### Empresa
    Cada empresa debe tener:
    idEmpresa; Integer
    nombreEmpresa; String
    direccionEmpresa; String
    departamentos; Set
    empleados; Set
    vehiculos; Set

### Departamento
    Cada departamento debe tener:
    idDepartamento; Integer
    nombreDepartamento; String
    empresa;
    empleados;

### Reserva
    Cada reserva debe tener:
    idReserva; Integer
    vehiculo; 
    empleado;
    fechaInicio; date
    fechaFinalizacion; date

### Uso
    Cada uso debe tener:
    idUso; Integer
    vehiculo;
    empleado;
    fechaInicio; date
    estadoUso; Enum
    alertaEnviada; Boolean
    
### Carga de combustible
    Cada cargaDeCombustible debe tener:
    idCarga; Integer
    vehiculo;
    litros; Double
    kilometroActualVehiculo; Integer
    empleado;
    fecha; -> actualAlCrearLaCarga - DateNow

### Historial Novedades
    Cada historialNovedades debe tener:
    idNovedad; Integer
    vehiculo; 
    empleado; 
    fechaReporte; dateNow
    descripción; String
    estadoNovedad; Enum
    urgencia; Enum
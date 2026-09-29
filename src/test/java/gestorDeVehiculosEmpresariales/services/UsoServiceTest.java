package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.uso.TerminarUsoDTO;
import gestorDeVehiculosEmpresariales.dto.uso.UsoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.uso.UsoResponseDTO;
import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Uso;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.ReservaRepository;
import gestorDeVehiculosEmpresariales.repositories.UsoRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsoServiceTest {
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private VehiculoRepository vehiculoRepository;
    @Mock
    private EmpleadoRepository empleadoRepository;
    @Mock
    private UsoRepository usoRepository;

    @InjectMocks
    private UsoService usoService;

    private Vehiculo vehiculoDisponible(Long id) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(id);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);

        return vehiculo;
    }

    private Empleado empleadoHabilitado(Long id) {
        Empleado empleado = new Empleado();
        empleado.setId(id);
        empleado.setTieneRegistroConducir(Boolean.TRUE);
        empleado.setVencimientoLicencia(LocalDate.now().plusYears(10));

        return empleado;
    }

    @Test
    @DisplayName("rechaza un Uso cuando se superpone con una reserva")
    void rechazaUsoSiElVehiculoTieneUnaReservaVigente() {

        Empleado empleado = empleadoHabilitado(2L);

        Vehiculo vehiculo = vehiculoDisponible(1L);

        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(eq(1L), any(), any())).thenReturn(true);

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("tiene una reserva"));
        verify(usoRepository, never()).save(any());
    }

    @Test
    @DisplayName("rechaza el uso si un vehiculo no se encuentra disponible para utilizar")
    void rechazaUsoSiElVehiculoNoSeEncuentraDisponible() {
        Vehiculo vehiculo = vehiculoDisponible(1L);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.EN_USO);

        Empleado empleado = empleadoHabilitado(2L);

        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(eq(1L), any(), any())).thenReturn(false);

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("no está disponible"));
        verify(usoRepository, never()).save(any());
    }


    @Test
    @DisplayName("rechaza el uso si el empleado no tiene registro de conducir.")
    void rechazaUsoSiElEmpleadoNoTieneRegistro() {
        Vehiculo vehiculo = vehiculoDisponible(1L);

        Empleado empleado = empleadoHabilitado(2L);
        empleado.setTieneRegistroConducir(Boolean.FALSE);

        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(eq(1L), any(), any())).thenReturn(false);

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("no tiene registro"));
        verify(usoRepository, never()).save(any());
    }

    @Test
    @DisplayName("rechaza el uso si el empleado no tiene cargada la fecha de vencimiento de su licencia de conducir registrada")
    void rechazaUsoSiElEmpleadoNoTieneVencimientoDeLicenciaRegistrada() {
        Vehiculo vehiculo = vehiculoDisponible(1L);

        Empleado empleado = empleadoHabilitado(2L);
        empleado.setVencimientoLicencia(null);

        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(eq(1L), any(), any())).thenReturn(false);

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("no tiene fecha de vencimiento"));
        verify(usoRepository, never()).save(any());
    }

    @Test
    @DisplayName("rechaza el uso si el empleado tiene la licencia vencida")
    void rechazaUsoSiEmpleadoTieneVencidaLaLicencia() {
        Vehiculo vehiculo = vehiculoDisponible(1L);

        Empleado empleado = empleadoHabilitado(2L);
        empleado.setVencimientoLicencia(LocalDate.now().minusYears(1));

        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(eq(1L), any(), any())).thenReturn(false);

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("licencia del empleado ha vencido"));
        verify(usoRepository, never()).save(any());
    }

    @Test
    @DisplayName("rechaza el uso si ya existe un uso activo con este vehiculo")
    void rechazaUsoSiUnVehiculoEstaEnUso() {
        Vehiculo vehiculo = vehiculoDisponible(1L);

        Empleado empleado = empleadoHabilitado(2L);

        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(eq(1L), any(), any())).thenReturn(false);
        when(usoRepository.existsByVehiculoAndFechaFinalizacionIsNull(vehiculo)).thenReturn(true);

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("vehículo ya está en uso"));
        verify(usoRepository, never()).save(any());
    }


    @Test
    @DisplayName("rechaza el uso si el empleado ya tiene un uso activo")
    void rechazaSiElEmpleadoYaTieneUnUsoActivo() {
        Vehiculo vehiculo = vehiculoDisponible(1L);

        Empleado empleado = empleadoHabilitado(2L);

        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(eq(1L), any(), any())).thenReturn(false);
        when(usoRepository.existsByVehiculoAndFechaFinalizacionIsNull(vehiculo)).thenReturn(false);
        when(usoRepository.existsByEmpleadoAndFechaFinalizacionIsNull(empleado)).thenReturn(true);

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("empleado ya tiene un uso activo"));
        verify(usoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crea un Uso")
    void creaUnUso() {
        Vehiculo vehiculo = vehiculoDisponible(1L);

        Empleado empleado = empleadoHabilitado(2L);

        LocalDateTime inicio = LocalDateTime.now();

        Uso usoGuardado = new Uso();
        usoGuardado.setVehiculo(vehiculo);
        usoGuardado.setEmpleado(empleado);
        usoGuardado.setId(3L);
        usoGuardado.setFechaInicio(inicio);
        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(eq(1L), any(), any())).thenReturn(false);
        when(usoRepository.existsByVehiculoAndFechaFinalizacionIsNull(vehiculo)).thenReturn(false);
        when(usoRepository.existsByEmpleadoAndFechaFinalizacionIsNull(empleado)).thenReturn(false);
        when(usoRepository.save(any(Uso.class))).thenReturn(usoGuardado);

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        UsoResponseDTO resultado = usoService.comenzarUso(dto);

        assertEquals(3L, resultado.id());
        assertEquals(1L, resultado.vehiculo().id());
        assertEquals(2L, resultado.empleado().id());
        assertEquals(EstadoVehiculo.EN_USO, resultado.vehiculo().estadoVehiculo());
        assertEquals(inicio, resultado.fechaInicio());
        verify(usoRepository).save(any(Uso.class));
    }

    @Test
    @DisplayName("Lanza error porque el uso ya se ha finalizado")
    void ElUsoYaSeFinalizoYLanzaError() {
        Vehiculo vehiculo = vehiculoDisponible(2L);
        vehiculo.setKmActual(10000);

        Uso uso = new Uso();
        uso.setId(1L);
        uso.setVehiculo(vehiculo);
        uso.setFechaFinalizacion(LocalDateTime.now().minusHours(1));


        when(usoRepository.findById(1L)).thenReturn(Optional.of(uso));

        TerminarUsoDTO dto = new TerminarUsoDTO(vehiculo.getKmActual() + 100);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.terminarUso(1L, dto));

        assertTrue(ex.getMessage().contains("uso ya ha sido finalizado"));
        assertEquals(10000, vehiculo.getKmActual());
        assertEquals(EstadoVehiculo.DISPONIBLE, vehiculo.getEstadoVehiculo());
        verify(usoRepository, never()).save(any());

    }

    @Test
    @DisplayName("Lanza error porque el vehiculo no se encuentra en uso")
    void ElVehiculoNoSeEncuentraEnUso() {
        Vehiculo vehiculo = vehiculoDisponible(2L);
        vehiculo.setKmActual(10000);


        Uso uso = new Uso();
        uso.setId(1L);
        uso.setVehiculo(vehiculo);

        when(usoRepository.findById(1L)).thenReturn(Optional.of(uso));

        TerminarUsoDTO dto = new TerminarUsoDTO(vehiculo.getKmActual() + 100);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.terminarUso(1L, dto));

        assertTrue(ex.getMessage().contains("vehículo no está actualmente en uso"));
        assertEquals(10000, vehiculo.getKmActual());
        assertEquals(EstadoVehiculo.DISPONIBLE, vehiculo.getEstadoVehiculo());
        verify(usoRepository, never()).save(any());

    }

    @Test
    @DisplayName("Lanza error porque los km indicados al finalizar el uso son incorrectos")
    void LanzaErrorPorCargarMalLosKmAlFinalizarElUso() {
        Vehiculo vehiculo = vehiculoDisponible(2L);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.EN_USO);
        vehiculo.setKmActual(10000);


        Uso uso = new Uso();
        uso.setId(1L);
        uso.setVehiculo(vehiculo);

        when(usoRepository.findById(1L)).thenReturn(Optional.of(uso));

        TerminarUsoDTO dto = new TerminarUsoDTO(vehiculo.getKmActual() - 100);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> usoService.terminarUso(1L, dto));

        assertTrue(ex.getMessage().contains("kilómetros actualizados no pueden ser menores que los kilómetros actuales"));
        assertEquals(10000, vehiculo.getKmActual());
        assertEquals(EstadoVehiculo.EN_USO, vehiculo.getEstadoVehiculo());
        verify(usoRepository, never()).save(any());

    }

    @Test
    @DisplayName("Termina correctamente un Uso")
    void TerminaUnUsoCorrectamente() {
        Vehiculo vehiculo = vehiculoDisponible(2L);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.EN_USO);
        vehiculo.setKmActual(10000);

        Empleado empleado = empleadoHabilitado(3L);

        Uso uso = new Uso();
        uso.setId(1L);
        uso.setVehiculo(vehiculo);
        uso.setEmpleado(empleado);

        when(usoRepository.findById(1L)).thenReturn(Optional.of(uso));

        TerminarUsoDTO dto = new TerminarUsoDTO(vehiculo.getKmActual() + 100);


        UsoResponseDTO resultado = usoService.terminarUso(1L, dto);

        assertEquals(1L, resultado.id());
        assertEquals(2L, resultado.vehiculo().id());
        assertEquals(3L, resultado.empleado().id());
        assertEquals(EstadoVehiculo.DISPONIBLE, resultado.vehiculo().estadoVehiculo());
        assertEquals(10100, vehiculo.getKmActual());
    }

    @Test
    @DisplayName("lanza RecursoNoEncontrado si el vehículo no existe")
    void rechazaUsoSiElVehiculoNoExiste() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.empty());

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("Vehículo no encontrado"));
        verify(usoRepository, never()).save(any());
    }

    @Test
    @DisplayName("lanza RecursoNoEncontrado si el empleado no existe")
    void rechazaUsoSiElEmpleadoNoExiste() {
        Vehiculo vehiculo = vehiculoDisponible(1L);

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(empleadoRepository.findById(2L)).thenReturn(Optional.empty());

        UsoCreateDTO dto = new UsoCreateDTO(1L, 2L);

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> usoService.comenzarUso(dto));

        assertTrue(ex.getMessage().contains("Empleado no encontrado"));
        verify(usoRepository, never()).save(any());
    }

    @Test
    @DisplayName("lanza RecursoNoEncontrado al terminar un uso que no existe")
    void rechazaTerminarUsoSiElUsoNoExiste() {
        when(usoRepository.findById(99L)).thenReturn(Optional.empty());

        TerminarUsoDTO dto = new TerminarUsoDTO(15000);

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> usoService.terminarUso(99L, dto));

        assertTrue(ex.getMessage().contains("Uso no encontrado"));
    }
}

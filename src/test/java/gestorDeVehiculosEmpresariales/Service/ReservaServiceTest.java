package gestorDeVehiculosEmpresariales.Service;

import gestorDeVehiculosEmpresariales.dto.reserva.ReservaCreateDTO;
import gestorDeVehiculosEmpresariales.dto.reserva.ReservaResponseDTO;
import gestorDeVehiculosEmpresariales.entities.*;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.ReservaRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import gestorDeVehiculosEmpresariales.services.ReservaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private VehiculoRepository vehiculoRepository;
    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    @DisplayName("Rechaza una reserva cuya fecha de inicio es posterior a la de finalización")
    void rechazaReservaConFechasInvertidas() {
        // Arrange: preparar escenario
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        LocalDateTime fin = LocalDateTime.now().plusDays(2);

        ReservaCreateDTO dto = new ReservaCreateDTO(1L, inicio, fin, 1L);
        // Act: guardamos la excepcion para poder inspeccionarla
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> reservaService.saveReserva(dto));

        //Assert
        assertTrue(ex.getMessage().contains("después de la fecha de finalización"));
        verify(reservaRepository, never()).save(any());

    }

    @Test
    @DisplayName("Rechaza una reserva cuando un vehiculo tiene el estado EN_REPARACION")
    void rechazaReservaSiElVehiculoEstaEnReparacion() {

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.EN_REPARACION);

        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        LocalDateTime fin = LocalDateTime.now().plusDays(4);

        Empleado empleado = new Empleado();
        empleado.setId(1L);

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        ReservaCreateDTO dto = new ReservaCreateDTO(1L, inicio, fin, 1L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> reservaService.saveReserva(dto));

        assertTrue(ex.getMessage().contains("en reparación"));
        verify(reservaRepository, never()).save(any());

    }

    @Test
    @DisplayName("Rechaza una reserva cuando se superpone con otra reserva")
    void rechazaReservaSiSeSuperponeConOtraReserva() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        LocalDateTime fin = LocalDateTime.now().plusDays(4);

        Empleado empleado = new Empleado();
        empleado.setId(1L);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);


        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(1L, inicio, fin)).thenReturn(true);


        ReservaCreateDTO dto = new ReservaCreateDTO(1L, inicio, fin, 1L);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> reservaService.saveReserva(dto));

        assertTrue(ex.getMessage().contains("ya está reservado"));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crea la reserva cuando los datos ingresados son validos")
    void crearLaReservaCuandoLosDatosSonValidos() {

        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        LocalDateTime fin = LocalDateTime.now().plusDays(4);

        Empleado empleado = new Empleado();
        empleado.setId(1L);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setVehiculo(vehiculo);
        reservaGuardada.setFechaDeInicio(inicio);
        reservaGuardada.setFechaDeFinalizacion(fin);
        reservaGuardada.setId(1L);
        reservaGuardada.setEmpleado(empleado);
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(reservaRepository.existsOverlapping(1L, inicio, fin)).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);


        ReservaCreateDTO dto = new ReservaCreateDTO(1L, inicio, fin, 1L);

        ReservaResponseDTO resultado = reservaService.saveReserva(dto);

        assertEquals(1L, resultado.id());
        assertEquals(1L, resultado.vehiculo().id());
        assertEquals(1L, resultado.empleado().id());
        assertEquals(inicio, resultado.fechaDeInicio());
        assertEquals(fin, resultado.fechaDeFinalizacion());
        verify(reservaRepository).save(any(Reserva.class));
    }
}



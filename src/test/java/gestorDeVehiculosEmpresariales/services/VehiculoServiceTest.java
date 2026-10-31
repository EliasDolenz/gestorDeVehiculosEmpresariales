package gestorDeVehiculosEmpresariales.services;


import gestorDeVehiculosEmpresariales.entities.*;
import gestorDeVehiculosEmpresariales.repositories.NovedadRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {
    @Mock
    VehiculoRepository vehiculoRepository;

    @Mock
    NovedadRepository novedadRepository;

    @InjectMocks
    VehiculoService vehiculoService;

    private Vehiculo vehiculoDisponible(Long id) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(id);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);

        return vehiculo;
    }

    private Empleado empleado(Long id) {
        Empleado empleado = new Empleado();
        empleado.setId(id);

        return empleado;
    }

    @Test
    @DisplayName("Pone el vehiculo en estado EN_REPARACION si tiene novedades urgentes y estaba DISPONIBLE")
    void poneEnReparacionSiEstaDisponibleYTieneUrgentes() {

        Vehiculo vehiculo = vehiculoDisponible(1L);
        when(novedadRepository.countByVehiculoAndUrgenciaAndEstadoNovedadNot(any(Vehiculo.class), any(Urgencia.class), any(EstadoNovedad.class))).thenReturn(1L);

        vehiculoService.recalcularEstadoPorNovedades(vehiculo);

        assertEquals(EstadoVehiculo.EN_REPARACION, vehiculo.getEstadoVehiculo());
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    @DisplayName("No pisa estado EN_USO aunque tenga urgentes si ya estaba en uso")
    void noPisaEnUsoAunqueTengaUrgentes() {
        Vehiculo vehiculo = vehiculoDisponible(1L);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.EN_USO);
        when(novedadRepository.countByVehiculoAndUrgenciaAndEstadoNovedadNot(any(Vehiculo.class), any(Urgencia.class), any(EstadoNovedad.class))).thenReturn(1L);

        vehiculoService.recalcularEstadoPorNovedades(vehiculo);

        assertEquals(EstadoVehiculo.EN_USO, vehiculo.getEstadoVehiculo());
        verify(vehiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Libera el vehiculo si esta en reparación y no tiene urgentes")
    void liberaVehiculoSiNoTieneUrgentes() {
        Vehiculo vehiculo = vehiculoDisponible(1L);
        vehiculo.setEstadoVehiculo(EstadoVehiculo.EN_REPARACION);
        when(novedadRepository.countByVehiculoAndUrgenciaAndEstadoNovedadNot(any(Vehiculo.class), any(Urgencia.class), any(EstadoNovedad.class))).thenReturn(0L);

        vehiculoService.recalcularEstadoPorNovedades(vehiculo);

        assertEquals(EstadoVehiculo.DISPONIBLE, vehiculo.getEstadoVehiculo());
        verify(vehiculoRepository).save(vehiculo);
    }
}

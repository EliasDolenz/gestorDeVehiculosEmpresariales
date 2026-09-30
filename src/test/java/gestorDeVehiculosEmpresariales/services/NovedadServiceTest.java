package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.novedad.NovedadCreateDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadResponseDTO;
import gestorDeVehiculosEmpresariales.entities.*;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.NovedadRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NovedadServiceTest {
    @Mock
    NovedadRepository novedadRepository;

    @Mock
    VehiculoRepository vehiculoRepository;

    @Mock
    EmpleadoRepository empleadoRepository;

    @InjectMocks
    NovedadService novedadService;

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
    @DisplayName("Lanza RecursoNoEncontrado si el vehiculo no existe en el sistema")
    void rechazaNovedadSiVehiculoNoExiste() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.empty());

        NovedadCreateDTO dto = new NovedadCreateDTO("Test de creación", 1L, 2L, EstadoNovedad.EN_PROCESO, Urgencia.BAJA);

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () -> novedadService.saveNovedad(dto));

        assertEquals("El vehiculo que indica la novedad no se encuentra en el sistema", ex.getMessage());
        verify(novedadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanza RecursoNoEncontrado si el empleado no existe en el sistema")
    void rechazaNovedadSiEmpleadoNoExiste() {
        Vehiculo vehiculo = vehiculoDisponible(1L);

        when(empleadoRepository.findById(2L)).thenReturn(Optional.empty());
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        NovedadCreateDTO dto = new NovedadCreateDTO("Test de creación", 1L, 2L, EstadoNovedad.EN_PROCESO, Urgencia.BAJA);

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () -> novedadService.saveNovedad(dto));

        assertEquals("El empleado que indica la novedad no se encuentra en el sistema", ex.getMessage());
        verify(novedadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crea una nueva Novedad cuando los datos son validos")
    void crearNovedadCuandoLosDatosSonValidos() {
        Vehiculo vehiculo = vehiculoDisponible(1L);
        Empleado empleado = empleado(2L);
        Novedad novedad = new Novedad();
        novedad.setId(3L);
        novedad.setVehiculo(vehiculo);
        novedad.setEmpleado(empleado);
        novedad.setUrgencia(Urgencia.BAJA);
        novedad.setEstadoNovedad(EstadoNovedad.EN_PROCESO);
        novedad.setDescripcion("Test de creación");

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(novedadRepository.countByVehiculoAndUrgencia(vehiculo, Urgencia.INMEDIATA)).thenReturn(0L);
        when(novedadRepository.save(any(Novedad.class))).thenReturn(novedad);

        NovedadCreateDTO dto = new NovedadCreateDTO("Test de creación", 1L, 2L, EstadoNovedad.EN_PROCESO, Urgencia.BAJA);

        NovedadResponseDTO resultado = novedadService.saveNovedad(dto);

        ArgumentCaptor<Novedad> captor = ArgumentCaptor.forClass(Novedad.class);
        verify(novedadRepository).save(captor.capture());
        Novedad guardada = captor.getValue();

        assertEquals("Test de creación", guardada.getDescripcion());
        assertEquals(vehiculo, guardada.getVehiculo());
        assertEquals(EstadoVehiculo.DISPONIBLE, vehiculo.getEstadoVehiculo());
        assertEquals(empleado, guardada.getEmpleado());
        assertNotNull(guardada.getFechaReporte());
        assertNotNull(resultado);
        assertEquals(3L,resultado.id());
        verify(vehiculoRepository, never()).save(any());

    }

    @Test
    @DisplayName("Crea una nueva Novedad cuando los datos son validos y tiene Urgencia Inmediata, poniendo el vehiculo enreparación")
    void poneElVehiculoEnReparacionSiLaNovedadEsInmediata() {
        Vehiculo vehiculo = vehiculoDisponible(1L);
        Empleado empleado = empleado(2L);
        Novedad novedad = new Novedad();
        novedad.setId(3L);
        novedad.setVehiculo(vehiculo);
        novedad.setEmpleado(empleado);
        novedad.setUrgencia(Urgencia.INMEDIATA);
        novedad.setEstadoNovedad(EstadoNovedad.EN_PROCESO);
        novedad.setDescripcion("Test de creación");

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(novedadRepository.countByVehiculoAndUrgencia(vehiculo, Urgencia.INMEDIATA)).thenReturn(1L);
        when(novedadRepository.save(any(Novedad.class))).thenReturn(novedad);

        NovedadCreateDTO dto = new NovedadCreateDTO("Test de creación", 1L, 2L, EstadoNovedad.EN_PROCESO, Urgencia.INMEDIATA);

        NovedadResponseDTO resultado = novedadService.saveNovedad(dto);

        ArgumentCaptor<Novedad> captor = ArgumentCaptor.forClass(Novedad.class);
        verify(novedadRepository).save(captor.capture());
        Novedad guardada = captor.getValue();

        assertEquals("Test de creación", guardada.getDescripcion());
        assertEquals(vehiculo, guardada.getVehiculo());
        assertEquals(empleado, guardada.getEmpleado());
        assertEquals(EstadoVehiculo.EN_REPARACION, vehiculo.getEstadoVehiculo());
        assertNotNull(guardada.getFechaReporte());
        assertNotNull(resultado);
        assertEquals(3L,resultado.id());

    }
}

package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaCreateDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaResponseDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadResponseDTO;
import gestorDeVehiculosEmpresariales.entities.Departamento;
import gestorDeVehiculosEmpresariales.entities.Empresa;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.repositories.DepartamentoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpresaRepository;
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
class EmpresaServiceTest {

    @Mock
    EmpresaRepository empresaRepository;

    @Mock
    DepartamentoRepository departamentoRepository;

    @InjectMocks
    EmpresaService empresaService;

    @Test
    @DisplayName("Lanza ReglaDeNegocioException si una empresa ya existe con esa dirección")
    void rechazaEmpresaSiYaExisteConEsaDireccion() {
        when(empresaRepository.existsByDireccion("Camacua 287")).thenReturn(true);

        EmpresaCreateDTO dto = new EmpresaCreateDTO("Empresa Test", "Camacua 287");

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> empresaService.saveEmpresa(dto));

        assertTrue(ex.getMessage().contains("Ya existe una empresa con esa dirección"));
        verify(empresaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanza RecursoNoEncontradoException si la empresa no existe en el sistema")
    void rechazaEmpresaSiNoExiste() {

        when(empresaRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () -> empresaService.findEmpresaById(1L));

        assertTrue(ex.getMessage().contains("No se encontró la empresa con el ID"));
    }

    @Test
    @DisplayName("Lanza ReglaDeNegocioException si al eliminar una empresa esta tiene departamentos asociados")
    void rechazaEliminarEmpresaSiTieneDepartamentosAsociados() {

        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.getDepartamentos().add(new Departamento());

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));


        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> empresaService.deleteEmpresa(1L));
        assertTrue(ex.getMessage().contains("No se puede eliminar la empresa porque tiene departamentos asociados"));
        verify(empresaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Elimina correctamente una empresa")
    void eliminaEmpresaSiNoTieneDepartamentosAsociados() {

        Empresa empresa = new Empresa();
        empresa.setId(1L);

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        empresaService.deleteEmpresa(1L);
        verify(empresaRepository).delete(empresa);
    }

    @Test
    @DisplayName("Guarda correctamente una empresa")
    void guardaEmpresaCorrectamente() {
        Empresa empresa = new Empresa();
        empresa.setDireccion("Camacua 287");
        empresa.setId(1L);
        empresa.setNombre("Empresa Test");
        empresa.getDepartamentos().add(new Departamento());

        when(empresaRepository.existsByDireccion("Camacua 287")).thenReturn(false);
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        EmpresaCreateDTO dto = new EmpresaCreateDTO("Empresa Test", "Camacua 287");

        EmpresaResponseDTO resultado = empresaService.saveEmpresa(dto);

        ArgumentCaptor<Empresa> captor = ArgumentCaptor.forClass(Empresa.class);
        verify(empresaRepository).save(captor.capture());
        Empresa guardada = captor.getValue();

        assertEquals("Camacua 287", guardada.getDireccion());
        assertEquals("Empresa Test", guardada.getNombre());
        assertEquals(1L, resultado.id());
        assertNotNull(resultado);
    }
}

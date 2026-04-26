package gestorDeVehiculosEmpresariales.dto.novedad;

import gestorDeVehiculosEmpresariales.entities.EstadoNovedad;
import gestorDeVehiculosEmpresariales.entities.Urgencia;

public record NovedadUpdateDTO(
        String descripcion,
        EstadoNovedad estadoNovedad,
        Urgencia urgencia
) {
}

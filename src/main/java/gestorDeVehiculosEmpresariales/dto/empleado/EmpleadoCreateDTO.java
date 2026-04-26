package gestorDeVehiculosEmpresariales.dto.empleado;

public record EmpleadoCreateDTO(
        String nombre,
        String apellido,
        String numeroTelefono,
        Long departamentoId,
        String correoElectronico,
        String puesto,
        Boolean tieneRegistroConducir,
        String vencimientoLicencia,
        String pinCarga,
        Long empresaId
) {
}

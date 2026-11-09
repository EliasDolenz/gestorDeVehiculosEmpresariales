ALTER TABLE vehiculos DROP CONSTRAINT vehiculos_estado_vehiculo_check;
UPDATE vehiculos SET estado_vehiculo = 'DISPONIBLE' WHERE estado_vehiculo = 'RESERVADO';
ALTER TABLE vehiculos ADD CONSTRAINT vehiculos_estado_vehiculo_check CHECK (estado_vehiculo IN ('DISPONIBLE', 'EN_USO', 'EN_REPARACION'));
ALTER TABLE VEHICULOS DROP CONSTRAINT vehiculos_nivel_combustible_check;
UPDATE vehiculos SET nivel_combustible = 'UN_CUARTO' WHERE nivel_combustible = 'UN_CUARTE';
ALTER TABLE vehiculos ADD CONSTRAINT vehiculos_nivel_combustible_check CHECK (nivel_combustible IN ('RESERVA', 'UN_CUARTO', 'MEDIO', 'TRES_CUARTOS', 'LLENO'));
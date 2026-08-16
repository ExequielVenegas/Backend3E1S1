package cl.duoc.bancoxyz.legacy_batch_migrator.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InteresEntity {
    private int cuentaId;
    private String nombre;
    private String tipo;
    private double saldoOriginal;
    private double interesAplicado;
    private double saldoFinal;
}
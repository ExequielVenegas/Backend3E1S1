package cl.duoc.bancoxyz.legacy_batch_migrator.model;

import lombok.Data;

@Data
public class InteresDTO {
    private int cuentaId;
    private String nombre;
    private double saldo;
    private int edad;
    private String tipo;
}
package cl.duoc.bancoxyz.legacy_batch_migrator.model;

import lombok.Data;

@Data
public class TransaccionDTO {
    private int id;
    private String fecha;
    private double monto;
    private String tipo;
}
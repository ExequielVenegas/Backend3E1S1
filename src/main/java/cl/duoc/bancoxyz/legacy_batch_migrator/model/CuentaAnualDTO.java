package cl.duoc.bancoxyz.legacy_batch_migrator.model;

import lombok.Data;

@Data
public class CuentaAnualDTO {
    private int cuentaId;
    private String fecha;
    private String transaccion;
    private double monto;
    private String descripcion;
}
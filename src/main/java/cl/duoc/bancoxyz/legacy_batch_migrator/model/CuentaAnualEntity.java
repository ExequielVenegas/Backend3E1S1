package cl.duoc.bancoxyz.legacy_batch_migrator.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;


@Data
@Builder
public class CuentaAnualEntity {
    private int cuentaId;
    private LocalDate fecha;
    private String tipoTransaccion;
    private double monto;
    private String descripcion;
    private boolean auditado;
}
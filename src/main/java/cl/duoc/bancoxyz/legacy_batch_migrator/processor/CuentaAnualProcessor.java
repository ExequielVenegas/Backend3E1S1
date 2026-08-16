package cl.duoc.bancoxyz.legacy_batch_migrator.processor;

import cl.duoc.bancoxyz.legacy_batch_migrator.model.CuentaAnualDTO;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.CuentaAnualEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.time.LocalDate;

@Slf4j
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnualDTO, CuentaAnualEntity> {

    @Override
    public CuentaAnualEntity process(CuentaAnualDTO item) throws Exception {

        if (item.getMonto() == 0) {
            log.warn("Registro anómalo omitido: La cuenta {} tiene una transacción con monto 0", item.getCuentaId());
            return null;
        }

        log.info("Procesando registro anual para cuenta ID: {}", item.getCuentaId());

        return CuentaAnualEntity.builder()
                .cuentaId(item.getCuentaId())
                .fecha(LocalDate.parse(item.getFecha()))
                .tipoTransaccion(item.getTransaccion().toUpperCase())
                .monto(item.getMonto())
                .descripcion(item.getDescripcion())
                .auditado(true)
                .build();
    }
}
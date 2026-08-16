package cl.duoc.bancoxyz.legacy_batch_migrator.processor;

import cl.duoc.bancoxyz.legacy_batch_migrator.model.TransaccionEntity;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.TransaccionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.time.LocalDate;

@Slf4j
public class TransaccionProcessor implements ItemProcessor<TransaccionDTO, TransaccionEntity> {

    @Override
    public TransaccionEntity process(TransaccionDTO item) throws Exception {

        if (item.getMonto() <= 0) {
            log.warn("Anomalía detectada y omitida: Monto inválido ({}) en transacción ID {}", item.getMonto(), item.getId());
            return null;
        }

        log.info("Procesando transacción válida ID: {}", item.getId());

        return TransaccionEntity.builder()
                .id(item.getId())
                .fecha(LocalDate.parse(item.getFecha()))
                .monto(item.getMonto())
                .tipo(item.getTipo())
                .estado("PROCESADO_OK")
                .build();
    }
}
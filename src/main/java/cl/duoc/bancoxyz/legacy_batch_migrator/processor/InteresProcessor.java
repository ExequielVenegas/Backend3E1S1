package cl.duoc.bancoxyz.legacy_batch_migrator.processor;

import cl.duoc.bancoxyz.legacy_batch_migrator.model.InteresDTO;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.InteresEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@Slf4j
public class InteresProcessor implements ItemProcessor<InteresDTO, InteresEntity> {

    @Override
    public InteresEntity process(InteresDTO item) throws Exception {

        if (item.getSaldo() < 0 || item.getTipo() == null || item.getTipo().trim().isEmpty()) {
            log.warn("Anomalía detectada: Cuenta ID {} con saldo negativo o tipo no definido. Omitiendo.", item.getCuentaId());
            return null;
        }

        log.info("Calculando intereses para cuenta ID: {} (Tipo: {})", item.getCuentaId(), item.getTipo());

        double tasaInteres = switch (item.getTipo().toLowerCase()) {
            case "ahorro" -> 0.03;
            case "prestamo" -> 0.05;
            case "hipoteca" -> 0.04;
            default -> 0.01;
        };

        double montoInteres = item.getSaldo() * tasaInteres;

        return InteresEntity.builder()
                .cuentaId(item.getCuentaId())
                .nombre(item.getNombre())
                .tipo(item.getTipo())
                .saldoOriginal(item.getSaldo())
                .interesAplicado(montoInteres)
                .saldoFinal(item.getSaldo() + montoInteres)
                .build();
    }
}
package cl.duoc.bancoxyz.legacy_batch_migrator.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;

@Slf4j
public class TrazabilidadJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("======================================================");
        log.info("INICIANDO BATCH JOB: {}", jobExecution.getJobInstance().getJobName());
        log.info("======================================================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("======================================================");
        log.info("FIN DEL BATCH JOB: {} | ESTADO FINAL: {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus());
        log.info("======================================================");
    }

}
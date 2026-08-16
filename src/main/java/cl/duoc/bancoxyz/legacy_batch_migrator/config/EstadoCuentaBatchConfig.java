package cl.duoc.bancoxyz.legacy_batch_migrator.config;

import cl.duoc.bancoxyz.legacy_batch_migrator.listener.TrazabilidadJobListener;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.CuentaAnualDTO;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.CuentaAnualEntity;
import cl.duoc.bancoxyz.legacy_batch_migrator.processor.CuentaAnualProcessor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class EstadoCuentaBatchConfig {

    @Bean
    public ItemReader<CuentaAnualDTO> cuentaAnualReader() {
        return new FlatFileItemReaderBuilder<CuentaAnualDTO>()
                .name("cuentaAnualReader")
                .resource(new ClassPathResource("input/cuentas_anuales.csv"))
                .linesToSkip(1)
                .delimited()
                .names("cuentaId", "fecha", "transaccion", "monto", "descripcion")
                .targetType(CuentaAnualDTO.class)
                .build();
    }

    @Bean
    public ItemProcessor<CuentaAnualDTO, CuentaAnualEntity> cuentaAnualProcessor() {
        return new CuentaAnualProcessor();
    }

    @Bean
    public ItemWriter<CuentaAnualEntity> cuentaAnualWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CuentaAnualEntity>()
                .dataSource(dataSource)
                .sql("INSERT INTO estado_cuenta_anual (cuenta_id, fecha, tipo_transaccion, monto, descripcion, auditado) " +
                        "VALUES (:cuentaId, :fecha, :tipoTransaccion, :monto, :descripcion, :auditado)")
                .beanMapped()
                .build();
    }

    @Bean
    @SuppressWarnings({"removal", "deprecation"})
    public Step estadoCuentaStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ItemReader<CuentaAnualDTO> cuentaAnualReader,
                                 ItemProcessor<CuentaAnualDTO, CuentaAnualEntity> cuentaAnualProcessor,
                                 ItemWriter<CuentaAnualEntity> cuentaAnualWriter) {
        return new StepBuilder("estadoCuentaStep", jobRepository)
                .<CuentaAnualDTO, CuentaAnualEntity>chunk(100, transactionManager)
                .reader(cuentaAnualReader)
                .processor(cuentaAnualProcessor)
                .writer(cuentaAnualWriter)
                .faultTolerant()
                .skipLimit(15)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Job generacionEstadosCuentaJob(JobRepository jobRepository, Step estadoCuentaStep) {
        return new JobBuilder("generacionEstadosCuentaJob", jobRepository)
                .listener(new TrazabilidadJobListener())
                .start(estadoCuentaStep)
                .build();
    }
}
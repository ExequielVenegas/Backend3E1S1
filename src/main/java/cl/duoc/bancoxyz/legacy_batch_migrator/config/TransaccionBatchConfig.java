package cl.duoc.bancoxyz.legacy_batch_migrator.config;

import cl.duoc.bancoxyz.legacy_batch_migrator.listener.TrazabilidadJobListener;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.TransaccionEntity;
import cl.duoc.bancoxyz.legacy_batch_migrator.processor.TransaccionProcessor;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.TransaccionDTO;
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
public class TransaccionBatchConfig {

    @Bean
    public ItemReader<TransaccionDTO> transaccionReader() {
        return new FlatFileItemReaderBuilder<TransaccionDTO>()
                .name("transaccionReader")
                .resource(new ClassPathResource("input/transacciones.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .targetType(TransaccionDTO.class)
                .build();
    }


    @Bean
    public ItemProcessor<TransaccionDTO, TransaccionEntity> transaccionProcessor() {
        return new TransaccionProcessor();
    }

    @Bean
    public ItemWriter<TransaccionEntity> transaccionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<TransaccionEntity>()
                .dataSource(dataSource)
                .sql("INSERT INTO transacciones_diarias (id, fecha, monto, tipo, estado) VALUES (:id, :fecha, :monto, :tipo, :estado)")
                .beanMapped()
                .build();
    }

    @Bean
    @SuppressWarnings({"removal", "deprecation"})
    public Step transaccionStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                ItemReader<TransaccionDTO> lector,
                                ItemProcessor<TransaccionDTO, TransaccionEntity> procesador,
                                ItemWriter<TransaccionEntity> escritor) {
        return new StepBuilder("transaccionStep", jobRepository)
                .<TransaccionDTO, TransaccionEntity>chunk(100, transactionManager)
                .reader(lector)
                .processor(procesador)
                .writer(escritor)
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Job reporteTransaccionesJob(JobRepository jobRepository, Step transaccionStep) {
        return new JobBuilder("reporteTransaccionesJob", jobRepository)
                .listener(new TrazabilidadJobListener())
                .start(transaccionStep)
                .build();
    }
}
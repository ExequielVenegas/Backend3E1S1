package cl.duoc.bancoxyz.legacy_batch_migrator.config;

import cl.duoc.bancoxyz.legacy_batch_migrator.listener.TrazabilidadJobListener;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.InteresDTO;
import cl.duoc.bancoxyz.legacy_batch_migrator.model.InteresEntity;
import cl.duoc.bancoxyz.legacy_batch_migrator.processor.InteresProcessor;
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
public class InteresBatchConfig {

    @Bean
    public ItemReader<InteresDTO> interesReader() {
        return new FlatFileItemReaderBuilder<InteresDTO>()
                .name("interesReader")
                .resource(new ClassPathResource("input/intereses.csv"))
                .linesToSkip(1)
                .delimited()
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .targetType(InteresDTO.class)
                .build();
    }

    @Bean
    public ItemProcessor<InteresDTO, InteresEntity> interesProcessor() {
        return new InteresProcessor();
    }

    @Bean
    public ItemWriter<InteresEntity> interesWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<InteresEntity>()
                .dataSource(dataSource)
                .sql("INSERT INTO calculo_intereses (cuenta_id, nombre, tipo, saldo_original, interes_aplicado, saldo_final) " +
                        "VALUES (:cuentaId, :nombre, :tipo, :saldoOriginal, :interesAplicado, :saldoFinal)")
                .beanMapped()
                .build();
    }

    @Bean
    @SuppressWarnings({"removal", "deprecation"})
    public Step interesStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ItemReader<InteresDTO> lector,
                            ItemProcessor<InteresDTO, InteresEntity> procesador,
                            ItemWriter<InteresEntity> escritor) {
        return new StepBuilder("interesStep", jobRepository)
                .<InteresDTO, InteresEntity>chunk(50, transactionManager)
                .reader(lector)
                .processor(procesador)
                .writer(escritor)
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Job calculoInteresesJob(JobRepository jobRepository, Step interesStep) {
        return new JobBuilder("calculoInteresesJob", jobRepository)
                .listener(new TrazabilidadJobListener())
                .start(interesStep)
                .build();
    }
}
Descripción / Objetivo
----------------------
Este proyecto es una aplicación Spring Boot que implementa varios jobs de Spring Batch para migrar/procesar archivos CSV y persistir resultados en una base de datos MySQL. Los jobs incluidos son:

- calculoInteresesJob: procesa `input/intereses.csv` y guarda resultados en la tabla `calculo_intereses`.
- reporteTransaccionesJob: procesa `input/transacciones.csv` y guarda resultados en la tabla `transacciones_diarias`.
- generacionEstadosCuentaJob: procesa `input/cuentas_anuales.csv` y guarda resultados en la tabla `estado_cuenta_anual`.

Estructura del código
---------------------
Raíz del proyecto:

- `pom.xml` — configuración de Maven (Spring Boot, Spring Batch, JDBC, Lombok, MySQL connector). Java: 21.
- `mvnw`, `mvnw.cmd` — Maven wrapper (útil para ejecutar sin Maven instalado globalmente).
- `src/main/java/.../config/` — clases de configuración de Spring Batch:
  - `InteresBatchConfig.java` (job `calculoInteresesJob`)
  - `TransaccionBatchConfig.java` (job `reporteTransaccionesJob`)
  - `EstadoCuentaBatchConfig.java` (job `generacionEstadosCuentaJob`)

- `src/main/java/.../processor/` — procesadores (transforman DTOs a Entity y aplican lógica de negocio).
- `src/main/java/.../model/` — DTOs y Entities (mapeos para lectura/escritura y persistencia).
- `src/main/java/.../listener/` — listener de trazabilidad de jobs (`TrazabilidadJobListener`).
- `src/main/resources/application.properties` — configuración (conexión a MySQL, propiedades de Spring Batch, job por defecto).
- `src/main/resources/input/` — archivos CSV de ejemplo:
  - `intereses.csv`
  - `transacciones.csv`
  - `cuentas_anuales.csv`
- `src/main/resources/script/` — scripts SQL para crear las tablas de destino:
  - `calculoInteres.sql`
  - `transacciones.sql`
  - `estadocuentaanual.sql`

Requisitos
----------
- Java 21 (el proyecto está configurado con `<java.version>21</java.version>` en `pom.xml`).
- MySQL (o servidor compatible JDBC). Crear una base de datos (por defecto `lab_batch`) o ajustar la URL en `application.properties`.
- Opcional: Maven (no es obligatorio si usas los scripts `mvnw`/`mvnw.cmd`).

Configuración importante
------------------------
Edita `src/main/resources/application.properties` para indicar tu conexión a la base de datos:

- `spring.datasource.url=jdbc:mysql://localhost:3306/lab_batch`
- `spring.datasource.username=root`
- `spring.datasource.password=root`

Nota: Los scripts en `src/main/resources/script/` contienen las definiciones propuestas para las tablas de destino (calculo_intereses, transacciones_diarias, estado_cuenta_anual).

Compilar y ejecutar
--------------------
Los ejemplos siguiente asumen Windows PowerShell (tienes `mvnw.cmd` en el proyecto). Ejecuta desde la raíz del proyecto.

1) Compilar (omitiendo tests):

```powershell
.\mvnw.cmd -DskipTests package
```

2) Ejecutar con el plugin de Spring Boot (arranca la app y ejecuta el job configurado):

```powershell
.\mvnw.cmd spring-boot:run
```

3) Ejecutar el JAR generado (después de `package`):

```powershell
java -jar .\target\legacy-batch-migrator-0.0.1-SNAPSHOT.jar
```

4) Ejecutar un job específico

Por defecto en `application.properties` está habilitado `spring.batch.job.enabled=true` y `spring.batch.job.name=calculoInteresesJob`. Para ejecutar otro job, puedes sobrescribir la propiedad en tiempo de ejecución.

Con `java -jar`:

```powershell
java -jar .\target\legacy-batch-migrator-0.0.1-SNAPSHOT.jar --spring.batch.job.name=reporteTransaccionesJob
```

Con el plugin `spring-boot:run` (ejemplo para PowerShell / mvnw.cmd):

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.batch.job.name=generacionEstadosCuentaJob"
```

Alternativa: pasar la propiedad JVM a Maven (menos recomendado para argumentos de Spring Boot):

```powershell
.\mvnw.cmd -Dspring.batch.job.name=reporteTransaccionesJob spring-boot:run
```

5) Ejecutar tests:

```powershell
.\mvnw.cmd test
```

Notas y buenas prácticas
-----------------------
- Asegúrate de que el usuario de la base de datos tiene permisos para crear tablas si vas a usar los scripts o `spring.batch.jdbc.initialize-schema=always`.
- Revisa `src/main/resources/script/` para crear manualmente las tablas de destino si prefieres control total sobre los esquemas.
- Los archivos de entrada de ejemplo están en `src/main/resources/input/`; puedes sustituirlos por tus propios CSV respetando los nombres de columnas definidos en las configuraciones (`*.names(...)` en las clases de configuración).
- Si usas un IDE (IntelliJ/Eclipse), puedes ejecutar la clase principal `LegacyBatchMigratorApplication` directamente.

Problemas comunes
-----------------
- Conector MySQL no encontrado: revisa que `mysql-connector-j` está en `pom.xml` y que la dependencia se descargó correctamente; al usar `mvnw` esto se gestiona automáticamente.
- Error de versión de Java: usa Java 21 o cambia `<java.version>` en `pom.xml` acorde a tu JDK.
- El job no se ejecuta: comprueba `spring.batch.job.enabled` y `spring.batch.job.name` en `application.properties` o sobrescribe al iniciar.



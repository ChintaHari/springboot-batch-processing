# Spring Boot Batch Processing Application

## Introduction

This Spring Boot application is designed to demonstrate the capabilities of batch processing using Spring Batch. Batch processing is essential for handling large volumes of data efficiently without user interaction. Applications like data migration, bulk updates, and complex calculations on large datasets are typical use-cases for batch processing.

## Why Batch Processing?

Batch processing is crucial in scenarios where we need to process high volumes of data with minimal supervision, ensuring efficient resource management and performance optimization. It allows for the automation of repetitive tasks, reducing the likelihood of human error and freeing up resources for other critical tasks.

## Architecture Overview

The architecture of this Spring Boot Batch Processing application revolves around several core components:

- **JobLauncher**: Launches jobs with given parameters.
- **Job**: Represents the batch job. Each job can have multiple steps.
- **Step**: Represents a single step in a job consisting of an ItemReader, ItemProcessor, and ItemWriter.
- **ItemReader**: Reads input data one item at a time.
- **ItemProcessor**: Processes the input item, providing an opportunity to apply business rules.
- **ItemWriter**: Writes the processed data to the destination.
- **TaskExecutor**: Manages asynchronous tasks to enhance performance.

![Batch Processing Architecture](/images/DetailedArchitecture.png)

The system is designed to leverage asynchronous tasks, enabling it to handle large datasets more efficiently by paralleling multiple parts of the process.

## Key Configuration

### Dependencies

To run this Spring Boot Batch Processing application, the following dependencies are essential:

- **spring-boot-starter-batch**: Core Spring Batch functionality, facilitating the creation and execution of batch jobs.
- **spring-boot-starter-data-jpa**: Provides integration with Spring Data JPA for database operations.
- **spring-boot-starter-web**: Required for building RESTful web applications.
- **mysql-connector-java**: MySQL JDBC driver for database connectivity.
- **spring-boot-starter-test**: Support for testing Spring Boot applications with libraries like JUnit.
- **lombok**: Reduces boilerplate code with auto-generated getters, setters, and other utility methods.

## Core Components

### ItemReader

The `ItemReader` reads records from a CSV file and converts each CSV row into a `Customer` object. It skips the first line, assuming it's a header.

```java
@Bean
public FlatFileItemReader<Customer> reader() {
    FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
    itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
    itemReader.setName("csvReader");
    itemReader.setLinesToSkip(1);
    itemReader.setLineMapper(lineMapper());
    return itemReader;
}
```

### ItemProcessor

The ItemProcessor in this application is straightforward; it receives and returns the Customer object without modification. This can be extended to include data validation, logging, or complex transformations.

```java
public class ItemProcessing implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        return customer; // Can be modified to include business logic.
    }
}
```

### ItemWriter

The ItemWriter writes the processed Customer objects to the database using Spring Data JPA. It uses the CustomerRepository to save the customer data.

```java
@Bean
public RepositoryItemWriter<Customer> writer() {
    RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
    writer.setRepository(customerRepository);
    writer.setMethodName("save");
    return writer;
}
```

### Asynchronous Execution

To enhance performance, the application uses a SimpleAsyncTaskExecutor to execute steps concurrently.

```java
@Bean
public TaskExecutor taskExecutor() {
    SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
    asyncTaskExecutor.setConcurrencyLimit(30);
    return asyncTaskExecutor;
}
```

### Step Configuration

Each step is configured to process a chunk of items to optimize memory usage and processing speed. The step configuration integrates the reader, processor, and writer into a single transactional step.

```java
@Bean
public Step step1() {
    return new StepBuilder("csvImport", jobRepository)
            .<Customer, Customer>chunk(1000, platformTransactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .taskExecutor(taskExecutor())
            .build();
}
```

### Job Configuration

Jobs are defined with steps that include reading, processing, and writing components, configured to handle transactions seamlessly and ensure data integrity.

```java
@Bean
public Job runJob() {
    return new JobBuilder("importStudents", jobRepository)
            .start(step1())
            .build();
}
```

## Understanding Spring Batch Metadata Tables

When the Spring Boot Batch Processing application starts, Spring Batch automatically creates several metadata tables alongside the application-specific entities. These tables are crucial for managing and monitoring the state of batch operations within the application. Here's why they are essential:

- **Metadata Tables and Their Significance:**
  - `batch_job_execution`: Records the details of each job execution.
  - `batch_job_execution_context`: Stores the context data (like execution parameters) for job executions.
  - `batch_job_execution_params`: Holds parameters used by job instances.
  - `batch_job_execution_seq`: Sequence table for generating primary keys for job executions.
  - `batch_job_instance`: Maintains information about each distinct job configuration.
  - `batch_job_seq`: Sequence table for generating primary keys for job instances.
  - `batch_step_execution`: Contains details about each step within a job execution.
  - `batch_step_execution_context`: Stores context information related to step executions.
  - `batch_step_execution_seq`: Sequence table for generating primary keys for step executions.

### Why Are These Tables Used?

These tables are fundamental to the framework’s ability to restart a job from a point of failure, track job histories, and correlate job executions with their respective outcomes. By leveraging these metadata tables, Spring Batch offers robust support for managing complex batch processing operations, ensuring consistency and recoverability in case of failures. The persistence of such data also facilitates auditing and monitoring of job and step executions, proving invaluable for maintaining the integrity and efficiency of batch operations.

This framework's architecture not only provides out-of-the-box mechanisms for managing job states but also enhances the application’s capability to perform large-scale data processing tasks within enterprise environments.


## Testing with Postman

### Testing without Asynchronous Tasks

1. **Set up a Postman request** to trigger the batch process. Configure a POST request pointing to the endpoint `/job/start`.
2. **Send the request** and measure the time taken for the batch process to complete using Postman's built-in timer.

![Synchronous Run](/images/Synchronous.png)


### Testing with Asynchronous Tasks

1. **Enable the asynchronous tasks** in the configuration by setting the concurrency limit on the `taskExecutor`.
2. **Send the same request** as above in Postman.
3. **Observe the reduction in time taken** to process the same amount of records due to parallel processing.
![Asynchronous Run](/images/Asynchronous.png)

### Data in MySQL Workbench

![Data](/images/Data.png)

This standard process and architecture ensure that the Spring Boot application is scalable, efficient, and suitable for enterprise-level batch processing tasks.

## Conclusion

This application demonstrates the power and flexibility of Spring Boot for batch processing, highlighting how it can be tailored to meet the demands of large-scale data handling scenarios. By utilizing Spring Batch's robust framework, the application efficiently processes large datasets with transactional integrity and minimal performance overhead.







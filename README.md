# SURVEY APP

Dynata interview task

## Stack

- Java 21
- Spring Boot 3.5.11
- Maven

## Documentation used

- OpenCSV: https://opencsv.sourceforge.net/#reading_into_beans
- Optional list: https://docs.oracle.com/javase/9/docs/api/java/util/Optional.html#stream--
- Counting in Stream API: https://www.geeksforgeeks.org/java/collectors-groupingby-method-in-java-with-examples/

## AI usage

Documentation and unit tests are generated using IntelliJ ultimate AI.
I also used Gemini AI for consultation and code review.

### Prompts

- prompt: "I am creating a Spring Boot application write prompt for refactoring suggestions."
- prompt: "I am creating a Spring Boot application where I read data from CSV files and store it in memory within a @Repository bean. It will serve multiple concurrent REST API requests, what is the best Collection implementation to use for storing this data to ensure thread safety and high performance? Please explain why."
- prompt: "I have the following Java code for a Spring Boot service that processes and filters data from multiple in-memory collections. Can you refactor this to be more idiomatic and 'Spring-like'?
<br/>Please focus on:
<br/>Using Java Stream API for cleaner data transformation.
Improving readability and reducing boilerplate.
Following SOLID principles (especially Single Responsibility).
Ensuring the code is easily unit-testable.
<br/>[INSERT YOUR CODE HERE]"

## Ideas for further developement

- caching
- logging
- observing logs/traces/metrics
- storing data in database
- integration tests
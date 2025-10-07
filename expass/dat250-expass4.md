# Experiment assignment 4 - Jakarta Persistence API (JPA)

### Technical problems during installation and use of Java Persistence Architecture (JPA)

- Working with Hibernate was relatively straight forward, although i did encounter issues regarding types as I initially had userId as a String, but it should be a Long. This was quickly resolved by changing the type.

### Links and screenshots

- Link to my code: [DAT250_expass4](https://github.com/frheg/DAT250_expass4)
- Passing test case screenshot: ![alt text](<img/Screenshot 2025-09-22 at 19.11.46.png>)

### Explanation of how I inspected the database tables and what tables were created.

- I inspected the database directly from `PollsTest.java` test by running simple SQL against `INFORMATION_SCHEMA`. The helper prints the tables and their columns that Hibernate generated for my entities.

- Run the tests from the terminal: `./gradlew test --rerun-tasks`.

- Screenshot: ![alt text](<img/Screenshot 2025-09-22 at 20.12.53.png>)

### Pending issues

- All tests are currently passing, but I would like to keep complete functionality with the PollManager class, which is currently not fully integrated with JPA.

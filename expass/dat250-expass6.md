# Experiment assignment 6 - RabbitMQ

### Technical problems that you encountered during installation and use of Redis and how you resolved

I had issues with dependencies in imports and circular dependencies between my RabbitMQService and PollManager as both autowired eachother. Solution was to use Lazy annotation in RabbitMQService on the PollManager autowiring.

I also had some issues with different dependencies from earlier assignments getting broken when trying to refactor my code, but I managed to resolve the issues with debugging and a lot of `./gradlew clean build`.

### Any pending issues with this assignment which you did not manage to solve

No known pending issues, but room for improvement and better functionality as it is quite basic right now.

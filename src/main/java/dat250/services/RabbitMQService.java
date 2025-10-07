package dat250.services;

import com.rabbitmq.client.*;
import dat250.models.Vote;
import dat250.models.User;
import dat250.services.PollManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMQService {
    private static final String EXCHANGE_NAME = "poll_events";

    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${rabbitmq.password}")
    private String rabbitmqPassword;

    private Connection connection;
    private Channel channel;

    @Autowired
    @Lazy
    private PollManager pollManager;

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitmqHost);
            factory.setPort(rabbitmqPort);
            factory.setUsername(rabbitmqUsername);
            factory.setPassword(rabbitmqPassword);

            connection = factory.newConnection();
            channel = connection.createChannel();

            // Declare a topic exchange for poll events
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);

            System.out.println("RabbitMQ connection established successfully");
        } catch (IOException | TimeoutException e) {
            System.out.println("Failed to connect to RabbitMQ: " + e.getMessage());
        }
    }

    /**
     * Creates a new topic for a poll and starts listening for vote events
     */
    public void createPollTopic(String pollId) {
        try {
            String routingKey = "poll." + pollId + ".vote";

            // Create a queue for this poll
            String queueName = "poll_" + pollId + "_votes";
            channel.queueDeclare(queueName, true, false, false, null);

            // Bind queue to exchange with routing key
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

            // Start consuming messages from this queue
            startListeningForVotes(queueName, pollId);

            System.out.println("Created topic for poll: " + pollId);
        } catch (IOException e) {
            System.out.println("Failed to create topic for poll: " + pollId);
        }
    }

    /**
     * Starts listening for vote events on a specific poll queue
     */
    private void startListeningForVotes(String queueName, String pollId) {
        try {
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received vote event for poll " + pollId + ": " + message);

                try {
                    processVoteEvent(pollId, message);
                } catch (Exception e) {
                    System.out.println("Error processing vote event: " + e.getMessage());
                }
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

            System.out.println("Started listening for votes on queue: " + queueName);
        } catch (IOException e) {
            System.out.println("Failed to start listening for votes on queue: " + queueName);
        }
    }

    private void processVoteEvent(String pollId, String message) {
        String[] parts = message.split(":");
        String voteOptionId = parts[0];
        String username = parts.length > 1 ? parts[1] : "anonymous";

        System.out
                .println("Processing vote: optionId=" + voteOptionId + ", username=" + username + ", pollId=" + pollId);

        Vote vote = new Vote();
        vote.setVoteOptionId(voteOptionId);
        vote.setPublishedAt(Instant.now());

        if (!"anonymous".equals(username)) {
            User user = new User();
            user.setUsername(username);
            vote.setUser(user);
        }

        try {
            pollManager.createVoteFromEvent(vote);
            System.out.println("Vote persisted for poll " + pollId + " on option " + voteOptionId);
        } catch (Exception e) {
            System.out.println("Failed to persist vote: " + e.getMessage());
        }
    }

    /**
     * Publishes a vote event to RabbitMQ
     */
    public void publishVoteEvent(String pollId, String voteOptionId, String username) {
        try {
            String routingKey = "poll." + pollId + ".vote";
            String message = voteOptionId + ":" + (username != null ? username : "anonymous");

            channel.basicPublish(EXCHANGE_NAME, routingKey, null,
                    message.getBytes(StandardCharsets.UTF_8));

            System.out.println("Published vote event for poll " + pollId + ": " + message);
        } catch (IOException e) {
            System.out.println("Failed to publish vote event: " + e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            System.out.println("RabbitMQ connection closed");
        } catch (IOException | TimeoutException e) {
            System.out.println("Error closing RabbitMQ connection: " + e.getMessage());
        }
    }
}
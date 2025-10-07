package dat250;

import com.rabbitmq.client.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import dat250.services.RedisService;

/**
 * Main program which bootstraps the Spring application.
 */

@SpringBootApplication
public class PollApplication {

	public static void main(String[] args) {
		SpringApplication.run(PollApplication.class, args);
	}

	@Component
	public static class RedisTestRunner {
		private final RedisService redisService;

		public RedisTestRunner(RedisService redisService) {
			this.redisService = redisService;
		}

		@EventListener(ApplicationReadyEvent.class)
		public void runAfterStartup() {
			redisService.runRedis();

			new Thread(() -> runRabbitMQTestClient()).start();
		}

		private void runRabbitMQTestClient() {
			System.out.println("\n#RabbitMQ");
			System.out.println("#Testing poll event system...\n");

			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");

			try (Connection connection = factory.newConnection();
					Channel channel = connection.createChannel()) {

				System.out.println("Connected to RabbitMQ");

				// Subscribe to all poll events to monitor the system
				String monitorQueue = channel.queueDeclare().getQueue();
				channel.queueBind(monitorQueue, "poll_events", "poll.*.vote");

				System.out.println("Monitoring all poll vote events on 'poll_events' exchange");

				// Set up consumer
				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					String message = new String(delivery.getBody(), "UTF-8");
					String routingKey = delivery.getEnvelope().getRoutingKey();
					System.out.println("[RabbitMQ Monitor] " + routingKey + " -> " + message);
				};

				channel.basicConsume(monitorQueue, true, deliverCallback, consumerTag -> {
				});

				System.out.println("\nListening for vote events for 5 seconds...");
				Thread.sleep(5000);

				// Now publish a test vote to demonstrate external voting
				System.out.println("\n[TEST] Publishing test vote from standalone client...");
				String testPollId = "test-poll-id"; // Use a real poll ID from your database
				String testOptionId = "test-option-id"; // Use a real option ID
				String testMessage = testOptionId + ":testuser";
				String testRoutingKey = "poll." + testPollId + ".vote";

				channel.basicPublish("poll_events", testRoutingKey, null,
						testMessage.getBytes("UTF-8"));
				System.out.println("[TEST] Published: " + testRoutingKey + " -> " + testMessage);

				// Wait to see if it's received
				System.out.println("\nListening for response for 10 more seconds...\n");
				Thread.sleep(10000);

				System.out.println("\nRabbitMQ Test Complete\n");

			} catch (Exception e) {
				System.err.println("RabbitMQ test failed: " + e.getMessage());
				System.err.println("Make sure RabbitMQ is running on localhost:5672");
			}
		}
	}
}
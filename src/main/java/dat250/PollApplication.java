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
			System.out.println("\nInitializing RabbitMQ Service...");
			String QUEUE_NAME = "hello";

			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			try (Connection connection = factory.newConnection();
					Channel channel = connection.createChannel()) {
				channel.queueDeclare(QUEUE_NAME, false, false, false, null);
				String message = "Hello World!";
				channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
				System.out.println("	Sent '" + message + "'");

				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					String receivedMessage = new String(delivery.getBody(), "UTF-8");
					System.out.println("	Received '" + receivedMessage + "'");
				};
				channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
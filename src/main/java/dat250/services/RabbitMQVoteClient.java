package dat250.services;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RabbitMQVoteClient {
    private static final String EXCHANGE_NAME = "poll_events";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // Declare exchange
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);

            // Subscribe to all poll events
            String monitorQueue = channel.queueDeclare().getQueue();
            channel.queueBind(monitorQueue, EXCHANGE_NAME, "poll.*.vote");

            System.out.println("=== RabbitMQ Poll Vote Client ===");
            System.out.println("Monitoring all poll votes...\n");

            // Consumer to listen for vote confirmations
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String routingKey = delivery.getEnvelope().getRoutingKey();
                System.out.println("[VOTE EVENT] " + routingKey + " -> " + message);
            };

            channel.basicConsume(monitorQueue, true, deliverCallback, consumerTag -> {
            });

            // Interactive voting
            Scanner scanner = new Scanner(System.in);
            System.out.println("Commands:");
            System.out.println("  vote <pollId> <optionId> <username>");
            System.out.println("  exit");
            System.out.print("\n> ");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.equals("exit")) {
                    break;
                }

                if (line.startsWith("vote ")) {
                    String[] parts = line.split(" ");
                    if (parts.length == 4) {
                        String pollId = parts[1];
                        String optionId = parts[2];
                        String username = parts[3];

                        String routingKey = "poll." + pollId + ".vote";
                        String message = optionId + ":" + username;

                        channel.basicPublish(EXCHANGE_NAME, routingKey, null,
                                message.getBytes(StandardCharsets.UTF_8));

                        System.out.println("[PUBLISHED] " + routingKey + " -> " + message);
                    } else {
                        System.out.println("Usage: vote <pollId> <optionId> <username>");
                    }
                }

                System.out.print("\n> ");
            }

            System.out.println("Exiting...");
        }
    }
}

package dat250;

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
			redisService.runRedisTests();
		}
	}
}
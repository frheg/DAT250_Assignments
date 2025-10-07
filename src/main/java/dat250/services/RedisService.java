package dat250.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.UnifiedJedis;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

    public void runRedis() {

        System.out.println("\n# Testing Redis Connection");

        // Test basic connection with PING
        String pong = jedis.ping();
        System.out.println("PING response: " + pong);

        // Basic SET/GET operations
        System.out.println("\n# Basic SET/GET Operations");
        jedis.set("user", "bob");
        String user1 = jedis.get("user");
        System.out.println("First user SET, total users: " + user1);

        jedis.set("user", "alice");
        String user2 = jedis.get("user");
        System.out.println("Second user SET, total users: " + user2);

        // Test expiration
        System.out.println("\n# Testing Expiration");
        jedis.set("temp_user", "charlie");
        jedis.expire("temp_user", 3); // Expire in 3 seconds
        Long ttl1 = jedis.ttl("temp_user");
        System.out.println("TTL for temp_user: " + ttl1 + " seconds");

        // Wait a moment and check again
        try {
            Thread.sleep(4000); // Wait 4 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Long ttl2 = jedis.ttl("temp_user");
        String expiredUser = jedis.get("temp_user");
        System.out.println("TTL after expiration: " + ttl2);
        System.out.println("Expired user value: " + expiredUser);

        // Use Case 1: Keep track of logged-in users using Set
        System.out.println("\n# Use Case 1: Tracking Logged-in Users with Set");
        String loggedInUsersKey = "logged_in_users";

        // Clear any existing data
        jedis.del(loggedInUsersKey);

        // 1. Initial state: no user is logged in
        Set<String> initialUsers = jedis.smembers(loggedInUsersKey);
        System.out.println("1. Initial state: no user is logged in: " + initialUsers);

        // 2. User "alice" logs in
        jedis.sadd(loggedInUsersKey, "alice");
        Set<String> afterAlice = jedis.smembers(loggedInUsersKey);
        System.out.println("2. User \"alice\" logs in: " + afterAlice);

        // 3. User "bob" logs in
        jedis.sadd(loggedInUsersKey, "bob");
        Set<String> afterBob = jedis.smembers(loggedInUsersKey);
        System.out.println("3. User \"bob\" logs in: " + afterBob);

        // 4. User "alice" logs off
        jedis.srem(loggedInUsersKey, "alice");
        Set<String> afterAliceLogoff = jedis.smembers(loggedInUsersKey);
        System.out.println("4. User \"alice\" logs off: " + afterAliceLogoff);

        // 5. User "eve" logs in
        jedis.sadd(loggedInUsersKey, "eve");
        Set<String> afterEve = jedis.smembers(loggedInUsersKey);
        System.out.println("5. User \"eve\" logs in: " + afterEve);

        // Use Case 2: Store poll information using Hash
        System.out.println("\n# Use Case 2: Poll Data with Hash Operations");
        String pollKey = "poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b";

        // Clear any existing data
        jedis.del(pollKey);

        // Store poll information in a hash
        Map<String, String> pollData = new HashMap<>();
        pollData.put("id", "03ebcb7b-bd69-440b-924e-f5b7d664af7b");
        pollData.put("title", "Pineapple on Pizza?");
        pollData.put("option1_caption", "Yes, yammy!");
        pollData.put("option1_votes", "269");
        pollData.put("option2_caption", "Mamma mia, nooooo!");
        pollData.put("option2_votes", "268");
        pollData.put("option3_caption", "I do not really care ...");
        pollData.put("option3_votes", "42");

        jedis.hmset(pollKey, pollData);

        // Retrieve and display poll information
        Map<String, String> retrievedPoll = jedis.hgetAll(pollKey);
        System.out.println("Poll Title: " + retrievedPoll.get("title"));
        System.out.println("Option 1: " + retrievedPoll.get("option1_caption") + " - Votes: "
                + retrievedPoll.get("option1_votes"));
        System.out.println("Option 2: " + retrievedPoll.get("option2_caption") + " - Votes: "
                + retrievedPoll.get("option2_votes"));
        System.out.println("Option 3: " + retrievedPoll.get("option3_caption") + " - Votes: "
                + retrievedPoll.get("option3_votes"));

        // Test incrementing vote count without replacing the whole object
        System.out.println("\n# Testing Vote Increment");
        System.out.println("Before increment - Option 1 votes: " + jedis.hget(pollKey, "option1_votes"));

        // Increment option 1 votes by 1
        jedis.hincrBy(pollKey, "option1_votes", 1);
        System.out.println("After incrementing by 1 - Option 1 votes: " + jedis.hget(pollKey, "option1_votes"));

        // Increment option 2 votes by 5
        System.out.println("Before increment - Option 2 votes: " + jedis.hget(pollKey, "option2_votes"));
        jedis.hincrBy(pollKey, "option2_votes", 5);
        System.out.println("After incrementing by 5 - Option 2 votes: " + jedis.hget(pollKey, "option2_votes"));

        // Simple cache simulation
        System.out.println("\n# Simple Cache Implementation Test");
        String cacheKey = "cache:poll_results:03ebcb7b-bd69-440b-924e-f5b7d664af7b";

        // Simulate checking if poll results are cached
        if (jedis.exists(cacheKey)) {
            System.out.println("Poll results found in cache!");
            String cachedResults = jedis.get(cacheKey);
            System.out.println("Cached results: " + cachedResults);
        } else {
            System.out.println("Poll results not in cache, simulating database query...");

            // Simulate expensive database operation
            String simulatedDbResult = "Option1:270,Option2:273,Option3:42";

            // Cache the result with 30 second expiration
            jedis.setex(cacheKey, 30, simulatedDbResult);
            System.out.println("Results cached: " + simulatedDbResult);
        }

        // Test cache hit on second call
        if (jedis.exists(cacheKey)) {
            System.out.println("Second call - Poll results found in cache!");
            String cachedResults = jedis.get(cacheKey);
            System.out.println("Cached results: " + cachedResults);
            Long ttlCache = jedis.ttl(cacheKey);
            System.out.println("Cache TTL: " + ttlCache + " seconds");
        }

        System.out.println("\nRedis Tests Completed Successfully!");

        jedis.close();
    }
}

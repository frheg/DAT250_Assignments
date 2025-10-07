package dat250.group2.poll.poll;

import dat250.models.Poll;
import dat250.models.User;
import dat250.models.Vote;
import dat250.models.VoteOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class pollRestClient {

        @LocalServerPort
        int port;
        private RestClient restClient;

        @BeforeEach
        void setup() {
                this.restClient = RestClient.builder()
                                .baseUrl("http://localhost:" + port)
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .build();
        }

        @Test
        void fullScenario() {
                // 1. Create new user
                User u1 = new User();
                u1.setUsername("Dummy");
                u1.setEmail("dummy@hvl.no");
                u1.setPassword("supersecret");

                User createdUser = restClient.post()
                                .uri("/users")
                                .body(u1)
                                .retrieve()
                                .body(User.class);

                assertThat(createdUser).isNotNull();
                assertThat(createdUser.getUsername()).isEqualTo("Dummy");

                // 2. List all users
                User[] users = restClient.get()
                                .uri("/users")
                                .retrieve()
                                .body(User[].class);

                assertThat(users).isNotNull();
                assertThat(Arrays.asList(users)).extracting(User::getUsername).contains("Dummy");

                // 3. Create another user
                User u2 = new User();
                u2.setUsername("user2");
                u2.setEmail("user2@hvl.no");
                u2.setPassword("notsupersecret");

                User createdUser2 = restClient.post()
                                .uri("/users")
                                .body(u2)
                                .retrieve()
                                .body(User.class);

                assertThat(createdUser2).isNotNull();
                assertThat(createdUser2.getUsername()).isEqualTo("user2");

                // 4. Check for 2 users
                users = restClient.get()
                                .uri("/users")
                                .retrieve()
                                .body(User[].class);

                assertThat(users).isNotNull();
                assertThat(users.length).isGreaterThanOrEqualTo(2);
                assertThat(Arrays.stream(users).map(User::getUsername)).contains("Dummy", "user2");

                // 5. User 1 create poll
                Poll testpoll = new Poll();
                testpoll.setQuestion("Dogs or Cats?");
                testpoll.setValidUntil(Instant.now().plusSeconds(3600));
                testpoll.setPublicAccess(false);

                User creator = new User();
                creator.setUsername("Dummy");
                testpoll.setCreatedBy(creator);
                testpoll.setOptions(new ArrayList<>());

                Poll createdpoll = restClient.post()
                                .uri("/polls")
                                .body(testpoll)
                                .retrieve()
                                .body(Poll.class);

                assertThat(createdpoll.getPollId()).isNotNull();
                String pollId = createdpoll.getPollId();

                // 6. List polls
                Poll[] testpolls = restClient.get()
                                .uri("/polls")
                                .retrieve()
                                .body(Poll[].class);

                assertThat(testpolls).isNotNull();
                assertThat(Arrays.stream(testpolls).map(Poll::getPollId)).contains(pollId);

                // 7. Create vote options
                VoteOption optA = new VoteOption();
                optA.setPollId(pollId);
                optA.setCaption("Dogs");

                VoteOption optB = new VoteOption();
                optB.setPollId(pollId);
                optB.setCaption("Cats");

                VoteOption savedA = restClient.post()
                                .uri("/voteoptions")
                                .body(optA)
                                .retrieve()
                                .body(VoteOption.class);

                VoteOption savedB = restClient.post()
                                .uri("/voteoptions")
                                .body(optB)
                                .retrieve()
                                .body(VoteOption.class);

                assertThat(savedA).isNotNull();
                assertThat(savedB).isNotNull();
                assertThat(savedA.getOptionId()).isNotBlank();
                assertThat(savedB.getOptionId()).isNotBlank();

                // 8. User 2 votes

                Vote vote1 = new Vote();
                vote1.setVoteOptionId(savedA.getOptionId());

                User voter = new User();
                voter.setUsername("user2");
                vote1.setUser(voter);

                Vote createdVote1 = restClient.post()
                                .uri("/votes")
                                .body(vote1)
                                .retrieve()
                                .body(Vote.class);

                assertThat(createdVote1).isNotNull();
                assertThat(createdVote1.getVoteId()).isNotBlank(); // check if given automaticly
                assertThat(createdVote1.getPublishedAt()).isNotNull(); // same here
                assertThat(createdVote1.getUser()).isNotNull();
                assertThat(createdVote1.getUser().getUsername()).isEqualTo("user2");
                assertThat(createdVote1.getVoteOptionId()).isEqualTo(savedA.getOptionId());

                // 9. User 2 changes vote
                Vote vote2 = new Vote();
                vote2.setVoteOptionId(savedB.getOptionId());

                User voter2 = new User();
                voter2.setUsername("user2");
                vote2.setUser(voter2);

                Vote createdVote2 = restClient.post()
                                .uri("/votes")
                                .body(vote2)
                                .retrieve()
                                .body(Vote.class);

                assertThat(createdVote2).isNotNull();
                assertThat(createdVote2.getVoteId()).isEqualTo(createdVote1.getVoteId()); // same id cuz new logic from
                                                                                          // updatevote
                assertThat(createdVote2.getPublishedAt()).isAfterOrEqualTo(createdVote1.getPublishedAt());
                assertThat(createdVote2.getUser().getUsername()).isEqualTo("user2");
                assertThat(createdVote2.getVoteOptionId()).isEqualTo(savedB.getOptionId());

                // 10. List votes and show vote
                Vote[] votes = restClient.get()
                                .uri("/votes")
                                .retrieve()
                                .body(Vote[].class);

                assertThat(votes).isNotNull();

                Vote latestfromuser2 = Arrays.stream(votes)
                                .filter(v -> v.getUser() != null && "user2".equals(v.getUser().getUsername()))
                                .max(Comparator.comparing(Vote::getPublishedAt))
                                .orElseThrow();

                assertThat(latestfromuser2.getVoteOptionId()).isEqualTo(savedB.getOptionId());

                // 11. Delete poll
                restClient.delete()
                                .uri("/polls/{id}", pollId)
                                .retrieve()
                                .toBodilessEntity();

                // 12. List votes = no votes
                Vote[] votesAfterDelete = restClient.get()
                                .uri("/votes")
                                .retrieve()
                                .body(Vote[].class);

                assertThat(votesAfterDelete).isEmpty();

        }
}
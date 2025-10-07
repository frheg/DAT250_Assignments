package dat250.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Not used anymore, but kept for PollManager
    @Transient
    private String voteId;
    @Transient
    private String voteOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User votedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private VoteOption votesOn;

    private Instant publishedAt;

    public Vote() {
    }

    // Id
    public Long getId() {
        return id;
    }

    // votedBy
    public User getVotedBy() {
        return votedBy;
    }

    public void setVotedBy(User user) {
        this.votedBy = user;
    }

    // votesOn
    public VoteOption getVotesOn() {
        return votesOn;
    }

    public void setVotesOn(VoteOption votesOn) {
        this.votesOn = votesOn;
    }

    // PublishedAt
    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    // Not used anymore, but kept for PollManager
    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getVoteOptionId() {
        return voteOptionId;
    }

    public void setVoteOptionId(String voteOptionId) {
        this.voteOptionId = voteOptionId;
    }

    // Compatibility accessors for old API naming
    public User getUser() {
        return getVotedBy();
    }

    public void setUser(User user) {
        setVotedBy(user);
    }
}
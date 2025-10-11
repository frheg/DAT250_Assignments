package dat250.models;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient // Is transient to avoid being persisted by JPA/Hibernate
    private String pollId;

    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    private Boolean publicAccess;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("presentationOrder ASC")
    private List<VoteOption> options = new ArrayList<>();

    public Poll() {
    }

    public VoteOption addVoteOption(String caption) {
        if (caption == null) {
            throw new IllegalArgumentException("caption must not be null");
        }
        VoteOption option = new VoteOption();
        option.setCaption(caption);
        option.setPresentationOrder(options.size());
        option.setPoll(this);
        options.add(option);
        return option;
    }

    // Id
    public Long getId() {
        return id;
    }

    // Legacy pollId for in-memory manager
    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    // Question
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    // PublishedAt
    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    // ValidUntil
    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    // Public Access
    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    // CreatedBy
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    // Options
    public List<VoteOption> getOptions() {
        return options;
    }

    public void setOptions(List<VoteOption> options) {
        this.options = options; // NOTE: This implementation may be altered depending on voteOptions should be
                                // appended or created new list each time.
    }
}
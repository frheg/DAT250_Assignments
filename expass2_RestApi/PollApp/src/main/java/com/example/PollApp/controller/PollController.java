package com.example.PollApp.controller;

import com.example.PollApp.domain.Poll;
import com.example.PollApp.domain.User;
import com.example.PollApp.domain.VoteOption;
import com.example.PollApp.service.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/polls")
public class PollController {
    @Autowired
    private PollManager pollManager;

    @PostMapping
    public void createPoll(@RequestBody PollRequest pollRequest) {
        Poll poll = new Poll();
        poll.setQuestion(pollRequest.getQuestion());
        poll.setCreator(pollManager.getUser(pollRequest.getCreatorId()));
        poll.setVoteOptions(pollRequest.getVoteOptions());
        pollManager.addPoll(pollRequest.getId(), poll);
        // Add all vote options to PollManager's map
        if (pollRequest.getVoteOptions() != null) {
            for (VoteOption option : pollRequest.getVoteOptions()) {
                pollManager.addVoteOption(option.getId(), option);
            }
        }
    }

    @GetMapping
    public Collection<Poll> getAllPolls() {
        return pollManager.getAllPolls();
    }

    @DeleteMapping("/{id}")
    public void deletePoll(@PathVariable Long id) {
        pollManager.removePoll(id);
    }

    // DTO for poll creation
    public static class PollRequest {
        private Long id;
        private String question;
        private Long creatorId;
        private List<VoteOption> voteOptions;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public Long getCreatorId() { return creatorId; }
        public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
        public List<VoteOption> getVoteOptions() { return voteOptions; }
        public void setVoteOptions(List<VoteOption> voteOptions) { this.voteOptions = voteOptions; }
    }
}

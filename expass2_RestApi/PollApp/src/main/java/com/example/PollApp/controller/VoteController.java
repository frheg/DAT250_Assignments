package com.example.PollApp.controller;

import com.example.PollApp.domain.Vote;
import com.example.PollApp.domain.User;
import com.example.PollApp.domain.VoteOption;
import com.example.PollApp.service.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RestController
@RequestMapping("/votes")
public class VoteController {
    @Autowired
    private PollManager pollManager;

    @PostMapping
    public void createVote(@RequestBody VoteRequest voteRequest) {
        Vote vote = new Vote();
        vote.setUser(pollManager.getUser(voteRequest.getUserId()));
        vote.setVoteOption(pollManager.getVoteOption(voteRequest.getVoteOptionId()));
        pollManager.addVote(voteRequest.getId(), vote);
    }

    @PutMapping("/{id}")
    public void updateVote(@PathVariable Long id, @RequestBody VoteRequest voteRequest) {
        Vote vote = pollManager.getVote(id);
        if (vote != null) {
            vote.setUser(pollManager.getUser(voteRequest.getUserId()));
            vote.setVoteOption(pollManager.getVoteOption(voteRequest.getVoteOptionId()));
            pollManager.addVote(id, vote);
        }
    }

    @GetMapping
    public Collection<Vote> getAllVotes() {
        return pollManager.getAllVotes();
    }

    public static class VoteRequest {
        private Long id;
        private Long userId;
        private Long voteOptionId;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getVoteOptionId() { return voteOptionId; }
        public void setVoteOptionId(Long voteOptionId) { this.voteOptionId = voteOptionId; }
    }
}

package dat250.controllers;

import dat250.models.Poll;
import dat250.services.PollManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages API-endpoints for polls.
 */

@RestController
@CrossOrigin
@RequestMapping("/polls")
public class PollController {
    @Autowired
    private PollManager pollManager;

    @GetMapping("")
    public ResponseEntity<List<Poll>> getPolls() {
        return ResponseEntity.ok(pollManager.getAllPolls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poll> getPoll(@PathVariable String id) {  
        Poll poll = pollManager.getPoll(id);
        if (poll == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(poll);
    }

    @PostMapping("")
    public ResponseEntity<Poll> createPoll(@RequestBody Poll poll) {
        Poll createdPoll = pollManager.createPoll(poll);
        if (createdPoll == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(createdPoll);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poll> updatePoll(@PathVariable String id, @RequestBody Poll poll) {
        Poll updatedPoll = pollManager.updatePoll(id, poll);
        if (updatedPoll == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedPoll);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoll(@PathVariable String id) {
        pollManager.deletePoll(id);
        return ResponseEntity.noContent().build();
    }
}

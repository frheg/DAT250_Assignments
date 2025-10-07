package dat250.controllers;

import dat250.models.Vote;
import dat250.services.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages API-endpoints for votes.
 */

@RestController
@CrossOrigin
@RequestMapping("/votes")
public class VoteController {
    @Autowired
    private PollManager pollManager;

    @GetMapping("")
    public ResponseEntity<List<Vote>> getVotes() {
        return ResponseEntity.ok(pollManager.getAllVotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vote> getVote(@PathVariable String id) {
        Vote vote = pollManager.getVote(id);
        if (vote == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(vote);
    }

    @PostMapping("")
    public ResponseEntity<Vote> createVote(@RequestBody Vote vote) {
        try {
            Vote created = pollManager.createVote(vote);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vote> updateVote(@PathVariable String id, @RequestBody Vote vote) {
        Vote updatedVote = pollManager.updateVote(id, vote);
        if (updatedVote == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedVote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVote(@PathVariable String id) {
        pollManager.deleteVote(id);
        return ResponseEntity.noContent().build();
    }
}
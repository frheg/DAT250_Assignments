package dat250.controllers;

import dat250.models.VoteOption;
import dat250.services.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages API-endpoints for vote options.
 */

@RestController
@RequestMapping("/voteoptions")
public class VoteOptionController {
    @Autowired
    private PollManager pollManager;

    @GetMapping("")
    public ResponseEntity<List<VoteOption>> getVoteOptions() {
        return ResponseEntity.ok(pollManager.getAllVoteOptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoteOption> getVoteOption(@PathVariable String id) {
        VoteOption option = pollManager.getVoteOption(id);
        if (option == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(option);
    }

    @PostMapping("")
    public ResponseEntity<VoteOption> createVoteOption(@RequestBody VoteOption option) {
        VoteOption created = pollManager.createVoteOption(option);
        return ResponseEntity.ok(created);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoteOption(@PathVariable String id) {
        pollManager.deleteVoteOption(id);
        return ResponseEntity.noContent().build();
    }
}
package dat250.controllers;

import dat250.models.User;
import dat250.models.UserRequests.UserGetResponse;
import dat250.models.UserRequests.UserUpdateRequest;
import dat250.services.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages API-endpoints for users.
 */

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    @Autowired
    private PollManager pollManager;

    @GetMapping("")
    public ResponseEntity<List<UserGetResponse>> getUsers() {
        return ResponseEntity.ok(pollManager.getAllRestrictedUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetResponse> getUser(@PathVariable String id) {
        UserGetResponse user = pollManager.getRestrictedUser(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = pollManager.createUser(user);
        if (createdUser == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest updateRequest) {
        User updatedUser = pollManager.updateUser(id, updateRequest);
        if (updatedUser == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        pollManager.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
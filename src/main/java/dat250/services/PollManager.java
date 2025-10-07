package dat250.services;

import dat250.models.Poll;
import dat250.models.User;
import dat250.models.UserRequests.UserGetResponse;
import dat250.models.UserRequests.UserUpdateRequest;
import dat250.models.Vote;
import dat250.models.VoteOption;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Manages CRUD (Create, Read, Update, Delete) operations for polls, users, votes, and vote options.
 */

@Component
public class PollManager {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Poll> polls = new HashMap<>();
    private final Map<String, Vote> votes = new HashMap<>();
    private final Map<String, VoteOption> voteOptions = new HashMap<>();

    /**
     * User CRUD
     * */
    public User createUser(User user) {
        User existingUser =  this.getUser(user.getUserId());
        if (existingUser != null) return null;

        users.put(user.getUserId(), user);
        return user;
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public UserGetResponse getRestrictedUser(String userId) {
        User user = this.getUser(userId);
        if (user == null) return null;

        UserGetResponse userGetResponse = new UserGetResponse();
        userGetResponse.setEmail(user.getEmail());
        userGetResponse.setUserId(user.getUserId());
        return userGetResponse;
    }

    public List<UserGetResponse> getAllRestrictedUsers() {
        List<UserGetResponse> restrictedUsers = new ArrayList<>();

        for (User user : users.values()) {
            UserGetResponse response = new UserGetResponse();
            response.setEmail(user.getEmail());
            response.setUserId(user.getUserId());
            restrictedUsers.add(response);
        }
        return restrictedUsers;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(String userId, UserUpdateRequest updateRequest) {
        User existingUser = users.get(userId);
        if (existingUser == null) {
            return null;
        }
        
        // Only update fields that are provided, if null, then do not touch
        if (updateRequest.getEmail() != null) {
            existingUser.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            existingUser.setPassword(updateRequest.getPassword());
        }

        return existingUser;
    }
    public void deleteUser(String userId) {
        users.remove(userId);
    }

    /**
     * Poll CRUD
     * */
    public Poll createPoll(Poll poll) {
        // Set pollId automatically using UUID only if not already set
        if (poll.getPollId() == null || poll.getPollId().trim().isEmpty()) {
            poll.setPollId(UUID.randomUUID().toString());
        }

        // User is required
        User createdBy = poll.getCreatedBy();
        if (createdBy == null || createdBy.getUserId() == null) {
            return null;
        }
        if (!users.containsKey(createdBy.getUserId())) {
            return null;
        }

        // Set validUntil in future if not set
        if (poll.getValidUntil() == null) {
            poll.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        }

        // Set publishedAt automatically to current time only if not already set
        if (poll.getPublishedAt() == null) {
            poll.setPublishedAt(Instant.now());
        }

        // Set VoteOption automatically if not defined
        for (VoteOption option : poll.getOptions()) {
            if (option.getOptionId() == null || option.getOptionId().trim().isEmpty()) {
                option.setOptionId(UUID.randomUUID().toString());
            }
            option.setPollId(poll.getPollId());
            this.createVoteOption(option);
        }
        
        polls.put(poll.getPollId(), poll);
        return poll;
    }
    public Poll getPoll(String pollId) {
        Poll poll = polls.get(pollId);
        return createFilteredPoll(poll);
    }
    public List<Poll> getAllPolls() {
        List<Poll> modifiedPolls = new ArrayList<>();
        for (Poll poll : polls.values()) {
            Poll filteredPoll = createFilteredPoll(poll);
            if (filteredPoll != null) {
                modifiedPolls.add(filteredPoll);
            }
        }
        return modifiedPolls;
    }
    public Poll updatePoll(String pollId, Poll updateRequest) {
        Poll existingPoll = polls.get(pollId);
        if (existingPoll == null) {
            return null;
        }
        
        // Only update validUntil and publicAccess fields
        if (updateRequest.getValidUntil() != null) {
            existingPoll.setValidUntil(updateRequest.getValidUntil());
        }
        if (updateRequest.getPublicAccess() != null) {
            existingPoll.setPublicAccess(updateRequest.getPublicAccess());
        }
        
        return existingPoll;
    }
    public void deletePoll(String pollId) {
            if (pollId == null) return;
            List<String> optionIds = new ArrayList<>();
            for (VoteOption option : voteOptions.values()) {
                if (pollId.equals(option.getPollId())) {
                    optionIds.add(option.getOptionId());
                }
            }
            for (String optionId : optionIds) {
                deleteVoteOption(optionId);
            }

            polls.remove(pollId);
        }

    // VoteOption CRUD
    public VoteOption createVoteOption(VoteOption option) {
        // Set optionId automatically only if not already set
        if (option.getOptionId() == null || option.getOptionId().trim().isEmpty()) {
            option.setOptionId(UUID.randomUUID().toString());
        }
        option.setVotes(null);
        
        voteOptions.put(option.getOptionId(), option);
        return option;
    }
    public VoteOption getVoteOption(String optionId) {
        VoteOption option = voteOptions.get(optionId);
        if (option != null) {
            VoteOption optionCopy = new VoteOption();
            optionCopy.setOptionId(option.getOptionId());
            optionCopy.setPollId(option.getPollId());
            optionCopy.setCaption(option.getCaption());
            optionCopy.setVotes(getFilteredVotesForOption(option.getOptionId()));
            return optionCopy;
        }
        return null;
    }
    public List<VoteOption> getAllVoteOptions() {
        List<VoteOption> filteredOptions = new ArrayList<>();
        for (VoteOption option : voteOptions.values()) {
            VoteOption optionCopy = new VoteOption();
            optionCopy.setOptionId(option.getOptionId());
            optionCopy.setPollId(option.getPollId());
            optionCopy.setCaption(option.getCaption());
            optionCopy.setVotes(getFilteredVotesForOption(option.getOptionId()));
            filteredOptions.add(optionCopy);
        }
        return filteredOptions;
    }
    public void deleteVoteOption(String optionId) {
        if (optionId == null) return;
        votes.values().removeIf(v -> optionId.equals(v.getVoteOptionId()));
        voteOptions.remove(optionId);
    }

    // Vote CRUD
    public Vote createVote(Vote vote) {
        // Set voteId automatically only if not already set
        if (vote.getVoteId() == null || vote.getVoteId().trim().isEmpty()) {
            vote.setVoteId(UUID.randomUUID().toString());
        }

        // Set publishedAt automatically only if not already set
        if (vote.getPublishedAt() == null) {
            vote.setPublishedAt(Instant.now());
        }

        // Check if poll is private and userId validation
        if (vote.getVoteOptionId() != null) {
            VoteOption option = voteOptions.get(vote.getVoteOptionId());
            if (option != null && option.getPollId() != null) {
                Poll poll = polls.get(option.getPollId());
                if (poll != null && poll.getPublicAccess() != null && !poll.getPublicAccess()) {
                    if (vote.getUser() == null || vote.getUser().getUserId() == null) {
                        throw new IllegalArgumentException("UserId is required for private polls");
                    }
                    for (Vote v : votes.values()) {
                        if (v.getUser() !=null && v.getUser().getUserId().equals(vote.getUser().getUserId())) {
                            VoteOption existingVo =  voteOptions.get(v.getVoteOptionId());
                            if(existingVo != null && poll.getPollId().equals(existingVo.getPollId())) {

                                return updateVote(v.getVoteId(), vote);
                            }
                        }
                    }
                }
            }
        }

        votes.put(vote.getVoteId(), vote);
        return vote;
    }

    public Vote getVote(String voteId) {
        Vote vote = votes.get(voteId);
        if (vote != null) {
            return createFilteredVote(vote);
        }
        return null;
    }


    public List<Vote> getAllVotes() {
        List<Vote> filteredVotes = new ArrayList<>();
        for (Vote vote : votes.values()) {
            filteredVotes.add(createFilteredVote(vote));
        }
        return filteredVotes;
    }

    public Vote updateVote(String id, Vote updatedVote) {
        Vote existingVote = votes.get(id);
        if (existingVote == null) {
            return null;
        }

        existingVote.setPublishedAt(Instant.now());

        if (updatedVote.getPublishedAt() == null) {
            existingVote.setPublishedAt(updatedVote.getPublishedAt());
        }

        if (updatedVote.getVoteOptionId() != null) {
            existingVote.setVoteOptionId(updatedVote.getVoteOptionId());
        }
        return existingVote;
    }

    public void deleteVote(String voteId) {
        votes.remove(voteId);
    }

    // ----- HELPER METHODS -----
    // Helper method to create a poll with filtered creator info and populated options
    private Poll createFilteredPoll(Poll originalPoll) {
        if (originalPoll == null) return null;
        
        Poll modifiedPoll = new Poll();
        modifiedPoll.setPollId(originalPoll.getPollId());
        modifiedPoll.setQuestion(originalPoll.getQuestion());
        modifiedPoll.setPublishedAt(originalPoll.getPublishedAt());
        modifiedPoll.setValidUntil(originalPoll.getValidUntil());
        modifiedPoll.setPublicAccess(originalPoll.getPublicAccess());
        
        // Only include userID of creator, not entire user object
        if (originalPoll.getCreatedBy() != null) {
            User creatorSummary = new User();
            creatorSummary.setUserId(originalPoll.getCreatedBy().getUserId());
            modifiedPoll.setCreatedBy(creatorSummary);
        }
        
        // Populate with vote options
        modifiedPoll.setOptions(getVoteOptionsForPoll(originalPoll.getPollId()));
        
        return modifiedPoll;
    }
    
    // Helper method to get vote options for a specific poll
    private List<VoteOption> getVoteOptionsForPoll(String pollId) {
        List<VoteOption> pollOptions = new ArrayList<>();
        for (VoteOption option : voteOptions.values()) {
            if (option.getPollId() != null && option.getPollId().equals(pollId)) {
                VoteOption optionCopy = new VoteOption();
                optionCopy.setOptionId(option.getOptionId());
                optionCopy.setPollId(option.getPollId());
                optionCopy.setCaption(option.getCaption());
                
                optionCopy.setVotes(getFilteredVotesForOption(option.getOptionId()));
                pollOptions.add(optionCopy);
            }
        }
        return pollOptions;
    }
    
    // Helper method to get votes for a specific option
    private List<Vote> getVotesForOption(String optionId) {
        List<Vote> optionVotes = new ArrayList<>();
        for (Vote vote : votes.values()) {
            if (vote.getVoteOptionId() != null && vote.getVoteOptionId().equals(optionId)) {
                optionVotes.add(vote);
            }
        }
        return optionVotes;
    }
    
    private Vote createFilteredVote(Vote originalVote) {
        if (originalVote == null) return null;
        
        Vote filteredVote = new Vote();
        filteredVote.setVoteId(originalVote.getVoteId());
        filteredVote.setVoteOptionId(originalVote.getVoteOptionId());
        filteredVote.setPublishedAt(originalVote.getPublishedAt());
        
        if (originalVote.getUser() != null) {
            User userSummary = new User();
            userSummary.setUserId(originalVote.getUser().getUserId());
            filteredVote.setUser(userSummary);
        }
        
        return filteredVote;
    }
    
    private List<Vote> getFilteredVotesForOption(String optionId) {
        List<Vote> filteredVotes = new ArrayList<>();
        for (Vote vote : votes.values()) {
            if (vote.getVoteOptionId() != null && vote.getVoteOptionId().equals(optionId)) {
                filteredVotes.add(createFilteredVote(vote));
            }
        }
        return filteredVotes;
    }
}

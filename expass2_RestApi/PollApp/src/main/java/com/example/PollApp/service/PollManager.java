package com.example.PollApp.service;


import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import com.example.PollApp.domain.User;
import com.example.PollApp.domain.Poll;
import com.example.PollApp.domain.VoteOption;
import com.example.PollApp.domain.Vote;

/**
 * Manages all domain objects in memory, e.g. by holding Polls and Users in a HashMap
 */

@Component
public class PollManager {
	private final Map<Long, User> users = new HashMap<>();
	private final Map<Long, Poll> polls = new HashMap<>();
	private final Map<Long, VoteOption> voteOptions = new HashMap<>();
	private final Map<Long, Vote> votes = new HashMap<>();

	// User CRUD
	public User getUser(Long id) { return users.get(id); }
	public Collection<User> getAllUsers() { return users.values(); }
	public void addUser(Long id, User user) { users.put(id, user); }
	public void removeUser(Long id) { users.remove(id); }

	// Poll CRUD
	public Poll getPoll(Long id) { return polls.get(id); }
	public Collection<Poll> getAllPolls() { return polls.values(); }
	public void addPoll(Long id, Poll poll) { polls.put(id, poll); }
	public void removePoll(Long id) { polls.remove(id); }

	// VoteOption CRUD
	public VoteOption getVoteOption(Long id) { return voteOptions.get(id); }
	public Collection<VoteOption> getAllVoteOptions() { return voteOptions.values(); }
	public void addVoteOption(Long id, VoteOption option) { voteOptions.put(id, option); }
	public void removeVoteOption(Long id) { voteOptions.remove(id); }

	// Vote CRUD
	public Vote getVote(Long id) { return votes.get(id); }
	public Collection<Vote> getAllVotes() { return votes.values(); }
	public void addVote(Long id, Vote vote) { votes.put(id, vote); }
	public void removeVote(Long id) { votes.remove(id); }
}

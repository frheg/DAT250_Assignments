package dat250.models;

import java.util.List;

public class VoteOption {
    private String optionId;
    private String caption;
    private List<Vote> votes;
    private String pollId;

    public VoteOption() {
    }

    // OptionId
    public String getOptionId()   {
        return optionId;
    }
    public void setOptionId(String optionId)    {
        this.optionId = optionId;
    }

    // Caption
    public String getCaption()  {
        return caption;
    }
    public void setCaption(String caption)  {
        this.caption = caption;
    }

    // Votes
    public List<Vote> getVotes()    {
        return votes;
    }
    public void setVotes(List<Vote> votes)  {
        this.votes = votes;     // NOTE: This implementation may be altered depending on votes should be appended or created new list each time.
    }

    // Poll
    public String getPollId() {
        return pollId;
    }
    public void setPollId(String pollId) {
        this.pollId = pollId;
    }
}

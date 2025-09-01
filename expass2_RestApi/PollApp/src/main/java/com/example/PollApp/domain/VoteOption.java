package com.example.PollApp.domain;

public class VoteOption {
    private String caption;
    private Long id;
    private int presentationOrder;
    private Poll poll;

    public VoteOption() {
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getPresentationOrder() {
        return presentationOrder;
    }

    public void setPresentationOrder(int presentationOrder) {
        this.presentationOrder = presentationOrder;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

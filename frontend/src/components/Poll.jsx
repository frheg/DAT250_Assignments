import {useEffect, useState} from "react";
import './Poll.css';
import Vote from "./Vote.js";

export default function Poll({poll, newVoteCallback}) {

    useEffect(() => {
        console.log('miau', poll, poll["question"])
    }, []);

    return (
        <div className="poll-card">
            <div className="poll-header">
                <h1 className="poll-question">{poll.question}</h1>
                <p className="poll-meta">
                    Created by: <span>{poll.createdBy.userId}</span> | Published: <span>{new Date(poll.publishedAt).toLocaleString("de-DE")}</span> | Valid until: <span>{new Date(poll.validUntil).toLocaleString("de-DE")}</span>
                </p>
            </div>

            <div className="poll-options">
                {poll.options.map(option => (
                    <div key={`option_${option.optionId}`} className="poll-option">
                        <div className="option-text">
                            <h2 className="option-caption">{option.caption}</h2>
                            <p className="option-votes">Votes: {option.votes.length}</p>
                        </div>
                        <Vote newVoteCallback={newVoteCallback} optionId={option.optionId} pollId={poll.pollId}/>
                    </div>
                ))}
            </div>
        </div>
    )
}
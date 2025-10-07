import {useEffect, useState} from "react";
import './Poll.css';

export default function Vote({optionId, pollId, newVoteCallback}) {

    const [votedBy, setVotedBy] = useState("user1")

    useEffect(() => {
        console.log('miau', optionId, pollId)
    }, []);

    function onVote() {
        let now = new Date()

        const vote = {
            user: {
                userId: votedBy
            },
            voteOptionId: optionId,
            pollId: pollId
        }

        newVoteCallback(vote)

        console.log("Vote")
    }

    return (
        <button
            className="vote-button"
            onClick={() => onVote()}
        >
            Vote
        </button>
    )
}
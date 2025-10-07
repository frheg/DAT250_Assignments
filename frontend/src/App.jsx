import {useEffect, useState} from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Poll from "./components/Poll.jsx";
import CreatePoll from "./components/CreatePoll.jsx";

const USER_ID = "user1"
const address = import.meta.env.VITE_API_BASE_URL

function App() {
    const [count, setCount] = useState(0)

    const [polls, setPolls] = useState([])
    const [userId, setUserId] = useState(localStorage.getItem('userId'))

    useEffect(() => {
        manageUser()

        fetchPolls()

        console.log('Address', import.meta.env.VITE_API_BASE_URL, address)
    }, []);


    async function manageUser() {
        const checkUserExists = await requestUser(userId);

        console.log('User Exists', checkUserExists)

        // Test if user already exists
        if (userId == null || checkUserExists == null) {
            const newUser = createNewUser({userId: USER_ID, email: 'dummy-user@gmx.no', password: 'dummy-user'})

            localStorage.setItem('userId', USER_ID)
            setUserId(USER_ID)

            fetchPolls()
        }
    }

    async function fetchPolls() {
        const response = await fetch(`${address}/polls`);
        if (!response.ok) throw new Error("Network error");
        const data = await response.json();

        // if it's an array
        for (const poll of data) {
            console.log(poll.pollId, poll.question, poll.publishedAt);
        }
        console.log(data)
        setPolls(data)
    }

    async function createNewPoll(poll) {
        const response = await fetch(`${address}/polls`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(poll)
        });

        if (!response.ok) throw new Error("Failed to submit vote");

        const data = await response.json();
        console.log(data)

        fetchPolls()
    }

    async function createNewUser(user) {
        const response = await fetch(`${address}/users`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(user)
        });

        if (!response.ok) throw new Error("Failed to submit vote");

        const data = await response.json();
        console.log(`Created new user: ${data}`)
    }

    async function requestUser(userId) {
        const response = await fetch(`${address}/users/` + userId);
        if (!response.ok) return null;

        const data = await response.json();
        console.log(`Created new user: ${data}`)
        return data
    }

    async function newVote(vote) {
        const response = await fetch(`${address}/votes`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(vote)
        });

        if (!response.ok) throw new Error("Failed to submit vote");

        const data = await response.json();
        console.log(data)

        fetchPolls()
    }

    return (
        <>
            {polls.map(poll => (
                <Poll newVoteCallback={newVote} key={`poll_${poll.pollId}`} poll={poll}/>
            ))}

            <CreatePoll createPollCallback={createNewPoll}/>
        </>
    )
}

export default App

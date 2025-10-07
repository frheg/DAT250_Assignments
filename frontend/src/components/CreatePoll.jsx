import {useEffect, useState} from "react";
import './CreatePoll.css';
import "./CreatePollForm.css"
import './Poll.css';

import Popup from 'reactjs-popup';

export default function CreatePoll({ createPollCallback }) {

    const [openPopup, setOpenPopup] = useState(false);

    const [question, setQuestion] = useState("");
    const [createdBy, setCreatedBy] = useState("user1"); // static for now
    const [publicAccess, setPublicAccess] = useState(true);
    const [validUntil, setValidUntil] = useState("");
    const [options, setOptions] = useState([{ caption: "" }, { caption: "" }]);


    useEffect(() => {
        console.log('miau')
    }, []);

    function onNewPoll() {
        setOpenPopup(true)
        console.log("New Poll")
    }

    function updateOption(index, value) {
        const newOptions = [...options];
        newOptions[index].caption = value;
        setOptions(newOptions);
    }

    function addOption() {
        setOptions([...options, { caption: "" }]);
    }

    function removeOption(index) {
        if (options.length > 2) {
            setOptions(options.filter((_, i) => i !== index));
        }
    }

    async function handleSubmit() {
        let validUntilDate = new Date(validUntil)
        const payload = {
            question: question,
            createdBy: { userId: createdBy },
            publicAccess: publicAccess,
            validUntil: (validUntil ? validUntilDate.toISOString() : null),
            options: options.filter(opt => opt.caption.trim() !== "")
        };

        createPollCallback(payload)

        setOpenPopup(false)
    }

    return (
        <>
            <button
                className="poll-button"
                onClick={() => onNewPoll()}
            >
                New Poll
            </button>

            <Popup onOpen={() => console.log("miau")} onClose={() => setOpenPopup(false)} open={openPopup}
                   arrow="false">
                <h3>Create a new Poll</h3>
                <div className="poll-form">
                    <div className="input_fields">
                        <label>Question</label>
                        <input
                            type="text"
                            value={question}
                            onChange={(e) => setQuestion(e.target.value)}
                        />
                    </div>

                    <div className="input_fields" id="checkbox-input-field">
                        <label>Public Access</label>
                        <input
                            type="checkbox"
                            checked={publicAccess}
                            onChange={(e) => setPublicAccess(e.target.checked)}
                        />
                    </div>

                    <div className="input_fields">
                        <label>Valid Until (optional)</label>
                        <input
                            type="datetime-local"
                            value={validUntil}
                            onChange={(e) => setValidUntil(e.target.value)}
                        />
                    </div>

                    <div className="options">
                        <label>Options</label>

                        { /* Inspired by ChatGPT */ }
                        {options.map((option, index) => (
                            <div key={index} className="input_fields">
                                <input
                                    type="text"
                                    value={option.caption}
                                    onChange={(e) => updateOption(index, e.target.value)}
                                />
                                {options.length > 2 && (
                                    <button type="button" onClick={() => removeOption(index)}>
                                        Remove
                                    </button>
                                )}
                            </div>
                        ))}
                        <button type="button" onClick={addOption}>Add Option</button>
                    </div>

                    <button type="button" onClick={handleSubmit}>Create Poll</button>
                </div>
            </Popup>
        </>
    )
}


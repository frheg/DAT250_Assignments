# Poll App with REST API

This project is developed in context of the module DAT250 (Advanced Softwaredevelopment) at the HVL in Bergen. 

The Expass files can be found here:
- [Expass2](./expass/dat250-expass2.md)
- [Expass3](./expass/dat250-expass3.md)

## Description of API

The current code is structured with MSC (Model-Service-Controller) architecture in mind. It includes:
- **Models**: Defines the data structures and their basic methods for the program.
    - `Poll`: Represents a poll containing a question and multiple choice options.
    - `User`: Represents a user who can create polls and vote.
    - `Vote`: Represents a user's vote on a option in a poll.
    - `VoteOption`: Represents an option within a poll that users can vote for.
- **Services**: Contains the business logic for the program.
    - `PollManager`: Manages the collections of users, polls, votes, and vote options, and provides methods to manipulate them.
- **Controllers**: Handles API-endpoints.
    - `PollController`: Manages API-endpoints for polls.
    - `UserController`: Manages API-endpoints for users.
    - `VoteController`: Manages API-endpoints for votes.
    - `VoteOptionController`: Manages API-endpoints for vote options.

### How to run and test current program:
In terminal, navigate to project root and run with gradlew:
```bash
./gradlew bootRun
```
Then use **Bruno** to interact with the API endpoints and test them. Default API endpoints are available at `http://localhost:8080/`. Check folder `brunofiles` for example requests.

## Description of Frontend

Can be static loaded on the path `\`.
- E.g. `http://localhost:8080`

### For development

The frontend is developed in ReactJS with JavaScript and is located in the folder: `/frontend`

To start the development server run: `npm run dev`

Keep in mind, that a `.env` file needs to be configured. More details can be found in: [Expass3](./expass/dat250-expass3.md).

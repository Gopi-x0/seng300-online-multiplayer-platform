# Quality & Testing Sub-Team: Work Summary

**Members:** Olivia Park, Jack Lu, Dima Levshukov, Esha Esha

---
## Overview

The Quality & Testing team was responsible for verifying the correctness and reliability of the platform through automated test suites. Tests were written across all major subsystems to ensure that core platform services, game logic, and stubbed external interfaces behave as expected.

---
## Test Descriptions

### User Management (`user`)

- **InGamePlayerTests:** Validates in-game player state, ensuring player data is correctly maintained during active gameplay sessions.
- **PlayerProfileTest:** Tests player profile creation, retrieval, and display of stats such as games played and win/loss records.
- **UserDataTest:** Verifies basic getters and setters on the user data model to confirm data integrity.
- **UserManagerTest:** Tests user lifecycle operations including registration, lookup, and account management.
- **playerGameRecordsTests:** Ensures that individual game history records are accurately created and associated with the correct user.

### Authentication (`authManager`)

- **LoginTest:** Validates the login flow, including credential verification and appropriate handling of invalid login attempts.

### Leaderboard (`leaderboard`)

- **GetTopPlayersByRankTest:** Tests retrieval of top-ranked players, verifying correct ordering and filtering by rank.
- **LogGameResultTest:** Ensures that completed game results are properly logged and reflected in player rankings.

### Game Logic (`gamelogic`)

- **Connect4Test:** Tests move placement, column validation, and win-condition detection for Connect Four.
- **TicTacToe:** Verifies turn handling, move validation, and win/draw detection for Tic-Tac-Toe.

### Game Session Management (`service`)

- **GameSessionManagerTest:** Tests the session lifecycle including session creation, player joining, state transitions, and session termination.

### Notifications (`notificationService`)

- **NotificationTest:** Validates that player notifications are correctly generated and delivered in response to platform events.

### Network (`network`)

- **NetworkTestGameServerTest:** Tests client-server communication logic against stubbed network interfaces to verify message handling and connection behaviour.

### Database Stubs (`stubs`)

- **DatabaseStubTest:** Confirms that the database stub layer correctly simulates persistence operations, ensuring the platform functions as expected in the absence of a live database.


## Notes

- All tests were written in Java using standard testing frameworks.
- Database and network layers are tested against stubs, consistent with the project's architectural requirements.
- All test suites pass and are runnable via the standard build process.
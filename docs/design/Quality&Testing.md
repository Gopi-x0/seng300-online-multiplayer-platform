# Quality & Testing Sub-Team: Work Summary

**Members:** Olivia Park, Jack Lu, Dima Levshukov, Esha Esha


The Quality & Testing team was responsible for verifying the correctness and reliability of the platform through automated test suites. Tests were written across all major subsystems to ensure that core platform services, game logic, and stubbed external interfaces behave as expected.

---
## Prerequisites

All tests require a **stock, unmodified `.dat` file** from the repository. If the file has been altered by previous runs or manual edits, tests may produce unexpected results. Reset it before running the full suite.

---
## Test Coverage

### Game Logic
- **Connect4Test**: Setup, move validation, piece stacking, turn alternation, wins (vertical, horizontal, diagonal, anti-diagonal), draws, and edge cases.
- **TicTacToeTest**: Setup, move placement, validation (out-of-bounds, wrong turn, occupied cell), wins for both players, draws, and win-on-last-move handling.

### Lobby
- **LobbyTests**: LobbySession lifecycle (add/remove players, full lobby, duplicates, post-start rejection). LobbyManager creation, code format, and removal safety.

### Networking
- **NetworkTestGameServerTest**: Server/client binding, send/receive, message ordering, disconnect detection, reset-client flow, round-trip echo, shutdown safety.

### Service Layer
- **GameSessionManagerTest**: Starting and playing Connect4/TicTacToe sessions, valid and invalid move handling, state getters, end-game cleanup.

### Data & Persistence
- **DatabaseStubTest**: User creation, authentication, field updates, deletion, game result recording, leaderboards, ranking, match history.
- **UserDataTest**: Getters/setters, derived stats, serialization round-trip, edge cases.
- **UserManagerTest**: Registration/login validation, session management, profile changes, ranking, save/load persistence.
- **playerGameRecordsTests**: Recording game outcomes through UserManager, streak tracking, points boundaries.

### Players
- **InGamePlayerTests**: Constructor, toString, null/empty/edge-case inputs.
- **PlayerProfileTest**: Constructor, getters, null/empty handling.

### Integration 
- Full user-flow integration tests covering end-to-end platform scenarios are in progress.
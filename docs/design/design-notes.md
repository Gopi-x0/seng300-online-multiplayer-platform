# Student Gaming Platform - Design Notes

**SENG 300 W26 Project Group 11 · Iteration 3 · University of Calgary**

---

## 1. Architecture Overview

The Student Gaming Platform (SGP) is a JavaFX desktop application that provides multiplayer Tic-Tac-Toe and Connect 4 over localhost TCP. The architecture follows a layered design:

| Layer | Package(s) | Responsibility |
|-------|-----------|---------------|
| **UI** | `screens`, `MainApp` | JavaFX screens, custom window chrome, screen navigation via callbacks |
| **Service** | `service` | `UserManager` (accounts, persistence), `GameSessionManager` (engine orchestration) |
| **Network** | `network` | `GameServer`/`GameClient` (TCP), `LobbyManager`/`LobbySession` (matchmaking) |
| **Engine** | `connect4engine`, `tictactoeengine` | Pure game logic implementing `GameEngine<S>` |
| **Contract** | `interfaces` | Shared types: `GameEngine<S>`, `Move`, `MoveResult`, `ValidationCode` |
| **Model** | `model` | DTOs: `UserData`, `GameState`, `GameStatus`, `GameType`, `PlayerProfile` |
| **Stub** | `stubs` | `DatabaseStub` documenting a future DB adapter contract |

## 2. Key Design Decisions

### Custom Window Chrome
`MainApp` uses `StageStyle.UNDECORATED` with a hand-built title bar (min/max/close, drag-to-move, double-click maximize, edge resize). This gives the platform a polished, branded appearance while retaining full window management.

### Callback-Based Navigation
All screen transitions are wired through `Runnable` and `BiConsumer` callbacks injected by `MainApp`. No screen knows about `MainApp` or any other screen directly, keeping coupling low and making screens individually testable.

### Engine Immutability
Both `Connect4Game` and `TicTacToeEngine` return new state objects from `applyMove()` - they never mutate the previous state. This makes game history clean, undo-safe, and eliminates a class of concurrency bugs.

### TCP Networking (Replaces Stubs)
Iteration 2 used a `NetworkStub` (console print). Iteration 3 introduces real TCP:
- **GameServer**: `ServerSocket` with port scan (5300–5325), accepts one client, line-based protocol, `Platform.runLater` for thread safety.
- **GameClient**: connects to host, daemon read thread, `DISCONNECTED` detection.
- Singleton pattern (`activeInstance`) for global shutdown in `MainApp`.

### Lobby System
`LobbyManager` generates 8-character hex codes. `HostLobby` starts a server and displays the code; `JoinLobby` scans ports to find the server, sends `JOIN:<code>:<username>`, and waits for `START`.

### Persistence
`UserManager` reads/writes `sgp_users.dat` (pipe-delimited, one user per line) with `FileLock` for concurrent safety. `mergeSaveCurrentUser()` performs atomic read-modify-write under a single lock.

### Database Stub
`DatabaseStub` is an in-memory class that mirrors every operation `UserManager` performs, expressed as a database-style API. It is **not wired into live code** - it documents the contract a future SQLite/Postgres adapter would implement.

## 3. What Is Stubbed vs Real

| Component | Status                                                  |
|-----------|---------------------------------------------------------|
| User accounts (register, login, profile) | **Real** - `UserManager` + `sgp_users.dat`              |
| Game engines (TTT, C4) | **Real** - full validate/apply/evaluate lifecycle       |
| TCP multiplayer | **Real** - `GameServer` + `GameClient` on localhost     |
| Lobby matchmaking | **Real** - `LobbyManager` + `LobbySession`              |
| In-game chat | **Real** - `ChatWidget` + `CHAT:` protocol over TCP     |
| Database backend | **Stubbed** - `DatabaseStub` (in-memory, not connected) |
| Online hosting | **Not implemented** - localhost only                    |

## 4. Known Limitations

- **Single connection per server**: `GameServer` accepts one client only (2-player games).
- **No reconnection**: if a player disconnects, the game ends - there is no rejoin flow.
- **Plaintext passwords**: `UserManager` stores passwords as-is; production would hash them.
- **Localhost only**: networking works on one machine (or LAN if firewall allows); no internet hosting.
- **No persistent match history**: `DatabaseStub.MatchRecord` exists as a contract but is not persisted to disk.

## 5. Diagram Index

All diagrams live in `docs/design/plantuml/` (`.puml` source + `.png` render).

| # | File | Type | Description                                                                          |
|---|------|------|--------------------------------------------------------------------------------------|
| 00 | `00-overview-packages.puml` | Package / Component | High-level package dependencies and layer overview                                   |
| 01 | `01-deployment.puml` | Deployment / Process | Host and Guest JVM processes, TCP communication                                      |
| 10 | `10-model.puml` | Class | `model` package - `UserData`, `GameState`, `GameStatus`, `GameType`, `PlayerProfile` |
| 20 | `20-interfaces-engines.puml` | Class | `interfaces` + `connect4engine` + `tictactoeengine`                                  |
| 30 | `30-network.puml` | Class | `network` package - `GameServer`, `GameClient`, `LobbyManager`, `LobbySession`       |
| 40 | `40-service-stubs.puml` | Class | `service` + `stubs` - `UserManager`, `GameSessionManager`, `DatabaseStub`            |
| 50 | `50-screens.puml` | Class | All UI screen classes + `MainApp`                                                    |
| 55 | `55-usecase.puml` | Use Case | Actors (Player, Host, Guest, File System, TCP) and all platform use cases            |
| 60 | `60-sequence-auth-home.puml` | Sequence | Launch → Auth → Home flow                                                            |
| 61 | `61-sequence-lobby-game.puml` | Sequence | Lobby → Host/Join → Play → Win flow                                                  |
| 62 | `62-sequence-chat-disconnect.puml` | Sequence | In-game chat and disconnect handling                                                 |
| 70 | `70-state-lobby.puml` | State Machine | Lobby creation, matchmaking, and match lifecycle states                              |
| 80 | `80-domain-persistence.puml` | Domain / ER | Persistence model - `UserData`, `sgp_users.dat`, `DatabaseStub` contract             |
| 90 | `90-include-all.puml` | Umbrella Class | Condensed single-page view of all types and key relationships                        |
| 95 | `95-test-coverage.puml` | Test Coverage Map | Test classes mapped to production packages they cover (unit + integration, 245 tests) |

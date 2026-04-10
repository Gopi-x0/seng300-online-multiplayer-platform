# Student Gaming Platform (SGP)

A JavaFX desktop application for playing Tic-Tac-Toe and Connect 4 over a local network. Built for SENG 300 W26 by Project Group 11 at the University of Calgary.

The platform supports user registration, login, profile management, real-time multiplayer via TCP sockets, in-game chat, stat tracking, and leaderboard rankings.

## Table of Contents

- [Building and Running](#building-and-running)
- [Project Structure](#project-structure)
- [Application Entry Point](#application-entry-point---mainappjava)
- [Model Layer](#model-layer)
- [Service Layer](#service-layer)
- [Network Layer](#network-layer)
- [Game Engines](#game-engines)
- [Interfaces](#interfaces)
- [Screens (UI)](#screens-ui)
- [Database Stub](#database-stub)
- [Data Persistence](#data-persistence)

## Building and Running

### Prerequisites

- **Java 25** (JDK 25 or later)
- **Maven** (or use the included Maven wrapper)

The project uses JavaFX 25 which is pulled in automatically by Maven.

### Build and Run

Using the Maven wrapper (no Maven install needed):

```bash
# On Linux / macOS
./mvnw clean javafx:run

# On Windows
mvnw.cmd clean javafx:run
```

Or if you have Maven installed globally:

```bash
mvn clean javafx:run
```

### Build Only (without running)

```bash
mvn clean compile
```

### Running from an IDE

If you are using IntelliJ IDEA or another IDE, import the project as a Maven project. The main class is:

```
ca.ucalgary.seng300.MainApp
```

Make sure your IDE is configured to use JDK 25 and that the JavaFX modules are resolved through Maven.

### Multiplayer Setup

Both players must be running the application on the same machine (localhost). The host creates a lobby, which starts a TCP server on a port in the range 5300-5325. The guest joins by entering the 8-character lobby code and connecting to the host's server on localhost.

---

## Project Structure

```
src/main/java/ca/ucalgary/seng300/
    MainApp.java                 -- Application entry point and screen navigation
    connect4engine/              -- Connect 4 game engine
        Board.java
        Connect4Game.java
        Connect4State.java
        Player.java
    interfaces/                  -- Shared interfaces for game engines
        GameEngine.java
        Move.java
        MoveResult.java
        ValidationCode.java
    model/                       -- Data models and enums
        GameState.java
        GameStatus.java
        GameType.java
        PlayerProfile.java
        UserData.java
    network/                     -- TCP networking for multiplayer
        GameClient.java
        GameServer.java
        LobbyManager.java
        LobbySession.java
    screens/                     -- All JavaFX UI screens
        AuthenticationScreen.java
        ChatWidget.java
        GameboardC4.java
        GameboardTTT.java
        HomePage.java
        HostLobby.java
        InstructionsC4.java
        InstructionsTTT.java
        JoinLobby.java
        Lobby.java
        ProfileDashboard.java
        SettingsScreen.java
        WelcomePage.java
        WinScreen.java
    service/                     -- Business logic and session management
        GameSessionManager.java
        UserManager.java
    stubs/                       -- Database stub for future migration
        DatabaseStub.java
```

---

## Application Entry Point - MainApp.java

`MainApp` extends `javafx.application.Application` and is the single entry point for the entire platform. It creates an undecorated JavaFX stage (no native OS title bar) and builds a custom title bar with minimize, maximize, and close buttons.

### Custom Window Management

The window chrome is fully custom. The title bar is built as an HBox with a title label on the left and window control buttons on the right. The close button also shuts down any active GameServer and GameClient instances to make sure TCP sockets are cleaned up.

Window dragging is implemented by tracking mouse press offsets on the title bar and updating the stage position on drag. Double-clicking the title bar toggles maximize. If the window is maximized and you start dragging, it un-maximizes first and recalculates the drag offset so the window snaps to your cursor properly.

Edge resizing is handled by listening to mouse move events on the scene edges. The cursor changes to the appropriate resize direction (N, S, E, W, NE, NW, SE, SW). On press+drag, the stage position and dimensions are updated with a minimum window size of 1050x600.

### Screen Navigation

MainApp owns all screen transitions. Each screen is constructed with callback Runnables or BiConsumers, and MainApp swaps the center content of a BorderPane (`appShell`). The flow is:

1. **WelcomePage** - initial landing page
2. **AuthenticationScreen** - login or register
3. **HomePage** - main hub after login
4. From HomePage, the user can go to:
   - **SettingsScreen** - change username, email, password, or logout
   - **ProfileDashboard** - view stats and rank
   - **InstructionsTTT** or **InstructionsC4** - game rules
5. From instructions, the user goes to the **Lobby** screen (Host or Join)
6. From the lobby: **HostLobby** or **JoinLobby**
7. Finally, the actual game board: **GameboardTTT** or **GameboardC4**

When navigating away from a game, `GameServer.shutdownActive()` and `GameClient.shutdownActive()` are called to tear down any open network connections.

---

## Model Layer

### UserData.java

The main data model for a user account. Stores the user's email (primary key), username (unique key), password, and per-game stats for both Tic-Tac-Toe and Connect 4. Stats tracked per game type include: games played, wins, losses, draws, current streak, best streak, and points.

UserData also has derived stat methods like `getTotalGames()`, `getTotalWins()`, `getOverallWinRate()`, `getTttWinRate()`, `getC4WinRate()`, and `getBestStreak()` which aggregate across both game types.

Serialization and deserialization are done with a pipe-delimited format. The `serialize()` method joins all 17 fields with `|` separators, and `deserialize()` splits a line by `|` and parses each field back. This is how user data is persisted to `sgp_users.dat`.

### GameState.java

A platform-level snapshot of the current game. Stores the game type (string like "connect4" or "tictactoe"), the current `GameStatus` enum, the active player index, and a generic Object reference to the engine-specific state (either `Connect4State` or `tictactoeengine.GameState`). This is what gets passed between the UI and the `GameSessionManager`.

### GameStatus.java

An enum with five values: `SETUP`, `IN_PROGRESS`, `P1_WINS`, `P2_WINS`, and `DRAW`. Used across both game engines and the platform layer to track game outcomes.

### GameType.java

A simple enum with two values: `TTT` (Tic-Tac-Toe) and `C4` (Connect 4). Used for routing in the lobby flow and for identifying game types in the UI.

### PlayerProfile.java

A lightweight model that pairs a player ID string with a username string. Used by `GameSessionManager.startGame()` to identify the two players in a game session.

---

## Service Layer

### UserManager.java

The central service for user account management. All user data lives in a static `List<UserData>` and is persisted to `sgp_users.dat` (a flat file in the project root). On class load (static initializer), it calls `loadUsers()`, then `ensureAdminExists()` and `ensureDummyUsersExist()` to seed the database with a default admin account and sample user data for testing.

Key operations:

- **Registration**: `register(email, password)` validates the email format, checks for duplicates, auto-generates a username from the email prefix (appending a number if taken), saves to file, and sets the new user as the current user.
- **Login**: `login(emailOrUsername, password)` accepts either the email or the username and does a case-insensitive match. On success it sets the current user.
- **Profile updates**: `changeUsername()`, `changeEmail()`, and `changePassword()` all validate input (no pipe characters allowed since they are the delimiter, minimum 3 chars for passwords, uniqueness checks), update the in-memory user object, and persist to file.
- **Game result recording**: `recordGameResult(gameType, isWin, isDraw)` updates the current user's stats (games played, wins/losses/draws, streak, points) and calls `mergeSaveCurrentUser()`.
- **Merge save**: `mergeSaveCurrentUser()` does an atomic read-modify-write with an exclusive file lock. It reads the latest file content, replaces only the current user's row, and writes everything back. This prevents two concurrent instances from clobbering each other's data.
- **Ranking**: `getUserRank(user)` sorts all users by total points descending and returns the 1-based rank.

File locking uses `java.nio.channels.FileLock` on a `RandomAccessFile` to make sure concurrent writes are safe. The pipe (`|`) character is banned from all user-facing fields because it is used as the field delimiter.

### GameSessionManager.java

A static bridge between the UI screens and the game engines. It holds references to the current `GameEngine`, the engine-specific state object, the platform-level `GameState`, and the list of players.

- **startGame(gameType, p1, p2)**: Creates either a `Connect4Game` or `TicTacToeEngine`, calls `createInitialState()` with the player IDs, and wraps the result in a platform `GameState` set to `IN_PROGRESS`.
- **submitMove(column)**: For Connect 4. Builds a `Move` from the current player's info and the column, validates it, applies it, and maps the resulting `Connect4State` status to a `GameStatus` enum. Returns the updated `GameState`.
- **submitMoveTTT(row, col)**: For Tic-Tac-Toe. Same flow but with row+col coordinates and mapping `GameStatusTicTacToeEngine` values (X_WINS, O_WINS, DRAW) to the platform `GameStatus` (P1_WINS, P2_WINS, DRAW).
- **endGame(gameState)**: Cleans up all references (engine, state, players) so the next game can start fresh.
- **getEngineState()** and **getCurrentState()**: Accessors used by the gameboard UI to read the raw engine state for rendering.

---

## Network Layer

All multiplayer is done via raw TCP sockets on localhost. The host runs a `GameServer` and the guest connects with a `GameClient`. Messages are plain text lines sent over the socket, one message per line.

### GameServer.java

Wraps a `java.net.ServerSocket`. The constructor takes a port and opens the server socket. The static factory `createWithPortScan()` tries ports from 5300 to 5325 in sequence until it finds one that is available. This means multiple lobbies can run at the same time on different ports.

Once created, `waitForClient()` spawns a background daemon thread that calls `serverSocket.accept()` to wait for one client connection. After the client connects, it starts a listen thread that reads lines from the input stream and dispatches them to the registered `messageHandler` on the JavaFX application thread (using `Platform.runLater()`).

`send(message)` writes a line to the client. `resetClient()` closes the client connection but keeps the server socket open (used when a guest disconnects mid-lobby so the host can accept a new guest). `shutdown()` closes everything.

There is a static `activeInstance` singleton pattern so the gameboard screens and MainApp can access the running server without passing references around. `shutdownActive()` is called on app close and when navigating home from a game.

### GameClient.java

Wraps a `java.net.Socket`. `connect(host, port)` opens a socket to the given address, sets up the PrintWriter and BufferedReader, and starts a background daemon listener thread. The listener reads lines and passes them to the `messageHandler` on the JavaFX thread. If the server disconnects, the listener fires a "DISCONNECTED" message.

`send(message)` writes a line. `shutdown()` closes streams and socket.

Same static singleton pattern as GameServer.

### Network Protocol

Messages are plain text, colon-delimited. The key message types are:

**Lobby phase** (HostLobby / JoinLobby):

- Guest sends: `JOIN:<lobbyCode>:<username>`
- Host replies: `JOINED:<hostUsername>` on success, or `ERROR:<reason>` on failure
- Host sends: `START` when the game begins
- Host sends: `LOBBY_CLOSED` if the host leaves

**Game phase** (GameboardTTT / GameboardC4):

- Guest sends: `MOVE_TTT:<row>:<col>` or `MOVE_C4:<col>`
- Host sends: `STATE_TTT:<grid>:<activePlayerId>:<status>:<lastRow>:<lastCol>` or `STATE_C4:<grid>:<currentSymbol>:<status>:<lastCol>`
- Either side sends: `CHAT:<username>:<text>`
- Either side sends: `PLAYER_LEFT` when leaving the game

The grid is serialized as rows separated by `/`. For TTT, each cell is `X`, `O`, or `.`. For C4, each cell is `R`, `Y`, or `.`.

### LobbyManager.java

A static utility that creates and tracks lobby sessions. `createLobby()` generates a unique 8-character hexadecimal code (using characters 0-9 and A-F) and creates a `LobbySession`. It retries up to 100 times if there is a code collision. `removeLobby()` cleans up the session map when the host leaves.

### LobbySession.java

A data class representing one lobby. Stores the lobby code, game type, game name, host username, player list (max 2), and whether the game has started. Provides `addPlayer()` and `removePlayer()` with guards: you cannot add a player if the lobby is full or the game has started, and you cannot remove the host.

---

## Game Engines

Both engines implement the `GameEngine<S>` interface, which defines the move lifecycle: `createInitialState()`, `validateMove()`, `applyMove()`, and `evaluateEnd()`.

### Connect 4 Engine

**Board.java**: The 6x7 grid stored as a `char[][]` where `.` is empty, `R` is Player 1 (Red), and `Y` is Player 2 (Yellow). Key operations:

- `dropPiece(column, player)`: Simulates gravity by scanning from the bottom row upward and placing the piece in the first empty cell. Returns the row where it landed.
- `isColumnFull(column)`: Returns true if the top cell of the column is occupied.
- `isFull()`: Returns true if every column's top cell is occupied (used for draw detection).
- `checkWin(row, column)`: After a piece is placed, checks all four directions (horizontal, vertical, and both diagonals) from the placed position. For each direction, it counts consecutive matching symbols on both sides. If the total count (plus the placed piece) reaches 4, it is a win.

The Board has a copy constructor for deep cloning, which is used by `applyMove()` to preserve immutability of previous states.

**Player.java**: Simple value object pairing a platform player ID with a board symbol character (`R` or `Y`).

**Connect4State.java**: An immutable snapshot of the game at any point. Holds the Board, both Player objects, which player moves next, the GameStatus, and the winner (if any). Provides `getOtherPlayer()` for clean turn swapping.

**Connect4Game.java**: The engine implementation.

- `createInitialState()`: Takes two player IDs, assigns Player 1 as `R` and Player 2 as `Y`, creates an empty board, and returns a `Connect4State` with status `IN_PROGRESS`.
- `validateMove()`: Checks that the game is still in progress, the column is in bounds (0-6), and the column is not full. Returns a `MoveResult` with a `ValidationCode`.
- `applyMove()`: Deep copies the board, drops the piece, checks for a win or full board, swaps the active player, and returns a new `Connect4State`.

### Tic-Tac-Toe Engine

**Symbol.java**: Enum with values `X`, `O`, and `EMPTY`.

**GameStatusTicTacToeEngine.java**: Enum with `IN_PROGRESS`, `X_WINS`, `O_WINS`, and `DRAW`.

**tictactoeengine/GameState.java**: Immutable DTO holding the 3x3 board (as `Symbol[][]`), the active player ID, the game status, the move history list, and the player ID list. The board is defensively copied on construction to preserve immutability. Provides `getCell(r, c)` for single cell reads and `getBoard()` for a full copy.

**TicTacToeEngine.java**: The engine implementation.

- `createInitialState()`: Creates a 3x3 board filled with `Symbol.EMPTY`, sets the first player as active, status as `IN_PROGRESS`.
- `validateMove()`: Checks in order: game not over, correct player's turn, coordinates in bounds (0-2), and cell is empty. Returns a `MoveResult` with a specific `ValidationCode` for each failure case.
- `applyMove()`: Clones the board, places the symbol, adds the move to history, toggles the active player, and calls `evaluateEnd()`.
- `evaluateEnd()`: Checks all 8 possible win lines (3 rows, 3 columns, 2 diagonals) for both X and O. If neither has won and the board is full, it is a draw. Returns a new `GameState` with the updated status.

---

## Interfaces

### GameEngine.java

A generic interface `GameEngine<S>` that both game engines implement. Defines four methods:

1. `createInitialState(List<String> playerIds)` - creates a fresh game state
2. `validateMove(S state, Move move)` - checks if a move is legal without changing state
3. `applyMove(S state, Move move)` - applies a validated move and returns new state
4. `evaluateEnd(S state)` - checks if the game has reached a terminal state

### Move.java

A value object that captures a move attempt: row, column, player ID, and symbol. Used by both engines. For Connect 4, the row field is set to 0 (only column matters since gravity handles the row). For TTT, both row and col are used.

### MoveResult.java

Returned by `validateMove()`. Contains a boolean `isValid`, a human-readable `message`, and a `ValidationCode`. Has static factory methods `success()` and `failure(code, message)` with a private constructor to enforce this pattern.

### ValidationCode.java

Enum listing the possible validation outcomes: `SUCCESS`, `CELL_OCCUPIED`, `OUT_OF_BOUNDS`, `WRONG_TURN`, `GAME_ALREADY_OVER`.

---

## Screens (UI)

All screens are JavaFX layout containers (StackPane, BorderPane, VBox, etc.) styled inline with a dark navy theme. There are no external CSS files; all styling is done through `-fx-` style strings directly on nodes. Buttons use hover/press/release animations (ScaleTransition) for polish.

### WelcomePage.java

The initial landing page. Displays the SGP logo (loaded from resources), a "WELCOME TO" heading, a "Student Gaming Platform" subtitle, and a "Continue to Play!" button. The logo is loaded from `/ca/ucalgary/seng300/images/sgp-logo.png`. Font resolution tries to use "Rajdhani" and "Inter" first, falling back to "Trebuchet MS" and "Verdana" if not available.

### AuthenticationScreen.java

Displays two side-by-side cards: Register and Login.

**Register card**: Email field and password field with an eye toggle button for show/hide. Validation checks for:

- Email must contain `@`
- Email cannot contain `|` (pipe is the data delimiter)
- Password cannot be empty
- Password must be at least 3 characters
- Password cannot contain `|`

On success, calls `UserManager.register()` which auto-generates a username from the email prefix.

**Login card**: Accepts email OR username plus password. Has a "Forgot?" link that shows a modal popup telling the user to contact the platform admin (since there is no email recovery system).

Both cards use animated primary buttons (gradient blue, scale on hover/press) and red error labels that appear when validation fails.

### HomePage.java

The main hub after login. Divided into sections:

**Top bar**: Shows the SGP logo on the left and the current username, a Profile link, and a Settings link on the right. The username is truncated with an ellipsis if too long.

**Game cards**: Two side-by-side cards for Tic-Tac-Toe and Connect 4. Each card has a mini board preview (a small GridPane showing a sample game state), a title, a subtitle, and a "PLAY NOW" button. The TTT preview shows a static X/O layout, the C4 preview shows colored circles.

**Record buttons**: Two buttons ("View Self Records" and "All Player Records") that control what data appears in the record table below.

**Record table**: A styled TableView showing player stats. Columns: PLAYER, GAME, WINS, LOSSES, DRAWS, WIN RATE, STREAK, POINTS. The table styling is heavily customized to match the dark theme, with custom cell factories, header styling, and scroll bar styling all applied programmatically via CSS lookups. The "View Self Records" button shows only the current user's stats, while "All Player Records" shows everyone.

### InstructionsTTT.java and InstructionsC4.java

Instruction screens for each game. Both follow the same structure: a back button, a title, and three styled cards (Rules, Objective, Play) explaining how to play. A "PLAY" button at the bottom triggers navigation to the lobby screen. Cards use a dark gradient background with a cyan glow DropShadow on hover.

### Lobby.java

Intermediate screen shown after pressing PLAY on the instructions. Displays two large cards: "Host Game" (with a laptop icon) and "Join Game" (with a globe icon). Each card has a description and a "Select" button. Choosing Host navigates to HostLobby, choosing Join navigates to JoinLobby.

### HostLobby.java

The host creates a lobby session via `LobbyManager.createLobby()` which generates the 8-character hex code. Then it calls `GameServer.createWithPortScan()` to start a TCP server on the first available port in the 5300-5325 range.

The UI shows:

- The lobby code in a green highlighted label with a "Copy" button (copies to system clipboard)
- A terminal-style log (VBox inside a ScrollPane) showing connection events in colored text
- A player list panel on the right
- A "Start Game" button that is disabled until a guest joins

When a guest connects and sends `JOIN:<code>:<username>`, the host validates the code, adds the player to the session, sends back `JOINED:<hostUsername>`, and enables the Start button. If the guest disconnects, the host resets and waits for a new connection. Pressing "Start Game" sends `START` to the guest and navigates both players to the gameboard.

If the host presses "Back", it sends `LOBBY_CLOSED` to any connected guest and shuts down the server.

### JoinLobby.java

The guest enters an 8-character lobby code in a text field. On pressing "Join", the client uses a port scanning approach: it tries connecting to `localhost` on ports 5300 through 5325 sequentially. For each port, it creates a new `GameClient`, connects, and sends `JOIN:<code>:<username>`. If the server responds with `ERROR:Invalid lobby code`, the client disconnects and tries the next port. If no server accepts the code, it shows an error.

On `JOINED:<hostUsername>`, the UI switches to a waiting state showing "Connected to [host]'s lobby" with a green checkmark. The guest waits for the host to send `START`, at which point both navigate to the gameboard.

If the host closes the lobby, the guest sees a modal popup saying "Host has closed the Lobby!" with a dismiss button that navigates back.

### GameboardTTT.java

The Tic-Tac-Toe game board. Supports both local and networked play:

**Layout**: A 3x3 grid of StackPane cells (150x150 each) with cyan borders, flanked by decorative streak columns (faded X symbols on the left, faded O circles on the right). Title and subtitle labels show the game name and whose turn it is. A ChatWidget sits on the right side.

**Local mode**: Both players click on the same board. Each click calls `GameSessionManager.submitMoveTTT(row, col)`, updates the board, and checks for game end.

**Networked mode**: The host runs the engine locally. When the host clicks, it processes the move and sends `STATE_TTT:<grid>:<activePlayerId>:<status>:<lastRow>:<lastCol>` to the guest. When the guest clicks, it sends `MOVE_TTT:<row>:<col>` to the host, who processes it and sends back the new state.

The guest renders the board entirely from the state messages it receives from the host. Both sides animate newly placed symbols with a fade+scale transition.

On game end, `UserManager.recordGameResult()` is called to update stats, and a WinScreen overlay is shown. The host sees P1_WINS as a win, the guest sees P1_WINS as a loss (and vice versa for P2_WINS).

If a player disconnects mid-game, the other player automatically wins.

### GameboardC4.java

Same architecture as GameboardTTT but for Connect 4. The grid is 6 rows by 7 columns of circles. Clicking a column drops a piece with an animation. The network protocol uses `MOVE_C4:<col>` and `STATE_C4:<grid>:<currentSymbol>:<status>:<lastCol>`.

The grid serialization uses `R` for red, `Y` for yellow, and `.` for empty, with rows separated by `/`. The board reads from the engine's `Connect4State` to update circle fill colors (red = `#ff4b4b`, yellow = `#ffd93d`).

### ChatWidget.java

A side panel (280px wide) displayed alongside the game board during play. Features:

- A "Match Chat" header
- A scrollable message area
- A text input field with a 200-character limit and a live character counter
- Messages are timestamped with the current time in "h:mm a" format

User messages appear right-aligned with a cyan background. Opponent messages appear left-aligned with a dark blue background and the sender's name in green above. System messages (like "Chat is now active") appear centered in italic.

The widget takes a `Consumer<String>` send handler. When the user presses Enter, the message is displayed locally and the handler is called. The gameboard's `sendChatMessage` method prepends `CHAT:<username>:` and sends it over the network. Incoming `CHAT:` messages are parsed and displayed via `receiveMessage()`.

When the opponent disconnects, `notifyOpponentLeft()` disables the input field and shows a system message.

### ProfileDashboard.java

Displays the current user's profile and statistics. Shows:

- Username and "Member since [month year]"
- Global rank (1-based, computed by `UserManager.getUserRank()`) and total points
- Four stat cards: Total Games, Total Wins, Win Rate, Best Streak
- Two per-game cards (Tic-Tac-Toe and Connect 4) each showing games played, wins, losses, draws, a win rate progress bar, current streak, best streak, and points earned

All data is read live from `UserManager.getCurrentUser()`. If no user is logged in, a placeholder "Guest" user is shown.

### SettingsScreen.java

A scrollable settings page with four cards:

- **Change Username**: Text field pre-filled with current username. Validates that the name is not empty, does not contain `|`, and is not already taken.
- **Change Email**: Text field pre-filled with current email. Validates that it contains `@`, does not contain `|`, and is not already in use.
- **Change Password**: Three fields (current password, new password, confirm new password). Validates minimum length of 3, no `|` character, passwords match, and current password is correct.
- **Logout**: A red button that calls `UserManager.logout()` and navigates back to the welcome page.

There is also a "Credits" button in the top bar that shows a modal popup listing all student contributors.

Each card shows inline feedback (green for success, red for error) after attempting a save.

### WinScreen.java

A reusable overlay displayed on top of the game board when a game ends. It is a StackPane with two layers: a semi-transparent dark scrim (dimming the game behind it) and a centered result card.

The card shows an icon (crown for win, sad face for loss, handshake for draw), a large text message, and a red "RETURN" button. The card pops in with a ScaleTransition and the whole overlay fades in.

Static helper methods `showWin()`, `showLose()`, and `showDraw()` add the overlay to a parent StackPane and trigger the animation. The dismiss button fades out the overlay, removes it from the parent, and runs the `onDismiss` callback (which navigates home).

---

## Database Stub

### DatabaseStub.java

This class defines the contract that a real database adapter would fulfill in a future iteration. Right now, all actual persistence goes through `UserManager`, which reads and writes the flat file `sgp_users.dat`. The stub mirrors every operation the platform already does but expressed as a database-style API:

- **User CRUD**: `createUser()`, `authenticateUser()`, `findUserByEmail()`, `updateUsername()`, `updateEmail()`, `updatePassword()`, `deleteUser()`
- **Stats recording**: `recordGameResult()` updates per-game stats with the same logic as `UserManager`
- **Leaderboard queries**: `getGlobalLeaderboard()`, `getTttLeaderboard()`, `getC4Leaderboard()`, `getUserRank()`
- **Match history**: `saveMatchRecord()` and `getMatchHistory()` use an in-memory list of `MatchRecord` objects

The `MatchRecord` inner class is a simple DTO with the game type, both player emails, the result string, and a timestamp.

The migration path for a future iteration would be: extract an interface from these methods, implement a real adapter (SQLite, PostgreSQL, etc.), and refactor `UserManager` to delegate to that adapter instead of doing file I/O directly.

---

## Data Persistence

User data is stored in `sgp_users.dat` in the project root. Each line is one user, with 17 fields separated by pipes:

```
email|username|password|tttGamesPlayed|tttWins|tttLosses|tttDraws|tttCurrentStreak|tttBestStreak|tttPoints|c4GamesPlayed|c4Wins|c4Losses|c4Draws|c4CurrentStreak|c4BestStreak|c4Points
```

On first run, the platform creates a default admin account (`admin@sgp.com` / `admin` / `admin`) and 17 sample users with pre-populated stats for testing and demo purposes. These sample users have email addresses like `muhammad.ahmad@sgp.com`, `olivia.park@sgp.com`, etc.

File writes use `RandomAccessFile` with `FileChannel.lock()` for exclusive locking, so if two instances of the platform are running simultaneously, their writes will not corrupt the file.

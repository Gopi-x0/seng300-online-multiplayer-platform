# Client / UI Layer Documentation
### SENG300 pg11 - Student Gaming Platform (SGP)
### Sub-team: UI (Client Layer)

---

## Purpose of This Document

This doc is for anyone who needs to connect the backend, platform core, or database to the UI layer - or for the TA reviewing what we built and what still needs wiring. It covers every screen we have, what each method does, what data it expects, what it currently stubs out, and exactly where the connection points are.

If you are working on PlatformCore, the database stub, or the matchmaking logic, the "TODO / Connection Points" sections in each class are the parts you care about most.

---

## Project Package

All UI classes live in:
```
ca.ucalgary.seng300.screens
```

Main entry point:
```
ca.ucalgary.seng300.MainApp
```

Logo resource (required by several screens):
```
src/main/resources/ca/ucalgary/seng300/images/sgp-logo.png
```

---

## UML Rule (from Diagram 04 in docs/02 structure)

The entire UI layer follows this rule:
- UI subscribes to PlatformCore notifications
- UI calls PlatformCore APIs
- UI does NOT call GameEngine directly

Right now, all PlatformCore calls are stubbed with `System.out.println`. The structure is already in place - the TODOs just need the real calls swapped in.

---

## Screen Index

| Class                | File                      | Extends     | Status         |
|----------------------|---------------------------|-------------|----------------|
| AuthenticationScreen | AuthenticationScreen.java | StackPane   | Done (P2)      |
| HomePage             | HomePage.java             | BorderPane  | Done (P2)      |
| WinScreen            | WinScreen.java            | StackPane   | Done (P2)      |
| ProfileDashboard     | ProfileDashboard.java     | VBox        | Done (P2)      |
| SettingsScreen       | SettingsScreen.java       | ScrollPane  | Done (P2)      |
| InstructionsTTT      | InstructionsTTT.java      | Application | Done (P2)      |
| InstructionsC4       | InstructionsC4.java       | Application | Done (P2)      |
| GameboardC4          | GameboardC4.java          | Application | Done (P2)      |
| GameboardTTT         | GameboardTTT.java         | Application | Done (P2)      |
| ChatboxGUI           | ChatboxGUI.java           | -           | Planned (P3)   |
| WelcomePage          | WelcomePage.java          | StackPane   | Done (P2)      |
| MainGameDashboard    | MainGameDashboard.java    | BorderPane  | Done (P2)      |

---

## AuthenticationScreen

**File:** `AuthenticationScreen.java`
**Author:** Muhammad Ahmad
**Extends:** `StackPane`
**UML Diagram:** Diagram 04, right branch

This is the entry point for users after the welcome screen. It shows Register and Login side by side in one view.

### UML Contract

```
+ displayLoginScreen(): void
+ submitLogin(): void
+ displayRegistrationScreen(): void
```

Note: Per TA feedback, `displayRegistrationScreen()` was given its own submit action even though it was not explicitly shown in the original UML. The TA confirmed this is correct - a registration flow must logically include submission.

### Layout

- SGP logo centered at top
- Two `VBox` cards in an `HBox`: Register (left), Login (right)
- Each card has: email `TextField`, password `PasswordField` with eye toggle, "Forgot?" label, submit `Button`
- Password show/hide uses a `StackPane` with a `PasswordField` and a `TextField` swapped via `setManaged()` + `setVisible()`

### Methods

**`displayLoginScreen()`**
- Currently: prints stub message
- TODO: called when the login panel becomes active; in future will trigger PlatformCore auth check

**`submitLogin()`**
- Triggered by: "Submit Login" button `setOnAction`
- Currently: prints stub + email value to console
- TODO: pass `emailField.getText()` and `passField.getText()` to PlatformCore authentication; on success navigate to `HomePage`; on failure surface error state from `State` enum (`UsernameTaken`, `EmailFormatWrong`, `VerificationCodeWrong`)

**`displayRegistrationScreen()`**
- Triggered by: "Submit Registration" button `setOnAction`
- Currently: prints stub + email value to console
- TODO: pass email + password to PlatformCore registration; handle `State` enum responses

### Connection Points for Backend

```java
// In submitLogin():
// Replace println with:
PlatformCore.login(emailField.getText(), passField.getText())
// returns HomePage on success, or State enum value on failure

// In displayRegistrationScreen():
// Replace println with:
PlatformCore.register(usernameField.getText(), passField.getText(), emailField.getText())
// returns State enum value
```

### Navigation

- On login success: navigate to `HomePage`
- On register success: navigate to `HomePage` (or back to login, TBD)
- Back navigation: return to `WelcomePage`

### Stub Data

None - all fields are live inputs.

---

## HomePage

**File:** `HomePage.java`
**Author:** Muhammad Ahmad
**Extends:** `BorderPane`
**UML Diagram:** Diagram 04, center branch

Main hub shown after login. Has game cards, record buttons, and a match history table.

### UML Contract

```
+ viewYourOwnRecords(): void
+ viewOtherPlayerRecords(): void
+ viewSettings(): void
+ chooseGame(): void
```

Note: `viewSettings()` returns `void` in the actual implementation (stub prints to console and navigates). The UML specifies `Settings` as the return type but the current stub returns nothing - this will be resolved when navigation is wired in P3.

Per TA feedback, `displayGames()` was also added because users must see games before selecting one. This matches the pattern in `MainGameDashboard`.

### Layout

- Top bar: SGP logo (left), "Settings" label link (right)
- Two game cards side by side: Tic Tac Toe, Connect 4
  - Each card: mini board preview (3x3 TTT grid with X/O labels, 4x5 C4 grid with colored circles), title label, subtitle label, "PLAY NOW" button
- Below cards: "View Self Records" button, "View Player Records" button (with dropdown indicator)
- RECORD section: `TableView` with columns KD, PLAYER, ACS, HEADSHOT COUNT, GAME
  - Table uses custom row factory and `Platform.runLater` to style internals (header background, scrollbar, cell colors)
  - Fixed cell size of 34px, fixed height calculated as `28 + (34 * 6)` to show exactly 6 rows
  - ACS column cells are styled in accent blue (`#00c8ff`), all others in white
  - Table uses `CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN`

### Methods

**`displayGames()`**
- Currently: println stub
- TODO: fetch available game list from PlatformCore; populate game cards dynamically

**`chooseGame()`**
- Triggered by: PLAY NOW button on either game card
- Currently: println stub
- TODO: call `PlatformCore.joinQueue(gameType)` and navigate to `PlayGame`

**`viewYourOwnRecords()`**
- Triggered by: "View Self Records" button
- Currently: println stub
- TODO: fetch current player's match history from PlatformCore; populate the RECORD `TableView`

**`viewOtherPlayerRecords()`**
- Triggered by: "View Player Records" button
- Currently: println stub
- TODO: open player search or leaderboard view; fetch other player data from PlatformCore

**`viewSettings()`**
- Triggered by: "Settings" label click (`setOnMouseClicked`)
- Currently: println stub
- TODO: navigate scene to `SettingsScreen`

### Connection Points for Backend

```java
// In chooseGame():
PlatformCore.joinQueue(gameType); // "TicTacToe" or "Connect4"
// then navigate to PlayGame screen

// In viewYourOwnRecords():
List<MatchRecord> records = PlatformCore.getPlayerHistory(playerId);
// populate RecordRow TableView items from records

// In viewOtherPlayerRecords():
List<PlayerProfile> players = PlatformCore.searchPlayers(query);
```

### TableView Columns

| Column | Data Type | Notes |
|---|---|---|
| KD | double | Kill/death ratio or win/loss ratio - stub metric |
| PLAYER | String | Player username |
| ACS | int | Average combat score - stub metric, rendered in accent blue |
| HEADSHOT COUNT | int | Stub metric |
| GAME | String | "Tic Tac Toe" or "Connect 4" |

Note: Column headers (KD, ACS, HEADSHOT COUNT) are temporary placeholder names. These will be renamed to reflect TTT and C4 specific stats (e.g. Win Rate, Wins, Losses) once the backend data model is defined.

### Stub Data

The RECORD table is currently hardcoded with 8 rows:
```java
new RecordRow(1.82, "Muhammad Ahmad", 2850, 47, "Tic Tac Toe"),
new RecordRow(1.36, "Haris Naveed",   2410, 39, "Connect 4"),
new RecordRow(1.58, "Bilal Ahmed",    2675, 44, "Tic Tac Toe"),
new RecordRow(1.21, "Shayan Shaikh",  2290, 36, "Connect 4"),
new RecordRow(1.47, "Haris Naveed",   2550, 41, "Tic Tac Toe"),
new RecordRow(1.69, "Bilal Ahmed",    2780, 45, "Connect 4"),
new RecordRow(1.33, "Shayan Shaikh",  2380, 38, "Tic Tac Toe"),
new RecordRow(1.75, "Muhammad Ahmad", 2905, 49, "Connect 4")
```
Replace these with a real PlatformCore query in P3.

The mini board previews (TTT grid, C4 circles) are hardcoded visual placeholders. They do not represent live game state.

---

## WinScreen

**File:** `WinScreen.java`
**Author:** Muhammad Ahmad
**Extends:** `StackPane`
**UML Diagram:** Diagram 04 - called from `PlayGame.displayWinner()`

Universal win/lose overlay. Works for both TicTacToe and Connect 4. Does not depend on the actual game board - it uses its own hardcoded background previews.

### UML Contract

```
+ displayWinner(): void
```

This is triggered from `PlayGame.displayWinner()` per the UML.

### Architecture

Three-layer StackPane:
- Layer 0: Enlarged hardcoded game board preview (`GridPane`) - has `GaussianBlur(12)` applied to it
- Layer 1: Semi-transparent dark scrim (`Pane`) - `rgba(0,0,0,0.58)`
- Layer 2: Centered result card (`VBox`) - never blurred, sits above everything

The blur is applied only to Layer 0. The result card is a sibling node, not a child, so it is unaffected by the blur.

### GameType Enum

```java
public enum GameType { TTT, C4 }
```

Pass `GameType.TTT` for a Tic-Tac-Toe background, `GameType.C4` for a Connect 4 background.

### Static Factory Methods (how game boards call it)

```java
// Show win overlay (defaults to TTT background)
WinScreen.showWin(parentStackPane, this::showHomePage);

// Show win overlay with explicit game type
WinScreen.showWin(parentStackPane, this::showHomePage, WinScreen.GameType.C4);

// Show lose overlay
WinScreen.showLose(parentStackPane, this::showHomePage);
WinScreen.showLose(parentStackPane, this::showHomePage, WinScreen.GameType.TTT);

// Generic method (no navigation callback)
WinScreen.show(parentStackPane, true, WinScreen.GameType.C4);
```

### Parameters

| Parameter | Type | Description |
|---|---|---|
| parentPane | StackPane | The scene root the overlay is added to |
| onDismiss | Runnable | Navigation callback fired when RETURN is pressed (e.g. `this::showHomePage`) |
| gameType | GameType | TTT or C4 - determines background image |
| isWin | boolean | true = "You Win!" + gold crown, false = "You Lose!" + red X |

### How to Integrate from a Game Board

The game board's root must be a `StackPane`. Both `GameboardC4` and `GameboardTTT` already use `StackPane root`. Example inside a game board when win is detected:

```java
// With back navigation callback:
WinScreen.showWin(root, () -> {
    // navigate back to home or wherever
}, WinScreen.GameType.TTT);
```

### displayWinner()

- Called after adding the overlay to the parent
- Plays a `FadeTransition` (300ms) on the whole overlay from opacity 0 to 1
- The result card also has a `ScaleTransition` pop-in (300ms, from 0.78x to 1.0x)

### Dismiss / RETURN button

- Fades overlay out (220ms)
- Removes itself from the parent `Pane`'s children via instance check `getParent() instanceof Pane`
- Fires the `onDismiss` Runnable if one was provided
- Currently: also prints stub message to console
- TODO: wire actual scene navigation to `HomePage` if no callback is passed

### Result Card Dimensions

Card is fixed size: `minWidth/maxWidth = 300`, `minHeight/maxHeight = 400`. Fixed size prevents the "You Lose!" two-line text from being clipped.

---

## ProfileDashboard

**File:** `ProfileDashboard.java`
**Author:** Shayan Shaikh
**Extends:** `VBox`
**UML Diagram:** Diagram 04 - navigated to from `MainGameDashboard.viewProfile()`

Shows player profile with overall stats and per-game breakdown.

### UML Contract

```
+ displayProfileView(): void
+ displayGameStats(type): void
```

Both methods are currently stubs. The visual layout is fully built.

### Layout

- Top bar: "Back to Home" button (left), "Profile" button (right) - both have hover/pressed states
- Profile header card: player name, "Member since" label, rank (gold), points (cyan)
- 4 stat cards in a row: Total Games, Total Wins (green value), Win Rate, Best Streak
- 2 per-game cards side by side: Tic Tac Toe, Connect 4
  - Each game card has: title, Games Played/Wins/Losses/Draws in a left-label right-value layout, Win Rate `ProgressBar` (cyan accent), Current Streak/Best Streak/Points Earned in same layout

### Methods

**`displayProfileView()`**
- Currently: empty stub
- TODO: populate all labels with real player data from PlatformCore

**`displayGameStats(String type)`**
- Parameter: game type string (e.g. "TicTacToe", "Connect4")
- Currently: empty stub
- TODO: fetch game-specific stats from PlatformCore and update the relevant game card

### Connection Points for Backend

```java
// In displayProfileView():
PlayerProfile profile = PlatformCore.getProfile(playerId);
name.setText(profile.getName());
rank.setText("Rank #" + profile.getRank());
points.setText(profile.getPoints() + " Points");
// etc.

// In displayGameStats(type):
GameStats stats = PlatformCore.getGameStats(playerId, type);
// Update the matching game card labels with stats.getWins(), stats.getLosses(), etc.
```

### Stub Data

All displayed values are currently hardcoded placeholders:
- Name: "Name Name"
- Rank: "Rank #"
- Points: "0000 Points"
- Member since: "Month Year"
- All game stats: 0 or default values
- Win rate progress bar: hardcoded to 0% for TTT, 10% for C4

### createGameCard() Parameters

```java
private VBox createGameCard(
    String title,         // "Tic Tac Toe" or "Connect 4"
    int games,            // total games played
    int wins,
    int losses,
    int draws,
    String winRate,       // e.g. "61.5%" - parsed into ProgressBar value (divided by 100)
    String currentStreak,
    String bestStreak,
    String points
)
```

When backend is connected, pass real values here instead of zeros.

---

## SettingsScreen

**File:** `SettingsScreen.java`
**Author:** Shayan Shaikh
**Extends:** `ScrollPane`
**UML Diagram:** Diagram 04 - `Settings` class

### UML Contract

```
+ changeUsername(newUsername: String): boolean
+ changeEmail(newEmail: String): boolean
+ changePassword(oldPassword: String, newPassword: String): boolean
+ logout(): boolean
```

Currently the screen has the full UI built but all button actions print to console instead of calling the UML methods.

### Layout

- Scrollable page (ScrollPane wrapping VBox)
- Back to Home button at top left with hover/pressed styles
- "Settings" title
- Four cards: Change Username, Change Email, Change Password, Logout
- Logout button is red (`#e7000b`) with hover/press states

### Connection Points for Backend

```java
// Change Username card - "Save Username" button:
boolean result = PlatformCore.changeUsername(usernameField.getText());

// Change Email card - "Save Email" button:
boolean result = PlatformCore.changeEmail(emailField.getText());

// Change Password card - "Change Password" button:
// Already has basic client-side validation (checks newPass == confirm)
boolean result = PlatformCore.changePassword(current.getText(), newPass.getText());

// Logout card - "Logout" button:
boolean result = PlatformCore.logout();
// then navigate back to AuthenticationScreen / WelcomePage
```

### Notes

- Password card does local validation (new password must match confirm password) before any backend call
- All four UML methods should return boolean - true on success, false on failure
- The "Back to Home" button currently only prints to console; needs navigation wired to `HomePage`

---

## InstructionsTTT

**File:** `InstructionsTTT.java`
**Author:** Bilal Ahmed
**Extends:** `Application`
**UML Diagram:** Diagram 04 - called from `PlayGame.displayInstructions()`

### Layout

- Title: "Tic-Tac-Toe", subtitle: "Instructions & Rules"
- Three info cards: Rules, Objective, Play
- Each card built via reusable `createCard(String title, String... lines)` method
- PLAY button at bottom with hover style (gradient cyan/blue)
- Scene size: 1000x1000

### Connection Points

The PLAY button currently has no `setOnAction`. It needs to be wired to navigate to `GameboardTTT` when clicked. This is a P3 integration task.

```java
// PLAY button action (currently missing):
playButton.setOnAction(e -> {
    // navigate to GameboardTTT
});
```

### Notes

- The class extends `Application` which means it launches its own `Stage`. In the integrated platform, this should be refactored to extend a layout class (like `VBox`) so it can be embedded in the main scene rather than opening a new window. This is noted as a P3 task.

---

## InstructionsC4

**File:** `InstructionsC4.java`
**Author:** Bilal Ahmed
**Extends:** `Application`
**UML Diagram:** Diagram 04 - called from `PlayGame.displayInstructions()`

Identical structure to `InstructionsTTT` but for Connect 4. Has a `public static void main()` method (unlike `InstructionsTTT` which does not).

### Connection Points

Same as `InstructionsTTT` - PLAY button needs wiring to `GameboardC4`:

```java
playButton.setOnAction(e -> {
    // navigate to GameboardC4
});
```

### Notes

Same refactor note as `InstructionsTTT` - extending `Application` should be changed to a layout class for platform integration.

---

## GameboardC4

**File:** `GameboardC4.java`
**Author:** Bilal Ahmed
**Extends:** `Application`
**UML Diagram:** Diagram 04 - `GameGUI` class (`setupUIWindow`, `renderBoard`, `updateUI`)

The visual Connect 4 game board. Builds the grid, left/right player streak indicators, and title.

### UML Contract (GameGUI)

```
+ setupUIWindow(): void
+ renderBoard(): void
+ updateUI(): void
```

These methods are not yet explicitly declared - the `start()` method currently does the setup work inline. In P3 these should be refactored into named methods matching the UML.

### Key Fields

```java
private int ROWS = 6;
private int COLS = 7;
private int CELLSPACING = 75;
private int CIRCLERADIUS = 60;
private StackPane[][] cells = new StackPane[ROWS][COLS]; // visual grid mapped to logic
private int CAPACITY = ROWS * COLS;
private int CONSECUTIVE = 4; // number in a row needed to win
```

### Layout

- Title "Connect 4" and subtitle "Your turn" in a `VBox` at top
- Game grid is a `VBox` of `HBox` rows, each cell is a `StackPane` containing a `Circle`
- Left and right side panels show the player's current streak tiles (`createPlayerCurrentTiles`)
- Root is a `StackPane` wrapping a `BorderPane` - dynamic resizing listeners on stage width/height
- Grid background is `Color.BLUE` with `CornerRadii(15)` and padding equal to `CELLSPACING`
- Scene size: 800x800

### Grid Mapping

`cells[row][col]` is a `StackPane` containing a `Circle` filled with `Color.GHOSTWHITE` by default. To update a cell when a player drops a disc:

```java
// Get the circle inside a cell and change its color:
Circle disc = (Circle) cells[row][col].getChildren().get(0);
disc.setFill(Color.RED); // or YELLOW for player 2
```

### Connection Points for Backend / Rules Team

```java
// When a player clicks a column (drop disc):
// 1. Validate the move with the Rules/Validation sub-team
// 2. Update cells[row][col] fill color
// 3. Call PlatformCore.submitMove(sessionId, move)
// 4. Check win condition - if won: WinScreen.showWin(root, callback, WinScreen.GameType.C4)

// To receive opponent move updates from PlatformCore:
// PlatformCore.notifyPlayer() should trigger updateUI()
// which updates the relevant cell color
```

### Notes

- Click handlers on columns are not yet implemented - this is the main P3 task for this class
- The `main` method uses `static void main` (not `public static void main`) - package-private
- Extending `Application` has the same refactor note as the Instructions classes

---

## GameboardTTT

**File:** `GameboardTTT.java`
**Author:** Bilal Ahmed
**Extends:** `Application`
**UML Diagram:** Diagram 04 - `GameGUI` class (`setupUIWindow`, `renderBoard`, `updateUI`)

The visual Tic-Tac-Toe game board. Builds the 3x3 grid and left/right player streak indicators for X and O.

### UML Contract (GameGUI)

```
+ setupUIWindow(): void
+ renderBoard(): void
+ updateUI(): void
```

Same as `GameboardC4` - these methods are not yet explicitly declared. The `start()` method does all setup inline. In P3 these should be refactored into named methods matching the UML.

### Key Fields

```java
private int ROWS = 3;
private int COLS = 3;
private int CELLS = ROWS * COLS;
private int CIRCLERADIUS = 60;
private StackPane[][] board = new StackPane[ROWS][COLS]; // visual grid mapped to logic
```

### Layout

- Title "Tic-Tac-Toe" and subtitle "Your turn" in a `VBox` at top
- Main layout is a `BorderPane` with a cyan/blue gradient background (`linear-gradient(#00d3f3, #0088a3)`)
- Grid is a `GridPane` centered, max size 600x600, with `ColumnConstraints` and `RowConstraints` at 33.33% each for dynamic resizing
- Each cell is a `StackPane` with white background, preferred size 150x150
- Left side shows X streak (`createPlayerCurrentStreakX`) - each X is built as two `Line` objects in a `StackPane`
- Right side shows O streak (`createPlayerCurrentStreakO`) - each O is a `Circle` with `Color.GREY`
- Root is a `StackPane` wrapping the `BorderPane` - dynamic resizing listeners on stage width/height

### Grid Mapping

`board[row][col]` is a `StackPane` with a white background. To place a symbol when a player clicks:

```java
// Place an X or O label in a cell:
Label symbol = new Label("X"); // or "O"
symbol.setStyle("-fx-font-size: 60px; -fx-font-weight: bold;");
board[row][col].getChildren().add(symbol);
```

### Connection Points for Backend / Rules Team

```java
// When a player clicks a cell:
// 1. Validate the move with the Rules/Validation sub-team
// 2. Add X or O label to board[row][col]
// 3. Call PlatformCore.submitMove(sessionId, move)
// 4. Check win condition - if won: WinScreen.showWin(root, callback, WinScreen.GameType.TTT)

// To receive opponent move updates from PlatformCore:
// PlatformCore.notifyPlayer() should trigger updateUI()
// which adds the opponent's symbol to the correct cell
```

### Notes

- Click handlers on cells are not yet implemented - main P3 task for this class
- No `main` method present in the current file - class is launched via `MainApp` or `InstructionsTTT` PLAY button
- Extending `Application` has the same refactor note as the other game classes
- There is a bug in the current code: `title.setStyle(...)` is called twice in `start()`, overwriting the first call. The `subTitle` label never gets its style applied. This is a P3 fix.

---

## ChatboxGUI (Planned - P3)

**File:** `ChatboxGUI.java` - not yet implemented
**Author:** Shayan Shaikh
**UML Diagram:** Diagram 04 - `ChatboxGUI` class

### UML Contract

```
+ displayMessages(): void
+ displayChatbox(): void
```

### Design Reference

From the provided mockup (Shayan's Figma design), the chatbox screen includes:
- "Back to Home" link top-left
- Chat header: "SGP Gaming Assistant" with green "Online" indicator
- Message history scroll area - user messages on right (blue), assistant messages on left (dark card)
- Text input field at bottom with send button
- Quick-action chips below input: "What are my stats?", "Give me Connect 4 tips", "Show leaderboard rankings"

### Planned Connection Points

```java
// displayMessages():
// Render the chat history from a List<ChatMessage>
// Each message has: sender (user/assistant), content, timestamp

// displayChatbox():
// Initialize the chat UI
// Wire send button to submit user message
// TODO: connect to AI/chatbot backend or PlatformCore chat service
```

This screen is left for P3. The design is finalized.

---

## WelcomePage

**File:** `WelcomePage.java`
**Author:** Haris Naveed
**Extends:** `StackPane`
**UML Diagram:** Diagram 04 - `AuthenticationScreen --> WelcomePage`

### UML Contract

```
+ displayMessage(msg: String): String
```

### Layout

- Dark background `#081221`
- Centered `VBox` that binds its max width to 60% of the `StackPane` width for responsive sizing
- SGP logo (`ImageView`, fit width 500px) overlapping with title text via negative spacing (`VBox(-28, logo, textGroup)`)
- Title: "WELCOME TO" in `#86b9ff`, font Rajdhani Bold (falls back to Trebuchet MS) at size 30
- Subtitle: "Student Gaming Platform" in `#d8e7ff`, font Inter Medium (falls back to Verdana) at size 14
- Login and Sign Up buttons side by side in an `HBox` - each 140px wide, 40px tall
- Login button: solid blue gradient (`#1d4ed8` to `#2563eb`)
- Sign Up button: transparent with border (`#4b6b97`), fills on hover
- Both buttons use `applyHoverStyle()` helper - only hover states, no press state defined
- Flexible top/bottom spacers (`VBox.setVgrow(Priority.ALWAYS)`) keep content vertically centered

### Connection Points

Login and Sign Up buttons currently have empty `setOnAction` handlers. These need to navigate to `AuthenticationScreen`:

```java
loginButton.setOnAction(event -> {
    // navigate to AuthenticationScreen, show login panel
});
signUpButton.setOnAction(event -> {
    // navigate to AuthenticationScreen, show register panel
});
```

`displayMessage(msg)` is not yet implemented - it is defined in the UML and should be used to display platform-level messages (e.g. "Server maintenance in 10 minutes") on the welcome screen.

---

## MainGameDashboard

**File:** `MainGameDashboard.java`
**Author:** Haris Naveed
**Extends:** `BorderPane`
**UML Diagram:** Diagram 04 - communicates with PlatformCore via `joinQueue()/start/join`

### UML Contract

```
+ displayGames(): void
+ chooseGame(): void
+ viewProfile(): void
+ showLeaderboard(): void
```

None of these are declared as explicit public methods yet - all logic is currently inline in the layout methods. They need to be extracted in P3 when navigation is wired.

### Layout

- Background: vertical gradient `#0c1d31` to `#050c17`
- Centered `VBox` (max width 1120px) with its own dark semi-transparent background
- Header row: SGP logo (left, fit width 210px), "Profile" label link (right, cyan `#3ed3ff`, rounded pill style)
- Two game cards side by side (max combined width 740px), each preferred 355px wide:
  - Mini board preview at top (TTT: 3x3 GridPane with X/O labels; C4: 4-row x 7-col GridPane with Circle tokens)
  - Game title + subtitle text block
  - "PLAY NOW" button (thin outline style, `#66dbff` text, `#2eaed8` border)
- Leaderboard `TableView` (max width 980px) with columns RANK, PLAYER, POINTS, SCORE, GAME

### C4 Preview in MainGameDashboard

The Connect 4 preview in this class uses a 4-row x 7-column grid (full-width Connect 4 proportions), whereas `HomePage` uses a 4-row x 5-column grid. Both are visual placeholders only.

### Connection Points

```java
// PLAY NOW buttons (currently empty setOnAction):
// PlatformCore.joinQueue(gameType);
// navigate to PlayGame

// Profile link (currently non-clickable Label):
// navigate to ProfileDashboard

// Leaderboard table:
// Currently hardcoded with 8 stub rows
// TODO: fetch from PlatformCore.getLeaderboard()
```

### Stub Data in Leaderboard

8 rows hardcoded:
```java
new LeaderboardRow(1, "Bilal Ahmed",   2780, 47, "Tic Tac Toe"),
new LeaderboardRow(2, "Shayan Sheikh", 2680, 42, "Connect 4"),
new LeaderboardRow(3, "Muhammad Ahmad", 2510, 39, "Tic Tac Toe"),
new LeaderboardRow(4, "npc",           2340, 35, "Connect 4"),
new LeaderboardRow(5, "npc",           2190, 31, "Tic Tac Toe"),
new LeaderboardRow(6, "npc",           2050, 28, "Connect 4"),
new LeaderboardRow(7, "npc",           1830, 25, "Tic Tac Toe"),
new LeaderboardRow(8, "haris Naveed",  1792, 23, "Connect 4")
```
Replace with `PlatformCore.getLeaderboard()` in P3.

### LeaderboardRow Data Model

```java
// Internal static class - fields:
SimpleIntegerProperty rank
SimpleStringProperty player
SimpleIntegerProperty points
SimpleIntegerProperty score
SimpleStringProperty game
```

All property getters are package-private (no `public` modifier).

---

## MainApp (Dev Entry Point)

**File:** `MainApp.java` - in `ca.ucalgary.seng300` (not the screens package)

This is the main `Application` class. Currently functions as a navigation test harness with a toolbar for jumping between screens. In the final product, the toolbar will be removed and navigation will be driven by button actions inside each screen.

### Current Toolbar Buttons

| Button | Action |
|---|---|
| Welcome | Shows `WelcomePage` |
| Authentication | Shows `AuthenticationScreen` |
| Home | Shows `HomePage` |
| Dashboard | Shows `MainGameDashboard` |
| Profile | Shows `ProfileDashboard` |
| Settings | Shows `SettingsScreen` |
| Instructions TTT | Shows `InstructionsTTT` embedded |
| Instructions C4 | Shows `InstructionsC4` embedded |
| Win (TTT) | Shows `WinScreen` win overlay with TTT background |
| Lose (TTT) | Shows `WinScreen` lose overlay with TTT background |
| Win (C4) | Shows `WinScreen` win overlay with C4 background |
| Lose (C4) | Shows `WinScreen` lose overlay with C4 background |
| Launch TTT | Opens `GameboardTTT` in a separate window |
| Launch C4 | Opens `GameboardC4` in a separate window |

### Navigation Flow (intended for P3)

```
WelcomePage
  -> AuthenticationScreen (login/register)
    -> HomePage (after login)
      -> MainGameDashboard (choose game)
        -> InstructionsTTT / InstructionsC4
          -> GameboardTTT / GameboardC4
            -> WinScreen (on game end)
              -> HomePage (on RETURN)
      -> ProfileDashboard (via profile link)
      -> SettingsScreen (via settings link)
      -> ChatboxGUI (via chat button - P3)
```

---

## Summary of All TODOs

| Screen | Method / Item | What needs to happen |
|---|---|---|
| AuthenticationScreen | `submitLogin()` | Call PlatformCore auth; navigate to HomePage on success; surface State enum errors |
| AuthenticationScreen | `displayRegistrationScreen()` | Call PlatformCore register; handle State enum |
| HomePage | `chooseGame()` | Call `PlatformCore.joinQueue(gameType)`; navigate to PlayGame |
| HomePage | `viewYourOwnRecords()` | Fetch match history from PlatformCore; populate TableView |
| HomePage | `viewOtherPlayerRecords()` | Open player search; fetch from PlatformCore |
| HomePage | `viewSettings()` | Navigate to SettingsScreen |
| HomePage | Column headers | Rename KD/ACS/HEADSHOT COUNT to game-relevant stats once data model is defined |
| ProfileDashboard | `displayProfileView()` | Populate all labels from PlatformCore player data |
| ProfileDashboard | `displayGameStats(type)` | Fetch per-game stats from PlatformCore |
| SettingsScreen | all 4 buttons | Call PlatformCore changeUsername/Email/Password/logout |
| SettingsScreen | Back button | Navigate to HomePage |
| WinScreen | `dismiss()` | Navigation callback already wired via Runnable - just pass correct callback |
| MainGameDashboard | PLAY NOW buttons | Wire to `PlatformCore.joinQueue()`; extract as `chooseGame()` method |
| MainGameDashboard | Profile link | Navigate to ProfileDashboard; extract as `viewProfile()` method |
| MainGameDashboard | Leaderboard table | Replace stub data with `PlatformCore.getLeaderboard()`; extract as `showLeaderboard()` method |
| GameboardC4 | Column click handlers | Add click handlers; validate move; call `PlatformCore.submitMove()` |
| GameboardC4 | Win detection | Call `WinScreen.showWin(root, callback, GameType.C4)` |
| GameboardTTT | Cell click handlers | Add click handlers; validate move; call `PlatformCore.submitMove()` |
| GameboardTTT | Win detection | Call `WinScreen.showWin(root, callback, GameType.TTT)` |
| GameboardTTT | subTitle label bug | `title.setStyle(...)` called twice in `start()` - subTitle never gets styled; fix in P3 |
| InstructionsTTT | PLAY button | Wire to navigate to GameboardTTT |
| InstructionsC4 | PLAY button | Wire to navigate to GameboardC4 |
| WelcomePage | Login/SignUp buttons | Wire to AuthenticationScreen |
| WelcomePage | `displayMessage()` | Implement to show platform-level messages on screen |
| ChatboxGUI | entire class | Build in P3 - see design reference above |

---

## Notes for PlatformCore Team

- All screens expect a service class accessible as `PlatformCore` (or equivalent) in the same package or via import
- The UML specifies three methods on PlatformCore that the UI calls: `notifyPlayer()`, `submitMove()`, `joinQueue()`
- The UI also expects to query player data, match history, and leaderboard - these are not in the current UML stub but will be needed
- Navigation between screens is currently handled in `MainApp`. In the integrated system, each screen should be able to trigger navigation itself - either via a shared `SceneManager` utility or by passing callbacks (the `WinScreen` Runnable pattern is a good model for this)
- The `State` enum (`UsernameTaken`, `EmailFormatWrong`, `VerificationCodeWrong`) is defined in the UML and needs to be returned from registration/login calls so the UI can surface appropriate error messages to the user
- `GameboardTTT` and `GameboardC4` both use `StackPane root` as their scene root - this is the node you pass to `WinScreen.showWin/showLose` when triggering the end-game overlay

---

*Last updated: Iteration 2 - March 2026*
*UI Sub-team: Muhammad Ahmad, Shayan Shaikh, Haris Naveed, Bilal Ahmed*
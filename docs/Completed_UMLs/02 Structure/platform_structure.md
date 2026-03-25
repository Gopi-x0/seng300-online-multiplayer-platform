# Platform Core & Services

## Description:

GameEngine:
- Rulebook that handles the match and logic of the game 
- createInitialState(): Generates the board of the game
- validateMove(state, movePayload): Checks if a move is valid
- applyMove(state, movePayload):  If a move is valid, a turn is used
- evaluateEnd(state): Checks if a move ends the game (Win, Ongoing, or Draw)

GameManager:
- Manages the flow of the match
- startGame(gameType, p1, p2): Clicks “start game” and starts by prompting for a gametype and stores player profiles
- submitMove(playerId, movePayload): Checks for a valid move, applies it, and either checks for a win or switches the turn. Sends it to MoveResult when it’s done
- switchActivePlayer(): If a player is still ongoing, it switches to the next player


AuthManager:
- Identifies the people playing the games
- login(credentials): Username and password for player and stores them in player profiles
- getActivePlayers(): Returns the players that are currently playing

PlayerProfile:
- Stores player information
- playerId(): ID that separates players from one another 
- username(): in-game name that Identifies player 

LeaderboardManager:
- Leaderboard that tracks a player's data when they play
- recordMatchResult(gameType, winner, loser, isDraw): Checks how a match ends and records the players' win/loss/draw record
- getTopPlayers(gameType): Displays the top ranked players on the leaderboard screen


Database (Stub):
- Simulates database for match data and leaderboards
- saveMatchRecord(record): Stores match data
- fetchLeaderboard(gameType): reads and sorts the win/loss/draw records of players on the leaderboard

NetworkHosting (Stub):
- Simulates connecting to a matchmaking server or cloud host
- connectToServer(): Displays message that says connection to server is established
- broadcastStateUpdate(GameState state): Broadcasts message of game state change and displays sessionID
- disconnect(): Displays message that drops connection to server when app is closed




# PlantUML Code:

``` 
@startuml
title Platform Structure (Tic-Tac-Toe & Connect 4)
hide empty members
skinparam classAttributeIconSize 0
skinparam linetype ortho

note "Architecture rule:\nStrictly Local Multiplayer.\nNo network matchmaking or party systems.\nDirect method returns instead of EventBus." as N_RULE

enum GameStatus {
  SETUP
  ONGOING
  WIN
  DRAW
}

class PlayerProfile {
  + playerId: String
  + username: String
}

class GameState {
  + gameType: String
  + status: GameStatus
  + activePlayerIndex: int
  + boardPayload: Object
}

class MoveResult {
  + success: boolean
  + reason: String
  + updatedState: GameState
}

interface GameEngine <<interface>> {
  + createInitialState(): GameState
  + validateMove(state: GameState, movePayload: Object): boolean
  + applyMove(state: GameState, movePayload: Object): GameState
  + evaluateEnd(state: GameState): GameStatus
}

class TicTacToeEngine {
  + validateMove(state, movePayload): boolean
  + applyMove(state, movePayload): GameState
}

class Connect4Engine {
  + validateMove(state, movePayload): boolean
  + applyMove(state, movePayload): GameState
}

class GameManager <<service>> {
  - engine: GameEngine
  - currentState: GameState
  - players: List<PlayerProfile>
  + startGame(gameType: String, p1: PlayerProfile, p2: PlayerProfile): GameState
  + submitMove(playerId: String, movePayload: Object): MoveResult
  - switchActivePlayer(): void
}

class AuthManager <<service>> {
  - loggedInUsers: List<PlayerProfile>
  + login(credentials): PlayerProfile
  + getActivePlayers(): List<PlayerProfile>
}

class LeaderboardManager <<service>> {
  + recordMatchResult(gameType: String, winner: PlayerProfile, loser: PlayerProfile, isDraw: boolean): void
  + getTopPlayers(gameType: String): List<PlayerProfile>
}

class Database <<stub>> {
  + saveMatchRecord(record: Object): void
  + fetchLeaderboard(gameType: String): List<Object>
}

class NetworkHosting <<stub>> {
  + connectToServer(): boolean
  + broadcastStateUpdate(state: GameState): void
  + disconnect(): void
}


GameEngine <|.. TicTacToeEngine
GameEngine <|.. Connect4Engine

GameManager o-down- GameEngine
GameManager -right-> GameState : manages >
GameManager ..> MoveResult : returns >

LeaderboardManager -down-> Database : uses >
GameManager -left-> LeaderboardManager : triggers >


GameManager -up-> NetworkHosting : broadcasts state >

GameManager -[hidden]down-> AuthManager 

AuthManager -right-> PlayerProfile : manages >

note right of AuthManager
  Handles logging in Player 1 
  and Player 2 locally before a match begins.
end note

@enduml
```
# Platform State Machine

### Description:

1. Authentication:
- Starts logged out.
- The user types in their credentials and logs in.
- From the main menu, they can view the main dashboard and the leaderboards.


2. Game Setup 
- The user clicks "Play" and enter the setup phase
- First, they pick which game they want to play (Tic-Tac-Toe or Connect 4).
- Next, the prompt asks Player 2 to log in. Once both players are registered, the game is ready.

3. In-Game Loop 
- The match begins, defaulting to Player 1's Turn.
- If Player 1 clicks an invalid spot, the program rejects it and stays on Player 1's turn.
- If they make a valid move, the program immediately switches to Player 2's Turn.
- It switches between these two states until a valid move triggers a win or a draw


4. Post-Match 
- The moment the game ends, the program automatically transitions into SubmittingStats. This updates the database
- Then, it moves the players to the match results screen.
- From there, the players are given a choice: click "Play Again" to jump right back to the setup screen, or "Quit" to return to the main menu.


## PlantUML Code

```
@startuml
title Platform State Machine (Tic-Tac-Toe & Connect 4)

[*] --> LoggedOut

LoggedOut --> Authenticating : submitCredentials
Authenticating --> LoggedOut : authFail
Authenticating --> MainMenu : authSuccess

state "LoggedIn (MainMenu)" as MainMenu {
  [*] --> ViewingDashboard
  ViewingDashboard --> ViewingLeaderboard : click "Leaderboard"
  ViewingLeaderboard --> ViewingDashboard : back
}

MainMenu --> GameSetup : click "Play"

state GameSetup {
  [*] --> SelectingGameType : choose Tic-Tac-Toe / Connect 4
  SelectingGameType --> SelectingPlayer2 : prompt for Opponent
  SelectingPlayer2 --> GameReady : select log in Player 2
}

GameSetup --> MainMenu : click "Back"
GameReady --> InGame : click "Start Match"

state InGame {
  [*] --> Player1Turn : initialize board
  
  Player1Turn --> Player1Turn : submitMove + rejected (invalid)
  Player1Turn --> Player2Turn : submitMove + accepted [gameStatus == Ongoing]
  
  Player2Turn --> Player2Turn : submitMove + rejected (invalid)
  Player2Turn --> Player1Turn : submitMove + accepted [gameStatus == Ongoing]

  Player1Turn --> GameOver : submitMove + accepted [gameStatus == Win/Draw]
  Player2Turn --> GameOver : submitMove + accepted [gameStatus == Win/Draw]
}

GameOver --> SubmittingStats : automatic trigger
note right of SubmittingStats
  The app securely sends the match 
  result (Win/Loss/Draw) to the 
  database to update the leaderboards.
end note

SubmittingStats --> MatchResults : statsSaved (Success)
SubmittingStats --> MatchResults : statsFailed (Error)

MatchResults --> GameSetup : click "Play Again"
MatchResults --> MainMenu : click "Quit to Menu"

@enduml
 ```
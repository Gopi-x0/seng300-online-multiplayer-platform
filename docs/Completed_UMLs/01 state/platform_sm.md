# Platform State Machine

## Description:



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
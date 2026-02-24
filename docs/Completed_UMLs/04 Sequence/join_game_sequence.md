# Join matchmaking and start a session #

Description: From the dashboard, the player selects a game type of either tic-tac-toe or connect 4 and queues for matchmaking. The player is notified to confirm that the matchmaking service is finding a match. Once a match is found based on the player's stats for the given game type, the session manager creates a session. The GUI is told to open the Join Game page where the player can join the game.


```uml
@startuml
title Sequence - Queue -> Match Found -> Join Game Page

actor Player
participant "GUI" as GUI
participant "PlatformCore" as Core
participant "MatchmakingService" as MM
participant "GameSessionManager" as GSM
participant "GameServer (stub)" as Server

Player -> GUI : choose tic-tac-toe/connect 4\nclick Queue
GUI -> Core : joinQueue(playerId, gameType)
Core -> MM : enqueue(playerId, gameType)
MM -> GUI: showNotification("Queueing...")
GUI -> GUI: display banner/badge
GUI --> MM: showNotification(-)

... time passes ...

note right of MM
finds match using
player's stats
end note

MM -> MM : findMatch(playerId, gameType)
MM --> Core : matchFound(players, gameType)

Core -> GSM : startSession(players, gameType)
GSM -> Server : createSession(players, gameType)
Server --> GSM : sessionId

GSM --> Core : sessionStarted(sessionId)
Core --> GUI : openJoinGame(sessionId)

GUI -> GUI : show Join Game Page
@enduml
```
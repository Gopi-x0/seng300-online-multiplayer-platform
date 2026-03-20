# Turn Notification Sequence Diagram

Description: The turn notification sequence occurs even when a player is not actively in the game window. 
When Player 2 finishes a turn (must be validated by submit move sequence), Player 1 should get a notification that it is their turn.
This notification must display on whatever active GUI player 1 has open. The platform core should be used to create the alert, 
which can be delivered through a standard notification method. 

```uml
@startuml
title Sequence - Turn Notification in Lobby/Dashboard

actor Player as P1
actor "Other Player" as P2

participant "GUI (Lobby/Dashboard)" as LobbyGUI
participant "GUI (Game Page: Connect 4 or Tic-Tac-Toe)" as GameGUI
participant "PlatformCore" as Core
participant "Game Session Manager" as GSM
participant "Game Engine" as GE
participant "NotificationService" as Notify

' Other player makes a move
P2 -> GameGUI : imput move (click cell or column)
note right of P2
second player submits their turn, 
which causes the first player 
to recieve a notification
end note
GameGUI -> Core : submitMove(P2, movePayload)
note right of GameGUI
    playerID = P2
    
    movePayload:
    Tic-Tac-Toe: {row, col}
    Connect 4: {col}
end note
Core -> GSM : submitMove(P2, movePayload)
note right of GSM
Refer to Submit Move Sequence Diagram for move validation
Move Validation occurs before continuing to deliver turn notification
end note

GSM -> GE : publish(GameStateUpdate(activePlayerId=P1))
GE --> Core : deliver(GameStateUpdate)

Core -> Notify : createTurnAlert(P1, sessionId)
Notify --> LobbyGUI : showNotification("Your turn in session ...")
note right of LobbyGUI
Displays notification for player 1
end note
LobbyGUI -> LobbyGUI : display banner/badge

@enduml
```

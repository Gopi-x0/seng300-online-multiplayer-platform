The Participants:

Current Player: The human whose turn it is currently in the game

GUI: The frontend components (tic-tac-toe board or connect 4 grid) that the player interacts with

Game Manager: The controller that keeps track of the match and remembers whose turn it is

Game Engine: The rule book that manages which game is being played (tic-tac-toe or connect 4)


1. Inputting a move

The active player clicks on the screen, and the movePayload sends it to the Game Manager

2. Validating the move

The manager asks the engine if the move is valid or not, according to the ruleset of the game 

3. Move is invalid

If the move is invalid, the GUI shows an error message

4. Move is valid

-If the move is valid, the manager tells the engine to update the board. Also, if this move allows the player to continue playing, win or draw  

5. Checking turn

-The manager checks if the current player finished his turn so that player 2 can have his turn next

6. Updating the GUI

-The manager gets the data (whether the game is over, player 2’s turn, and updating the board) and sends it to the GUI to update the current situation


PlantUML Code:
@startuml
title Sequence Diagram - Submit move(Tic-Tac-Toe & Connect 4)

actor "Current Player" as Player
participant "GUI" as GUI
participant "Game Manager" as Manager
participant "Game Engine" as Engine

Player -> GUI : input move\n(click cell or column)
GUI -> Manager : submitMove(playerId, movePayload)
note right of GUI
  movePayload:
  Tic-Tac-Toe: {row, col}
  Connect 4: {col}
end note

Manager -> Engine : validateMove(state, movePayload)
Engine --> Manager : return boolean (isValid)

alt isValid == false
  Manager --> GUI : return moveResult(success=false, reason="Invalid Move")
  GUI -> GUI : display error text
else isValid == true
  Manager -> Engine : applyMove(state, movePayload)
  Engine --> Manager : return gameState (updatedState)
  
  Manager -> Engine : evaluateEnd(updatedState)
  Engine --> Manager : return gameStatus (Win/Draw/Ongoing)
  
  alt gameStatus == Ongoing
    Manager -> Manager : switchActivePlayer()
  end
  
  Manager --> GUI : return moveResult(success=true, updatedState, activePlayer, gameStatus)
  
  GUI -> GUI : render updated board
  
  alt gameStatus == Win/Draw 
    GUI -> GUI : show Game Over screen
  else gameStatus == Ongoing
    GUI -> GUI : prompt activePlayer for turn
  end
end

@enduml

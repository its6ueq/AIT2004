# ChessGame bot using Minimax

# Implemented

- Board state evaluation based on pieces weights, position of each chess piece on the chess board
- BlackChecked and whiteChecked tables to store the number of pieces corresponding to each color checking a square, greatly improving the speed of checking valid moves to avoid checking and ending the game
- Minimax search algorithm for best move/optional depth/ optimal time search
- Alpha-beta search tree pruning
- Automatically increase depth until reached maximum time

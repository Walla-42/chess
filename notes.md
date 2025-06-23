# My notes


# Switches
The switch can thow an exception directly after the ->
If you dont use a -> to yeild a result for your case, you must use this format with either a break or a yeild

switch (type) {
    case 0: 
        var output = String("This is the output for case 0");
        yeild output;
    case 1:
        System.out.println("This is for case 1");
        break;
}

# What is need for move calculations:
    - Each chessPiece will have its own move patters on what is allowed
        - Rook: can move from a relative position of (0,0) to {(x = 0,0<y<8) and (0<x<8,y = 0)} (Moves to any postion in a strait vertical or horizontal line)
        - Bishop: Can move from a relative position of (0,0) to {(1,1),(2,2),(3,3),(4,4),(-1,-1),(-2,-2),(-3,-3),(-4,-4),(1,-1),(2,-2),(3,-3),(4,-4),(-1,1),(-2,2),(-3,3),(-4,4)} (12 total positions from center of board) {(x = i, y = i), (x = -i, y = i), (x = i, y = -i)}
        - Knight: Can move from a relative postion of (0,0) to {(-1,2),(1,2),(-2,1),(2,1),(-2,-1),(2,-1),(-1,-2),(-1,2)} (8 total positions) {(x = )}
        - King: can move to any position in a 1 block radius of current square on board {(x<=1, y<=1)}
        - Queen: can move to any position on the board in a strait line {(x = 0, y < 8), (x < 8, y = 0), (x = i, y = i), (x = -i, y = i), (x = i, y = -i)}
        - Pawn: can move one block forward and attack diagonal. Can move forward 2 on first move. { if first move {(x = 2, y = 0)} else if on attack {(x = 1, y = 1), (x = 1, y = -1)} else {(x = 1, y = 0)}}

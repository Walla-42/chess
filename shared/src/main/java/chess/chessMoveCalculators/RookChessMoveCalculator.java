package chess.chessMoveCalculators;

/**
 * class extension of the SharedCalculatorLogic class returning the allowed direction for the Rook chess piece
 */
public class RookChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedDirectionalMoves(){
        return new int[][]{{-1,0}, {1,0}, {0,-1}, {0,1}};
    }
}
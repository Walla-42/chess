package chess.chessmovecalculators;

/**
 * class extension of the SharedCalculatorLogic class returning the allowed direction for the Queen chess piece
 */
public class QueenChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedDirectionalMoves(){
        return new int[][]{{-1,1}, {1,1}, {1,-1}, {-1,-1}, {-1,0}, {1,0}, {0,-1}, {0,1}};
    }
}

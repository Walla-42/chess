package chess.chessMoveCalculators;

/**
 * class extension of the SharedCalculatorLogic class returning the allowed direction for the Bishop chess piece
 */
public class BishopChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedDirectionalMoves(){
        return new int[][]{{-1,1}, {1,1}, {1,-1}, {-1,-1}};
    }

}

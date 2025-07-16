package chess.chessmovecalculators;

/**
 * class extension of the SharedCalculatorLogic class returning the allowed direction for the Knight chess piece
 */
public class KnightChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedFixedMoves(){
        return new int[][]{{1,2}, {2,1}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {2,-1}, {1,-2}};
    }


}

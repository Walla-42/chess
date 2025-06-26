package chess.chessMoveCalculators;

public class KnightChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedFixedMoves(){
        return new int[][]{{1,2}, {2,1}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {2,-1}, {1,-2}};
    }


}

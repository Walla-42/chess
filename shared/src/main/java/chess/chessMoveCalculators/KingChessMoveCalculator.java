package chess.chessMoveCalculators;

public class KingChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedFixedMoves(){
        return new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}, {0,1}, {1,0}, {-1,0}, {0,-1}};
    }
}

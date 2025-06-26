package chess.chessMoveCalculators;

public class RookChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedDirectionalMoves(){
        return new int[][]{{-1,0}, {1,0}, {0,-1}, {0,1}};
    }
}
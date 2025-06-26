package chess.chessMoveCalculators;

public class QueenChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedDirectionalMoves(){
        return new int[][]{{-1,1}, {1,1}, {1,-1}, {-1,-1}, {-1,0}, {1,0}, {0,-1}, {0,1}};
    }
}

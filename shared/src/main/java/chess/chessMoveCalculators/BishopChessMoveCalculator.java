package chess.chessMoveCalculators;

public class BishopChessMoveCalculator extends SharedCalculatorLogic{
    @Override
    public int [][] allowedDirectionalMoves(){
        return new int[][]{{-1,1}, {1,1}, {1,-1}, {-1,-1}};
    }

}

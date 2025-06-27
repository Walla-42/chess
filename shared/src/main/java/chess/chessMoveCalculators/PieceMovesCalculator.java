package chess.chessMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public interface PieceMovesCalculator {
    HashSet<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}



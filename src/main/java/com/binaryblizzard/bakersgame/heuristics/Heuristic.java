package com.binaryblizzard.bakersgame.heuristics;

import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.Move;

/**
 * Interface implemented by heuristics that evaluate a move and return a weight indicating if it is good or bad
 */

public interface Heuristic {

    /**
     * Evaluate a move of a card on the game baord.
     *
     * @param board The current state of the board
     * @param move The move to evaluate
     * @return an integer weight that indicates how good or bad the move is
     */

    int evaluate(Board board, Move move);
}

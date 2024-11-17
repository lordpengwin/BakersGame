package com.binaryblizzard.bakersgame.heuristics;

import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.CardPosition;
import com.binaryblizzard.bakersgame.Move;

public class ReserveEmptySlot implements Heuristic {

    /**
     * Give a bonus for emptying a reserve spot
     *
     * @see Heuristic#evaluate(Board, Move)
     */

    @Override
    public int evaluate(Board board, Move move) {

        if (move.getFrom() == CardPosition.RESERVE)
            return 1;
        else
            return 0;
    }
}

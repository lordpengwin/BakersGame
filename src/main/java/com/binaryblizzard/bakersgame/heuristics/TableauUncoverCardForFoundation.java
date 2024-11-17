package com.binaryblizzard.bakersgame.heuristics;

import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.Card;
import com.binaryblizzard.bakersgame.CardPosition;
import com.binaryblizzard.bakersgame.Move;

import java.util.List;

@SuppressWarnings("unchecked")
public class TableauUncoverCardForFoundation implements Heuristic {

    /**
     * This checks if the move will uncover a card that can then be moved to the foundation
     *
     * @see Heuristic#evaluate(Board, Move)
     */

    @Override
    public int evaluate(Board board, Move move) {

        int weight = 0;
        CardPosition from = move.getFrom();
        if (from.getArea() == CardPosition.Area.TABLEAU) {

            List<Card> fromColumn = board.getTableau()[from.getColumn()];
            if (fromColumn.size() > 1) {
                Card uncovered = fromColumn.get(fromColumn.size() - 2);
                if (board.isMoveLegal(uncovered, CardPosition.FOUNDATION))
                    weight += 2;
            }
        }

        return weight;
    }
}

package com.binaryblizzard.bakersgame.heuristics;

import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.CardPosition;
import com.binaryblizzard.bakersgame.Move;

public class TableauEmptyAColumn implements Heuristic {

    /**
     * Emptying a column in the Tableau is good unless you are moving a card from one card to another empty column
     *
     * @see Heuristic#evaluate(Board, Move)
     */

    @Override
    public int evaluate(Board board, Move move) {

        CardPosition from = move.getFrom();
        CardPosition to = move.getTo();
        int weight = 0;
        if ((from.getArea() == CardPosition.Area.TABLEAU) && (board.getTableau()[from.getColumn()].size() == 1)) {
            if ((to.getArea() == CardPosition.Area.TABLEAU) && (! board.getTableau()[to.getColumn()].isEmpty()))
                weight = 2;
            else
                weight = -5;
        }

        return weight;
    }
}

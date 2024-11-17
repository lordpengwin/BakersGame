package com.binaryblizzard.bakersgame.heuristics;

import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.Card;
import com.binaryblizzard.bakersgame.CardPosition;
import com.binaryblizzard.bakersgame.Move;

import java.util.List;

@SuppressWarnings({"unchecked"})
public class ReserveEmptySlotForNextMove implements Heuristic {

    /**
     * Emptying a slot in the Reserve that leads to another move in the tableau is good
     *
     * @see Heuristic#evaluate(Board, Move)
     */

    @Override
    public int evaluate(Board board, Move move) {

        int weight = 0;
        CardPosition from = move.getFrom();
        CardPosition to = move.getTo();
        Card card = move.getCard();
        List<Card>[] tableau = board.getTableau();
        if (from.getArea() == CardPosition.Area.RESERVE && to.getArea() == CardPosition.Area.TABLEAU) {

            // Check the other cards in the tableau and see if any of them can be moved on to this move's card in the next turn
            // There is an extra bonus if this will empty a slot in the tableau

            for (int i = 0; i < tableau.length; i++)
                if (i != to.getColumn() && (! tableau[i].isEmpty())) {
                    Card futureCard = tableau[i].get(tableau[i].size() - 1);
                    if ((futureCard.getSuit() == card.getSuit()) && (futureCard.getRank().getValue() == card.getRank().getValue() - 1))
                        weight += (tableau[i].size() == 1 ? 2 : 1);
                }
        }

        return weight;
    }
}

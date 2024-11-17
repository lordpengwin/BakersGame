package com.binaryblizzard.bakersgame.heuristics;

import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.Card;
import com.binaryblizzard.bakersgame.CardPosition;
import com.binaryblizzard.bakersgame.Move;

import java.util.List;

@SuppressWarnings("unchecked")
public class TableauUnCoverLowerCardOfSameSuit implements Heuristic {

    /**
     * Uncovering a card of the same suit but lower rank in a column on the Tableau is good because we will need that covered card to
     * move both cards to the foundation
     *
     * @see Heuristic#evaluate(Board, Move)
     */

    @Override
    public int evaluate(Board board, Move move) {

        // The target area must be the tableau

        int weight = 0;
        CardPosition from = move.getFrom();
        if (from.getArea() != CardPosition.Area.TABLEAU)
            return weight;

        // Look at all the cards in the source column and add a bonus for any of the same suit and lesser rank

        Card card = move.getCard();
        List<Card> sourceColumn = board.getTableau()[from.getColumn()];
        for (int i = sourceColumn.size() - 2; i >= 0; i--) {
            Card uncoveredCard = sourceColumn.get(i);
            if ((uncoveredCard.getSuit() == card.getSuit()) && (uncoveredCard.getRank().getValue() < card.getRank().getValue())) {

                // If the uncovered card can go on the foundation, give a bigger bonus

                if (board.isMoveLegal(uncoveredCard, CardPosition.FOUNDATION))
                    weight += 2;
                else
                    weight += 1;
            }
        }

        return weight;
    }
}

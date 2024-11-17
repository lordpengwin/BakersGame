package com.binaryblizzard.bakersgame.heuristics;

import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.Card;
import com.binaryblizzard.bakersgame.CardPosition;
import com.binaryblizzard.bakersgame.Move;

import java.util.List;

@SuppressWarnings("unchecked")
public class TableauCoverLowerCardOfSameSuit implements Heuristic {

    /**
     * Covering a card of the same suit but lower rank in a column on the Tableau is bad because we will need that covered card to
     * move both cards to the foundation
     *
     * @see Heuristic#evaluate(Board, Move)
     */

    @Override
    public int evaluate(Board board, Move move) {

        // The target area must be the tableau

        int weight = 0;
        CardPosition to = move.getTo();
        if (to.getArea() != CardPosition.Area.TABLEAU)
            return weight;

        // Look at all the cards in the target column and add a penalty for any of the same suit and lesser rank

        Card card = move.getCard();
        List<Card> targetColumn = board.getTableau()[to.getColumn()];
        for (int i = targetColumn.size() - 1; i >= 0; i--) {
            Card lowerCard = targetColumn.get(i);
            if ((lowerCard.getSuit() == card.getSuit()) && (lowerCard.getRank().getValue() < card.getRank().getValue())) {

                // If the card could be moved to the foundation, increase the penalty

                if (board.isMoveLegal(lowerCard, CardPosition.FOUNDATION))
                    weight -= 2;
                else
                    weight -= 1;
            }
        }

        return weight;
    }
}

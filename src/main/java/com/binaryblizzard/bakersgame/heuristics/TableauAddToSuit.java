package com.binaryblizzard.bakersgame.heuristics;

import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.Card;
import com.binaryblizzard.bakersgame.CardPosition;
import com.binaryblizzard.bakersgame.Move;

import java.util.List;

@SuppressWarnings("unchecked")
public class TableauAddToSuit implements Heuristic {

    /**
     * Moving a card onto another card the one higher than it in the same suit is good.
     *
     * @see Heuristic#evaluate(Board, Move)
     */

    @Override
    public int evaluate(Board board, Move move) {

        // The target area must be the tableau

        CardPosition to = move.getTo();
        if (to.getArea() != CardPosition.Area.TABLEAU)
            return 0;

        // Collect information about the move

        Card card = move.getCard();
        List<Card> targetColumn = board.getTableau()[to.getColumn()];

        // Check if the card at the bottom of the target column is the one higher than the current card in its suit

        if (! targetColumn.isEmpty()) {
            Card bottomCard = targetColumn.get(targetColumn.size() - 1);
            if (bottomCard.getSuit() == card.getSuit() && bottomCard.getRank().getValue() == (card.getRank().getValue() + 1))
                return 2;
        }

        return 0;
    }
}

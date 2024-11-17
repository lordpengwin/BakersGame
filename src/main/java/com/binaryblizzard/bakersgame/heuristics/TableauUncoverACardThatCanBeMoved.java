package com.binaryblizzard.bakersgame.heuristics;


import com.binaryblizzard.bakersgame.Board;
import com.binaryblizzard.bakersgame.Card;
import com.binaryblizzard.bakersgame.CardPosition;
import com.binaryblizzard.bakersgame.Move;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class TableauUncoverACardThatCanBeMoved implements Heuristic{

    /**
     * Uncovering a card that can then be moved to another tableau pile is good
     *
     * @see Heuristic#evaluate(Board, Move)
     */

    @Override
    public int evaluate(Board board, Move move) {

        int weight = 0;
        if (move.getFrom().getArea() == CardPosition.Area.TABLEAU) {

            // Find the card that under the one that we are moving and see if it can be moved to any other tableau pile

            Card card = move.getCard();
            CardPosition from = move.getFrom();
            CardPosition to = move.getTo();
            List<Card> fromColumn = board.getTableau()[from.getColumn()];
            if (fromColumn.size() > 1) {

                // Get the uncovered card figure create a list of columns that we can't move it to

                Card uncoveredCard = fromColumn.get(fromColumn.size() - 2);
                List<Integer> exclude = new ArrayList<>(2);
                exclude.add(from.getColumn());
                if (to.getArea() == CardPosition.Area.TABLEAU)
                    exclude.add(to.getColumn());

                // Check if it can move the uncovered card to another column

                for (int i = 0; i < board.getTableau().length; i++)
                    if (! exclude.contains(i) && board.isMoveLegal(uncoveredCard, new CardPosition(CardPosition.Area.TABLEAU, i)))
                        weight++;

                // Finally check of the uncovered card would be movable on top of the card currently being moved

                if ((to.getArea() == CardPosition.Area.TABLEAU) && (uncoveredCard.getSuit() == card.getSuit()) && (uncoveredCard.getRank().getValue() == (card.getRank().getValue() - 1)))
                    weight++;

            }
        }

        return weight;
    }
}

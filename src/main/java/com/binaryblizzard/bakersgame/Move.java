package com.binaryblizzard.bakersgame;

/**
 * This object represents a Move in the game
 */

public class Move {

    /** The location on the board that the card was moved from. */

    private CardPosition from;

    /** The location on the board that the card is moving to. */

    private CardPosition to;

    /** The card being moved. */

    private Card card;

    /** The weight of the move, positive is betterm negative is worse. */

    private int weight = 0;

    /**
     * Create a move.
     *
     * @param from The location the card is coming from
     * @param to The location that the card is going to
     * @param card The card being moved
     */

    public Move(CardPosition from, CardPosition to, Card card) {

        this.from = from;
        this.to = to;
        this.card = card;
    }

    /**
     * Get the from location.
     *
     * @return The location that the card is coming from
     */

    public CardPosition getFrom() {
        return from;
    }

    /**
     * Get the to location.
     *
     * @return The location that the card is moving to
     */

    public CardPosition getTo() {
        return to;
    }

    /**
     * Get the card being moved.
     *
     * @return The card
     */

    public Card getCard() {
        return card;
    }

    /**
     * Get the weight.
     *
     * @return the current weight
     */

    public int getWeight() {
        return weight;
    }

    /**
     * Update the weight by adding a value to it.
     *
     * @param updateAmount The amount to update the weight by
     */

    public void updateWeight(int updateAmount) {
        this.weight += updateAmount;
    }

    /** @see java.lang.Object#toString() */

    @Override
    public String toString() {
        return "Move{" +
                "from=" + from +
                ", to=" + to +
                ", card=" + card +
                ", weight=" + weight +
                '}';
    }
}

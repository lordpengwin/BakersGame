package com.binaryblizzard.bakersgame;

public class Card implements Comparable<Card> {

    /** Enum to represent a suit. */

    enum Suit {
        Spades("S"),
        Clubs("C"),
        Hearts("H"),
        Diamonds("D");

        /** The short form symbol of a suit. */

        private final String shortForm;

        /**
         * Create a Suit.
         *
         * @param shortForm The short form symbol of the suit
         */

        Suit(String shortForm) {
            this.shortForm = shortForm;
        }

        /**
         * Find a Suit by short form
         *
         * @param shortForm The short form to find the suit for
         * @return The matching suit
         * @throws IllegalArgumentException If the short form is not valid
         */

        static Suit forShortForm(String shortForm) throws IllegalArgumentException {

            // Search the values for the one that matches the given short form

            for (Suit value : values())
                if (value.shortForm.equals(shortForm))
                    return value;

            // Not found

            throw new IllegalArgumentException("ShortForm " + shortForm + " is not valid");
        }
    }

    /** Enum to represent the Rank of value of a card. */

    public enum Rank {

        Ace(1, "A"),
        Two(2, "2"),
        Three(3, "3"),
        Four(4, "4"),
        Five(5, "5"),
        Six(6, "6"),
        Seven(7, "7"),
        Eight(8, "8"),
        Nine(9, "9"),
        Ten(10, "10"),
        Jack(11, "J"),
        Queen(12, "Q"),
        King(13, "K");

        /** The value of the rank. */

        private final int value;

        /** The short form of the rank. */

        private final String shortForm;

        /**
         * Create a Rank.
         *
         * @param value The value of the Rank
         * @param shortForm The short form of the rank
         */

        Rank(int value, String shortForm) {
            this.value = value;
            this.shortForm = shortForm;
        }

        /**
         * Find a Rank by short form
         *
         * @param shortForm The short form to find the rank for
         * @return The matching rank
         * @throws IllegalArgumentException If the short form is not valid
         */

        static Rank forShortForm(String shortForm) throws IllegalArgumentException {

            // Search the values for the one that matches the given short form

            for (Rank value :values())
                if (value.shortForm.equals(shortForm))
                    return value;

            // Not found

            throw new IllegalArgumentException("ShortForm " + shortForm + " is not valid");
        }

        /**
         * Get the value of this Rank
         *
         * @return The value
         */

        public int getValue() {
            return value;
        }
    }

    /** An array of all the Suits. */

    public static final Suit[] SUITS = {Suit.Spades, Suit.Clubs, Suit.Hearts, Suit.Diamonds};

    /** An array of all the Ranks. */

    public static final Rank[] RANKS = {Rank.Ace, Rank.Two, Rank.Three, Rank.Four, Rank.Five, Rank.Six, Rank.Seven, Rank.Eight, Rank.Nine, Rank.Ten, Rank.Jack, Rank.Queen, Rank.King};

    /** The Suit of this card. */

    private final Suit suit;

    /** The Rank fo this card. */

    private final Rank rank;

    /**
     * Create a Card.
     *
     * @param suit The Suit of the card
     * @param rank The Rank of the card
     */

    public Card(Suit suit, Rank rank) {

        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Create a Card given its string from, e.g. 1S, KH, ...
     *
     * @param card The
     */
    public Card(String card) {
        this(Suit.forShortForm(card.substring(card.length() - 1)), Rank.forShortForm(card.substring(0, card.length() - 1)));
    }

    /**
     * Get the suit of the Card.
     *
     * @return The Suit
     */

    public Suit getSuit() {
        return suit;
    }

    /**
     * Get the rank of the Card.
     *
     * @return The rank.
     */

    public Rank getRank() {
        return rank;
    }

    /**
     * Cards are compared by suit and rank
     *
     * @see Comparable#compareTo(Object)
     */

    @Override
    public int compareTo(Card that) {

        if (this.equals(that))
            return 0;

        else if (this.suit != that.suit) {
            if (this.suit.ordinal() < that.suit.ordinal())
                return -1;
            else return 1;

        } else if (this.rank.value < that.rank.value)
            return -1;

        else
            return 1;
    }

    /**
     * The hash code for a card is the suit multipled by 100 plus the rank. This should give a unique code for each card.
     *
     * @see Object#hashCode()
     */

    @Override
    public int hashCode() {
        return (suit.ordinal() * 100) + rank.ordinal();
    }

    /**
     * Two cards are equal if they have the same suit and rank.
     *
     * @see Object#equals(Object)
     */

    @Override
    public boolean equals(Object obj) {

        if (! (obj instanceof Card))
            return false;

        Card other = (Card) obj;
        return (suit == other.suit) && (rank == other.rank);
    }

    /** @see java.lang.Object#toString() */

    @Override
    public String toString() {
        return rank.shortForm + suit.shortForm;
    }
}
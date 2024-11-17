package com.binaryblizzard.bakersgame;

/**
 * A CardPosition represents the location of a card on the board
 */

public class CardPosition {

    /** A pre-created position targeting the Foundation */

    public static final CardPosition FOUNDATION = new CardPosition(Area.FOUNDATION);

    /** A pre-created position targeting the reserve. */

    public static final CardPosition RESERVE = new CardPosition(Area.RESERVE);

    /** An array of pre-created positions targeting the Tableau. **/

    public static final CardPosition[] TABLEAU = new CardPosition[] {
        new CardPosition(Area.TABLEAU, 0),
        new CardPosition(Area.TABLEAU, 1),
        new CardPosition(Area.TABLEAU, 2),
        new CardPosition(Area.TABLEAU, 3),
        new CardPosition(Area.TABLEAU, 4),
        new CardPosition(Area.TABLEAU, 5),
        new CardPosition(Area.TABLEAU, 6),
        new CardPosition(Area.TABLEAU, 7)
    };

    /** The different areas on the board. */

    public enum Area {

        FOUNDATION("F"),
        TABLEAU("T"),
        RESERVE("R");

        /** A short form for the area */

        private final String shortForm;

        /**
         * Create an Area enum
         *
         * @param shortForm The short form for the Area
         */

        Area(String shortForm) {
            this.shortForm = shortForm;
        }
    }

    /** The Area that the card is in. */

    private final Area area;

    /** The column in the area (this is only used for the Tableau, it is set to -1 when not used). */

    private final int column;

    /**
     * Create a CardPosition
     *
     * @param area The area on the board that the card is in
     * @param column The column within the area
     */

    public CardPosition(Area area, int column) {

        this.area = area;
        this.column = column;
    }

    /**
     * Create a CardPosition that only uses the area
     *
     * @param area The area of the card
     */

    public CardPosition(Area area) {

        this.area = area;
        this.column = -1;
    }

    /**
     * Get the Area of the card.
     *
     * @return The area
     */

    public Area getArea() {
        return area;
    }

    /**
     * Get the column on the area of the card.
     *
     * @return The column
     */

    public int getColumn() {
        return column;
    }

    /** @see java.lang.Object#toString() */

    @Override
    public String toString() {
        return (column == -1 ? area.shortForm : area.shortForm + column);
    }
}


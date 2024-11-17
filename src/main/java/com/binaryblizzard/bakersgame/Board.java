package com.binaryblizzard.bakersgame;

import com.binaryblizzard.bakersgame.heuristics.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/** A class representing the state of a Bakers Game board. */

@SuppressWarnings({"rawtypes", "unchecked"})
public class Board {

    /** The logger for this class. */

    private static final Logger LOG = Logger.getLogger(Board.class.getName());

    /** The foundation is the set of piles where cards ar built up in suit from Ace to King. */

    private final Map<Card.Suit, List<Card>> foundation = new HashMap<>();

    /** The Tableau is the set of columns (or cascades) that consist of the cards that can be arranged in descending order by suit. There are 8 cascades. */

    private final ArrayList[] tableau = new ArrayList[8];

    /** The set of free cells that can hold up to 4 cards. */

    private final List<Card> reserve = new ArrayList<>(4);

    /** A list of pending moves. */

    private final List<Move> pendingMoves = new ArrayList<>(110);

    /** The sequence of moves that produced this board. */

    private List<Move> solution = new ArrayList<>(110);

    /** A Random number used to shuffle lists. */

    private final Random random = new Random();

    /** A collection of heuristics used to evaluate moves. */

    private final Heuristic[] heuristics = new Heuristic[]{
            new TableauAddToSuit(),
            new TableauCoverLowerCardOfSameSuit(),
            new TableauEmptyAColumn(),
            new TableauUncoverCardForFoundation(),
            new TableauUnCoverLowerCardOfSameSuit(),
            new ReserveEmptySlot(),
            new TableauUncoverACardThatCanBeMoved(),
            new ReserveEmptySlotForNextMove()
    };

    /**
     * Create a Board.
     */

    public Board() {

        // Initialize the structures

        for (Card.Suit suit : Card.SUITS)
            foundation.put(suit, new ArrayList<>());

        for (int i = 0; i < tableau.length; i++)
            tableau[i] = new ArrayList<Card>();
    }

    /**
     * Load a Board from a JSON file.
     *
     * @param filePath The path to the file to load the board from
     */

    public Board(String filePath) throws IOException {

        this();

        // Load the board object from the JSON file

        JsonObject board = JsonParser.parseReader(new FileReader(filePath)).getAsJsonObject().getAsJsonObject("board");

        // Load the foundation object and populate it

        JsonObject foundationObject = board.getAsJsonObject("foundation");
        for (Card.Suit suit: Card.SUITS)
            if (foundationObject.has(suit.name())) {
                JsonArray suitCards = foundationObject.getAsJsonArray(suit.name());
                for (JsonElement cardElement : suitCards.asList())
                    foundation.get(suit).add(new Card(cardElement.getAsString()));
            }

        // Load the reserve

        JsonArray reserveArray = board.getAsJsonArray("reserve");
        for (JsonElement cardElement : reserveArray.asList())
            reserve.add(new Card(cardElement.getAsString()));

        // Now the Tableau

        JsonArray tableauArray = board.getAsJsonArray("tableau");
        for (int i = 0; i < tableau.length; i++) {
            JsonArray column = tableauArray.get(i).getAsJsonArray();
            for (JsonElement cardElement : column.asList())
                tableau[i].add(new Card(cardElement.getAsString()));
        }

        // Compute the possible moves for the board

        computePendingMoves();
//        LOG.info(this.toString());
    }

    /**
     * Create a Board that is based on another board.
     *
     * @param copyFrom The board to copy the new one from.
     */

    private Board(Board copyFrom, Move move) {

        this();

        // Copy the Foundation

        for (Card.Suit suit : Card.SUITS)
            for (Card card : copyFrom.foundation.get(suit))
                foundation.get(suit).add(card);

        // Copy the Reserve

        reserve.addAll(copyFrom.reserve);

        // And the Tableau

        for (int i = 0; i < copyFrom.tableau.length; i++) {
            List<Card> column = copyFrom.tableau[i];
            for (Card card : column)
                tableau[i].add(card);
        }

        // Set the sequence of moves

        solution = new ArrayList<>(copyFrom.solution);
        solution.add(move);
    }

    /**
     * Get the Tableau section of the Board.
     *
     * @return The Tableau
     */

    public ArrayList[] getTableau() {
        return tableau;
    }

    /**
     * Get the Solution
     *
     * @return The list of moves that lead to the solution
     */

    public List<Move> getSolution() {
        return solution;
    }

    /**
     * Check if the current state of the board represents a won game
     *
     * @return true if the game is won, false otherwise
     */

    public boolean gameIsWon() {

        // The game is won if the Reserve and Tableau are empty

        if (! reserve.isEmpty())
            return false;

        for (List<Card> column : tableau)
            if (! column.isEmpty())
                return false;

        // Woot!

        return true;
    }

    /**
     * Check if this board has failed, i.e. there are no more moves
     *
     * @return true if the board is a failure
     */

    public boolean boardHasFailed() {
        return pendingMoves.isEmpty();
    }

    /**
     * Apply a move and return the resulting board.
     *
     * @return The resulting board after the move is applied
     * @throws IllegalStateException If the move is illegal. Note this really should not happen but it is a way to ensure that things are working as expected.
     */

    public Board applyNextMove() throws IllegalStateException {

        // Get the next move and transfer it to the tried moves list

        if (pendingMoves.isEmpty())
            throw new IllegalStateException("Board has no more moves available");
        Move move = pendingMoves.remove(0);

        // Make a copy of this board

        Board resultingBoard = new Board(this, move);

        // Remove the card from the from position

        Card card = move.getCard();
        resultingBoard.removeCard(card, move.getFrom());

        // Put it in the new position

        resultingBoard.addCard(card, move.getTo());

        // Compute the pending moves for the new board.

        resultingBoard.computePendingMoves();

        // Return the new board

        return resultingBoard;
    }

    /**
     * Check if a given card can be moved to a target position.
     *
     * @param card The card to check
     * @param targetPosition The position to move it to
     * @return true if the move is legal, false otherwise
     */

    public boolean isMoveLegal(Card card, CardPosition targetPosition) {

        // The reserve must have an empty slot

        if ((targetPosition.getArea() == CardPosition.Area.RESERVE)) {
            return reserve.size() != 4;

        } else if (targetPosition.getArea() == CardPosition.Area.FOUNDATION) {

            // Check the last card in the target foundation column to make sure it is the one lower than the new card

            List<Card> pile = foundation.get(card.getSuit());
            if (pile.isEmpty() && (card.getRank() != Card.Rank.Ace))
                return false;

            else if (! pile.isEmpty()) {

                Card topCard = pile.get(pile.size() - 1);
                return topCard.getRank().getValue() == (card.getRank().getValue() - 1);
            }

        } else {

            // Check if we can add the card to the tableau. The column must be empty or last card must have the same suit and be one higher than the card

            List<Card> column = tableau[targetPosition.getColumn()];
            if (! column.isEmpty()) {

                Card lastCard = column.get(column.size() - 1);
                return (lastCard.getSuit() == card.getSuit()) && (lastCard.getRank().getValue() == (card.getRank().getValue() + 1));
            }
        }

        // It must be a legal move

        return true;
    }

    /**
     * Compute a set of heuristics that determine if a move is desirable or not
     *
     * @param move The move to apply the heuristics to
     * @return An amount to adjust the weight of the move by
     */

    private int computeHeuristics(Move move) {

        int adjustment = 0;
        for (Heuristic heuristic : heuristics)
            adjustment += heuristic.evaluate(this, move);

        // Return the adjustment

        return adjustment;
    }

    /**
     * Remove a card from a given position in the board.
     *
     * @param card The card to remove (this is used to verify this move)
     * @param position The position to remove the card from
     * @throws IllegalStateException If the card does not exist in the target position or if the move is invalid
     */

    private void removeCard(Card card, CardPosition position) {

        if (position.getArea() == CardPosition.Area.RESERVE) {

            // Find the card in the reserve

            if (! reserve.remove(card))
                throw new IllegalStateException("Failed to find card: " + card + " in Reserve");

        } else if (position.getArea() == CardPosition.Area.TABLEAU) {

            // Check that the card is the last element of the target column in the Tableau

            List<Card> column = tableau[position.getColumn()];
            int index = column.size() - 1;
            if (column.isEmpty() || ! column.get(index).equals(card))
                throw new IllegalStateException("Failed to find card: " + card + " at the end of column " + position.getColumn() + " in Tableau");
            column.remove(index);

        } else

            // Invalid position

            throw new IllegalStateException("From position is not valid: " + position);
    }

    /**
     * Put a card in a position on the board.
     *
     * @param card The card to put
     * @param position The position
     * @throws IllegalStateException If the card cannot be put in the target position
     */

    private void addCard(Card card, CardPosition position) {

        // Verify that the move is legal

        if (! isMoveLegal(card, position))
            throw new IllegalStateException("It is illegal to move card: " + card + " to position: " + position);

        // Add the card to the target postion

        if (position.getArea() == CardPosition.Area.RESERVE) {

            // Add the card and re-sort the reserve. This ensures that we don't waste moves targeting different slots when they all are equal

            reserve.add(card);
            Collections.sort(reserve);

        } else if (position.getArea() == CardPosition.Area.FOUNDATION) {

            // Add the card to the foundation

            List<Card> pile = foundation.get(card.getSuit());
            pile.add(card);

        } else {

            // Add it to the Tableau

            List<Card> column = tableau[position.getColumn()];
            column.add(card);
        }
    }

    /**
     * Create a list of possible moves from the current board state. This updates the pendingMoves list for this board.
     */

    public void computePendingMoves() {

        // Make sure that the list of moves is empty

        pendingMoves.clear();

        // Check each of the available cards to see if they can go in the Foundation, If one can, that is the only move to make

        for (Card card : reserve)
            if (isMoveLegal(card, CardPosition.FOUNDATION)) {
                pendingMoves.add(new Move(CardPosition.RESERVE, CardPosition.FOUNDATION, card));
                return;
            }

        for (int i = 0; i < tableau.length; i++) {
            List<Card> column = tableau[i];
            if (! column.isEmpty()) {
                Card card = column.get(column.size() - 1);
                if (isMoveLegal(card, CardPosition.FOUNDATION)) {
                    pendingMoves.add(new Move(CardPosition.TABLEAU[i], CardPosition.FOUNDATION, card));
                    return;
                }
            }
        }

        // Check if the Reserve cards can move to the Tableau

        for (Card card : reserve)
            for (CardPosition position : CardPosition.TABLEAU)
                if (isMoveLegal(card, position)) {
                    Move move = new Move(CardPosition.RESERVE, position, card);
                    move.updateWeight(computeHeuristics(move));
                    pendingMoves.add(move);
                }

        // Check if the tableau cards can move to the reserve or other tableau spots

        for (int i = 0; i < tableau.length; i++) {

            List<Card> column = tableau[i];
            if (! column.isEmpty()) {

                Card card = column.get(column.size() - 1);
                for (CardPosition cardPosition : CardPosition.TABLEAU)
                    if (isMoveLegal(card, cardPosition)) {
                        Move move = new Move(CardPosition.TABLEAU[i], cardPosition, card);
                        move.updateWeight(computeHeuristics(move));
                        pendingMoves.add(move);
                    }

                if (isMoveLegal(card, CardPosition.RESERVE)) {
                    Move move = new Move(CardPosition.TABLEAU[i], CardPosition.RESERVE, card);
                    move.updateWeight(computeHeuristics(move));
                    pendingMoves.add(move);
                }
            }
        }

        // Randomly shuffle the pending moves so that one end of the board does not get all the attention. This greatly
        // improves the results and I'm not sure why

        Collections.shuffle(pendingMoves, random);

        // Sort the moves by weight

        pendingMoves.sort((move1, move2) -> {
            return Integer.compare(move2.getWeight(), move1.getWeight());
        });

//        System.out.println(pendingMoves);
//        System.exit(0);
    }

    /**
     * Create a signature for the board. This is a string of characters that uniquely identifies the board state
     *
     * @return A unique signature for the board.
     */

    public String getSignature() {

        StringBuilder builder = new StringBuilder();

        // Foundation

        for (Card.Suit suit : Card.SUITS) {
            List<Card> pile =  foundation.get(suit);
            if (pile.isEmpty())
                builder.append("B");
            else
                pile.forEach(builder::append);
        }
        builder.append("|");

        // Tableau

        for (List<Card> column : tableau) {
            if (column.isEmpty())
                builder.append("B");
            else
                column.forEach(builder::append);
            builder.append("|");
        }

        // Reserve

        reserve.forEach(builder::append);

        return builder.toString();
    }

    /** @see Object#toString() */

    @Override
    public String toString() {

        return "Board{" +
                "foundation=" + foundation +
                ", reserve=" + reserve +
                ", tableau=" + Arrays.toString(tableau) +
                '}';
    }
}

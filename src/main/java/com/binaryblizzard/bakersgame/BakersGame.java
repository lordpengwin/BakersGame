package com.binaryblizzard.bakersgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

public class BakersGame {

    /** The logger for this class. */

    private static final Logger LOG = Logger.getLogger(BakersGame.class.getName());

    /** The initial Baord. */

    private Board initialBoard;

    /** A stack of boards and the moves that created them. */

    private Stack<Board> gameStates = new Stack<>();

    /** A set of boards that we have already seen. */

    private Set<String> previousBoards = new HashSet<>();

    /** The shortest solution. */

    private List<Move> solution = null;

    /**
     * Create a BakersGame solver.
     *
     * @param boardFile A path to a file defining the initial deal
     * @throws IOException If the file can't be read
     */

    public BakersGame(String boardFile) throws IOException {
        initialBoard = new Board(boardFile);
    }

    public boolean solveGame() throws IOException {


        Board currentBoard = initialBoard;
        previousBoards.add(currentBoard.getSignature());
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        int cnt = 0;
        int skiped = 0;

        // This is the main loop where we apply the next move to a board and check for a solution.

        while (true) {

            // If there are no more moves with the current board, we have to the previous board on the stack. If we run out of boards we are done

            while (currentBoard.boardHasFailed())
                if (gameStates.isEmpty()) {

                    // We've searched the whole tree

                    if (solution != null) {
                        LOG.info("Final solution");
                        dumpGameSolution(solution);
                        return true;
                    }

                    // We failed to find anything

                    return false;

                } else {

                    // Just pop the previous board

//                    LOG.info("Board has failed");
                    currentBoard = gameStates.pop();
                }

            // Try the next move with the current board

            Board nextBoard = currentBoard.applyNextMove();

            // See if we have a solution

            if (nextBoard.gameIsWon()) {

                // Check to see if this is the shortest solution so far

                if ((solution == null) || (nextBoard.getSolution().size() < solution.size())) {

                    // Yes so save it and print it

                    LOG.info("Found a solution of length: " + nextBoard.getSolution().size() + " shortest is " + (solution == null ? "" : solution.size()));
                    solution = new ArrayList<>(nextBoard.getSolution());
                    dumpGameSolution(solution);

                    // Quit it we reach a target length

                    if (solution.size() < 130) {
                        return true;
                    }
                }

                // Reset to the first board and keep looking for more

                previousBoards.clear();
                gameStates.clear();
                nextBoard = initialBoard;
                nextBoard.computePendingMoves();
            }

            // Have we seen this board before?

            if (! previousBoards.contains(nextBoard.getSignature())) {

                // No so save the previous board on the stack and move forward with this one

                gameStates.push(currentBoard);
                currentBoard = nextBoard;
                previousBoards.add(currentBoard.getSignature());

            } else

                // Yes so just skip the next board and continue with the current one

                skiped++;

            // Output some information periodically

//            if (cnt++ % 100 == 0) {
//                System.out.println(currentBoard.toString() + " stack depth: " + gameStates.size() + " cnt: " + cnt + " skipped: " + skiped);
//                stdin.readLine();
//            }
        }
    }

    /**
     * Print the solution to the game.
     *
     * @param solution The list of moves that lead to a solution
     */

    private void dumpGameSolution(List<Move> solution) {

        for (Move move : solution)
            System.out.println(move);
    }

    /**
     * Try to solve a BakersGame
     *
     * @param args The path to a file with the initial board state
     */

    public static void main(String[] args) {

        try {
            BakersGame bakersGame = new BakersGame(args[0]);
            bakersGame.solveGame();

        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.severe("Caught exception: " +  ex);
        }

    }
}


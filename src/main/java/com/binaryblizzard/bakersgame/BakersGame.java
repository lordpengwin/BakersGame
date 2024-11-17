package com.binaryblizzard.bakersgame;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

/**
 * This is a program that is given an initial deal for the Baker's Solitaire Game and will try to find solutions for
 * it. It basically does a brute force search until a solution is found.
 */

@SuppressWarnings("ReassignedVariable")
public class BakersGame {

    /** The logger for this class. */

    private static final Logger LOG = Logger.getLogger(BakersGame.class.getName());

    /** The name of the board. */

    private final String boardName;

    /** The initial Board read from an input file. */

    private final Board initialBoard;

    /**
     * Create a BakersGame solver.
     *
     * @param boardFile A path to a file defining the initial deal
     * @throws IOException If the file can't be read
     */

    public BakersGame(String boardFile) throws IOException {

        initialBoard = new Board(boardFile);
        boardName = Paths.get(boardFile).getFileName().toString().replaceFirst("[.][^.]+$", "");
        LOG.info("Board name: " + boardName);
    }

    /**
     * Generate solutions for the Bakers Game. This will search the for shorter and shorter solutions. It will terminate
     * after the specified number is found.
     *
     * @param maxSolutions The maximum number of solutions to stop after
     * @return true if a solution was found.
     * @throws IOException If an error occurs
     */

    public boolean solveGame(int maxSolutions) throws IOException {

        // Initialize the game data structures

        List<Move> solution = null;
        Stack<Board> gameStates = new Stack<>();
        Set<String> previousBoards = new HashSet<>();

        // Some stats to collect

        int cnt = 0;
        int skipped = 0;
        int solutions = 0;

        Board currentBoard = initialBoard;
        previousBoards.add(currentBoard.getSignature());

        // This is the main loop where we apply the next move to a board and check for a solution.

        while (true) {

            // If there are no more moves with the current board, we have to pop the previous board from the stack. If we run out of boards we are done

            while (currentBoard.boardHasFailed())
                if (gameStates.isEmpty()) {

                    // We've searched the whole tree

                    if (solution != null) {
                        LOG.info("Final solution");
                        dumpGameSolution(solution);
                        return true;
                    }

                    // We failed to find anything

                    LOG.severe("Failed to find a solution");
                    return false;

                } else {

                    // Just pop the previous board

                    currentBoard = gameStates.pop();
                }

            // Try the next move with the current board

            Board nextBoard = currentBoard.applyNextMove();

            // See if we have a solution

            if (nextBoard.gameIsWon()) {

                // Check to see if this is the shortest solution so far

                solutions++;
                if (solutions % 100 == 0)
                    LOG.info("Found " + solutions + " solutions");

                if ((solution == null) || (nextBoard.getSolution().size() < solution.size())) {

                    // Yes so save it and print it

                    LOG.info("Found solution #" + solutions + " of length " + nextBoard.getSolution().size() + ", previous shortest was " + (solution == null ? "" : solution.size()));
                    solution = new ArrayList<>(nextBoard.getSolution());
                }

                // Quit if we reach the maximum solutions

                if (solutions == maxSolutions) {
                    LOG.info("Quitting after " + solutions + " solutions, shortest is " + solution.size());
                    dumpGameSolution(solution);
                    return true;
                }

                // Reset to the first board and keep looking for more

                previousBoards.clear();
                gameStates.clear();
                nextBoard = initialBoard;
                nextBoard.computePendingMoves();
            }

            // Have we seen next board before?

            if (! previousBoards.contains(nextBoard.getSignature())) {

                // No so save the previous board on the stack and move forward with this one

                gameStates.push(currentBoard);
                currentBoard = nextBoard;
                previousBoards.add(currentBoard.getSignature());

            } else

                // Yes so just skip it and continue with the current one

                skipped++;

            // Output some information periodically

//            if (cnt++ % 100 == 0) {
//                LOG.info(currentBoard.toString() + " stack depth: " + gameStates.size() + " cnt: " + cnt + " skipped: " + skipped);
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

        try {

            // Dump the solution to a file

            PrintWriter writer = new PrintWriter(new FileWriter(boardName + "-solution-" + solution.size()));
            for (Move move : solution)
                writer.println(move.toString());
            writer.close();

        } catch (IOException ioe) {
            LOG.severe("Failed to write solution to file: " + ioe);
        }
    }

    /**
     * Try to solve a BakersGame
     *
     * @param args The path to a file with the initial board state
     */

    public static void main(String[] args) {

        // Configure the logger to use one line.

        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        try {

            BakersGame bakersGame = new BakersGame(args[0]);
            boolean success = bakersGame.solveGame(2000);
            System.exit(success ? 0 : 1);

        } catch (Exception ex) {

            ex.printStackTrace();
            LOG.severe("Caught exception: " +  ex);
        }
    }
}


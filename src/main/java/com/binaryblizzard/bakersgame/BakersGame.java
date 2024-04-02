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

        // Apply the next move to the board and push the result on the stack if the resulting board is a winner, dump the moves that got us there

        Board currentBoard = initialBoard;
        previousBoards.add(currentBoard.getSignature());
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        int cnt = 0;
        int skiped = 0;
        while (true) {


            // We have to avoid retrying boards that we have already seen

            boolean retry = true;
            do {

                // If there are no more moves with the current board, we have to rewind

                while (currentBoard.boardHasFailed())
                    if (gameStates.isEmpty()) {
                        if (solution != null) {
                            dumpGameSolution(solution);
                            return true;
                        }
                        return false;
                    } else {
//                        LOG.info("Board has failed");
                        currentBoard = gameStates.pop();
                    }

                // Try the next move with the current board

                Board nextBoard = currentBoard.applyNextMove();

                // See if we have won

                if (nextBoard.gameIsWon()) {
                    LOG.info("Found a solution of length: " + nextBoard.getSolution().size());
                    if ((solution == null) || (nextBoard.getSolution().size() < solution.size())) {
                        solution = new ArrayList<>(nextBoard.getSolution());
                        if (solution.size() < 100) {
                            dumpGameSolution(solution);
                            return true;
                        } else {

                            // Reset

                            previousBoards.clear();
                            gameStates.clear();
                            nextBoard = initialBoard;
                            nextBoard.computePendingMoves();
                        }
                    }
                }

                // Have we seen this board before?

                if (! previousBoards.contains(nextBoard.getSignature())) {
                    gameStates.push(currentBoard);
                    currentBoard = nextBoard;
                    previousBoards.add(currentBoard.getSignature());
                    retry = false;
                } else
                    skiped++;
                   // LOG.info("Skipping " + nextBoard);
            } while (retry);

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


package com.example.chester.puzzlepieces;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PuzzleUnitTest {
  private final int PUZZLE_COLUMNS = 50;
  private final int PUZZLE_ROWS = 20;

  private Puzzle puzzle;

  @Before
  public void createPuzzle() {
    puzzle = new Puzzle(PUZZLE_COLUMNS, PUZZLE_ROWS);
  }

  @Test
  public void piecesCountIsValid() {
    assertEquals(puzzle.getPiecesCount(), PUZZLE_COLUMNS * PUZZLE_ROWS);
  }

  @Test
  public void allPiecesInPlace() throws Exception {
    for (int pieceNumber = 0; pieceNumber < puzzle.getPiecesCount(); pieceNumber++) {
      assertNotNull(puzzle.getPiece(pieceNumber));
    }
  }

  @Test
  public void allPiecesHaveValidSideForms() {
    int piecesCount = puzzle.getPiecesCount();
    for (int pieceNumber = 0; pieceNumber < piecesCount; pieceNumber++) {

      Puzzle.Piece piece = puzzle.getPiece(pieceNumber);

      // First piece in every row must have left side flat,
      // all other pieces have their left sides curvy
      if (pieceNumber % PUZZLE_COLUMNS == 0) {
        assertEquals(piece.getSideForm(Puzzle.Piece.SIDE_LEFT), Puzzle.Piece.SIDE_FORM_FLAT);
      } else {
        assertNotEquals(piece.getSideForm(Puzzle.Piece.SIDE_LEFT), Puzzle.Piece.SIDE_FORM_FLAT);
      }

      // Last piece in every row must have its right side flat,
      // other pieces have it curvy
      if (pieceNumber % PUZZLE_COLUMNS == PUZZLE_COLUMNS - 1) {
        assertEquals(piece.getSideForm(Puzzle.Piece.SIDE_RIGHT), Puzzle.Piece.SIDE_FORM_FLAT);
      } else {
        assertNotEquals(piece.getSideForm(Puzzle.Piece.SIDE_RIGHT), Puzzle.Piece.SIDE_FORM_FLAT);
      }

      // Every piece in the top row must have top side flat,
      // others have it curvy
      if (pieceNumber < PUZZLE_COLUMNS) {
        assertEquals(piece.getSideForm(Puzzle.Piece.SIDE_TOP), Puzzle.Piece.SIDE_FORM_FLAT);
      } else {
        assertNotEquals(piece.getSideForm(Puzzle.Piece.SIDE_TOP), Puzzle.Piece.SIDE_FORM_FLAT);
      }

      // Every piece in the bottom row must have top side flat,
      // others have it curvy
      if (piecesCount - PUZZLE_COLUMNS <= pieceNumber) {
        assertEquals(piece.getSideForm(Puzzle.Piece.SIDE_BOTTOM), Puzzle.Piece.SIDE_FORM_FLAT);
      } else {
        assertNotEquals(piece.getSideForm(Puzzle.Piece.SIDE_BOTTOM), Puzzle.Piece.SIDE_FORM_FLAT);
      }
    }
  }
}
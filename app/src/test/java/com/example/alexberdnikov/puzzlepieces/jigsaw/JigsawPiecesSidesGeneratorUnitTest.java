package com.example.alexberdnikov.puzzlepieces.jigsaw;

import com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece;
import com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesSidesGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JigsawPiecesSidesGeneratorUnitTest {
  private final int PUZZLE_COLUMNS = 50;
  private final int PUZZLE_ROWS = 20;

  private PiecesSidesGenerator piecesSidesGenerator;

  @Before
  public void createPuzzle() {
    piecesSidesGenerator = new PiecesSidesGenerator(PUZZLE_COLUMNS, PUZZLE_ROWS);
  }

  @Test
  public void piecesCountIsValid() {
    assertEquals(piecesSidesGenerator.getPiecesCount(), PUZZLE_COLUMNS * PUZZLE_ROWS);
  }

  @Test
  public void allPiecesInPlace() {
    for (int pieceNumber = 0; pieceNumber < piecesSidesGenerator.getPiecesCount(); pieceNumber++) {
      assertNotNull(piecesSidesGenerator.getSidesDescription(pieceNumber));
    }
  }

  @Test
  public void allPiecesHaveValidSideForms() {
    int piecesCount = piecesSidesGenerator.getPiecesCount();
    for (int pieceNumber = 0; pieceNumber < piecesCount; pieceNumber++) {

      JigsawPiece.SidesDescription sidesDescription = piecesSidesGenerator.getSidesDescription(pieceNumber);

      // First piece in every row must have left side flat,
      // all other pieces have their left sides curvy
      if (pieceNumber % PUZZLE_COLUMNS == 0) {
        assertEquals(sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_LEFT), JigsawPiece.SidesDescription.SIDE_FORM_FLAT);
      } else {
        assertNotEquals(sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_LEFT), JigsawPiece.SidesDescription.SIDE_FORM_FLAT);
      }

      // Last piece in every row must have its right side flat,
      // other pieces have it curvy
      if (pieceNumber % PUZZLE_COLUMNS == PUZZLE_COLUMNS - 1) {
        assertEquals(sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_RIGHT), JigsawPiece.SidesDescription.SIDE_FORM_FLAT);
      } else {
        assertNotEquals(sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_RIGHT), JigsawPiece.SidesDescription.SIDE_FORM_FLAT);
      }

      // Every piece in the top row must have top side flat,
      // others have it curvy
      if (pieceNumber < PUZZLE_COLUMNS) {
        assertEquals(sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_TOP), JigsawPiece.SidesDescription.SIDE_FORM_FLAT);
      } else {
        assertNotEquals(sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_TOP), JigsawPiece.SidesDescription.SIDE_FORM_FLAT);
      }

      // Every piece in the bottom row must have top side flat,
      // others have it curvy
      if (piecesCount - PUZZLE_COLUMNS <= pieceNumber) {
        assertEquals(sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_BOTTOM), JigsawPiece.SidesDescription.SIDE_FORM_FLAT);
      } else {
        assertNotEquals(sidesDescription.getSideForm(JigsawPiece.SidesDescription.SIDE_BOTTOM), JigsawPiece.SidesDescription.SIDE_FORM_FLAT);
      }
    }
  }
}
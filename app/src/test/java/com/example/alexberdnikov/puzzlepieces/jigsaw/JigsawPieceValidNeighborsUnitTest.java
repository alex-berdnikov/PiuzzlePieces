package com.example.alexberdnikov.puzzlepieces.jigsaw;

import android.graphics.Bitmap;
import com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece;
import com.example.alexberdnikov.puzzlepieces.view.jigsaw.PiecesSidesGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class JigsawPieceValidNeighborsUnitTest {
  private final int PUZZLE_COLUMNS = 10;
  private final int PUZZLE_ROWS = 30;
  private final int TOTAL_PIECES_COUNT = PUZZLE_COLUMNS * PUZZLE_ROWS;

  private List<JigsawPiece> pieces;

  @Mock
  private Bitmap mockedImage;

  @Before
  public void createPieces() {
    MockitoAnnotations.initMocks(this);

    PiecesSidesGenerator generator = new PiecesSidesGenerator(PUZZLE_COLUMNS, PUZZLE_ROWS);
    pieces = new ArrayList<>();

    for (int i = 0; i < generator.getPiecesCount(); i++) {
      JigsawPiece piece = new JigsawPiece(
          mockedImage,
          generator,
          i,
          0,
          0,
          0,
          0,
          0,
          0);

      pieces.add(piece);
    }
  }

  @Test
  public void checkIfTopRowDoesntHaveTopNeighbors() {
    for (int i = 0; i < PUZZLE_COLUMNS; i++) {
      JigsawPiece topLeftPiece = pieces.get(i);
      assertEquals(JigsawPiece.NEIGHBOR_NOT_AVAILABLE, topLeftPiece.getTopNeighborNumber());
    }
  }

  @Test
  public void checkIfBottomRowDoesntHaveBottomNeighbors() {
    int firstPieceInLastRow = (TOTAL_PIECES_COUNT - 1) - (PUZZLE_COLUMNS - 1);
    for (int i = firstPieceInLastRow; i < TOTAL_PIECES_COUNT; i++) {
      JigsawPiece topLeftPiece = pieces.get(i);
      assertEquals(JigsawPiece.NEIGHBOR_NOT_AVAILABLE, topLeftPiece.getBottomNeighborNumber());
    }
  }

  @Test
  public void checkIfLeftmostColumnDoesntHaveLeftNeighbors() {
    int firstPieceInLastRow = (TOTAL_PIECES_COUNT - 1) - (PUZZLE_COLUMNS - 1);
    for (int i = 0; i < firstPieceInLastRow; i += PUZZLE_COLUMNS) {
      JigsawPiece piece = pieces.get(i);
      assertEquals(JigsawPiece.NEIGHBOR_NOT_AVAILABLE, piece.getLeftNeighborNumber());
    }
  }

  @Test
  public void checkIfRightmostColumnDoesntHaveRightNeighbors() {
    int lastPieceInFirstRow = PUZZLE_COLUMNS - 1;
    for (int i = lastPieceInFirstRow; i < TOTAL_PIECES_COUNT - 1; i += PUZZLE_COLUMNS) {
      JigsawPiece piece = pieces.get(i);
      assertEquals(JigsawPiece.NEIGHBOR_NOT_AVAILABLE, piece.getRightNeighborNumber());
    }
  }

  @Test
  public void checkIfRowsHaveValidTopNeighbors() {
    int firstPieceInSecondRow = PUZZLE_COLUMNS;
    for (int i = firstPieceInSecondRow; i < TOTAL_PIECES_COUNT; i++) {
      JigsawPiece piece = pieces.get(i);
      assertEquals(i - PUZZLE_COLUMNS, piece.getTopNeighborNumber());
    }
  }

  @Test
  public void checkIfRowsHaveValidBottomNeighbors() {
    int firstPieceInLastRow = (TOTAL_PIECES_COUNT - 1) - (PUZZLE_COLUMNS - 1);
    for (int i = 0; i < firstPieceInLastRow; i++) {
      JigsawPiece piece = pieces.get(i);
      assertEquals(i + PUZZLE_COLUMNS, piece.getBottomNeighborNumber());
    }
  }

  @Test
  public void checkIfAllColumnsHaveValidRightNeighbors() {
    for (int i = 0; i < TOTAL_PIECES_COUNT; i++) {
      if (i % PUZZLE_COLUMNS == PUZZLE_COLUMNS - 1) {
        continue;
      }

      JigsawPiece piece = pieces.get(i);
      assertEquals(piece.getNumber() + 1, piece.getRightNeighborNumber());
    }
  }

  @Test
  public void checkIfAllColumnsHaveValidLeftNeighbors() {
    for (int i = 1; i < TOTAL_PIECES_COUNT; i++) {
      if (i % PUZZLE_COLUMNS == 0) {
        continue;
      }

      JigsawPiece piece = pieces.get(i);
      assertEquals(piece.getNumber() - 1, piece.getLeftNeighborNumber());
    }
  }
}

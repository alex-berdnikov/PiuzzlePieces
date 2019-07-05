package com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_BOTTOM;
import static com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE;
import static com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_CONVEX;
import static com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_FLAT;
import static com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_LEFT;
import static com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_RIGHT;
import static com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_TOP;


public class PiecesSidesGenerator {
  private final int puzzlePiecesCount;
  private final int puzzleColumnsCount;
  private final int puzzleRowsCount;

  private List<JigsawPiece.SidesDescription> sidesDescriptions;

  public PiecesSidesGenerator(int columnsCount, int rowsCount) {
    if (columnsCount < 1 || rowsCount < 1) {
      throw new IllegalArgumentException("Puzzle must have at least on piece.");
    }

    puzzleColumnsCount = columnsCount;
    puzzleRowsCount = rowsCount;
    puzzlePiecesCount = columnsCount * rowsCount;

    generatePiecesSides();
  }

  private void generatePiecesSides() {
    sidesDescriptions = new ArrayList<JigsawPiece.SidesDescription>(puzzlePiecesCount) {{
      for (int i = 0; i < puzzlePiecesCount; i++) {
        add(new JigsawPiece.SidesDescription());
      }
    }};

    generateLeftAndRightSides();
    generateTopAndBottomSides();
  }

  private int getRandomSideType() {
    return new Random().nextInt(2) + 1;
  }

  private void generateLeftAndRightSides() {
    for (int pieceNumber = 0; pieceNumber < puzzlePiecesCount; pieceNumber++) {
      boolean isFirstPieceInRow = false;
      boolean isLastPieceInRow = false;

      JigsawPiece.SidesDescription sidesDescription = sidesDescriptions.get(pieceNumber);
      if (sidesDescription == null) {
        sidesDescription = new JigsawPiece.SidesDescription();
      }

      if (pieceNumber % puzzleColumnsCount == 0) {
        isFirstPieceInRow = true;
      } else if (pieceNumber % puzzleColumnsCount == puzzleColumnsCount - 1) {
        isLastPieceInRow = true;
      }

      if (isFirstPieceInRow) {
        sidesDescription.pieceSidesForms[SIDE_LEFT] = SIDE_FORM_FLAT;
      } else {
        sidesDescription.pieceSidesForms[SIDE_LEFT] =
            (sidesDescriptions.get(pieceNumber - 1).getSideForm(SIDE_RIGHT) == SIDE_FORM_CONCAVE)
                ? SIDE_FORM_CONVEX : SIDE_FORM_CONCAVE;
      }

      sidesDescription.pieceSidesForms[SIDE_RIGHT] = isLastPieceInRow ? SIDE_FORM_FLAT : getRandomSideType();
      sidesDescriptions.set(pieceNumber, sidesDescription);
    }
  }

  private void generateTopAndBottomSides() {
    for (int pieceNumber = 0; pieceNumber < puzzlePiecesCount; pieceNumber++) {
      boolean isFirstPieceInColumn = false;
      boolean isLastPieceInColumn = false;

      JigsawPiece.SidesDescription sidesDescription = sidesDescriptions.get(pieceNumber);
      if (sidesDescription == null) {
        sidesDescription = new JigsawPiece.SidesDescription();
      }

      if (pieceNumber < puzzleColumnsCount) {
        isFirstPieceInColumn = true;
      } else if (sidesDescriptions.size() - puzzleColumnsCount <= pieceNumber) {
        isLastPieceInColumn = true;
      }

      if (isFirstPieceInColumn) {
        sidesDescription.pieceSidesForms[SIDE_TOP] = SIDE_FORM_FLAT;
      } else {
        int upperPieceBottomSide =
            sidesDescriptions.get(pieceNumber - puzzleColumnsCount).getSideForm(SIDE_BOTTOM);
        sidesDescription.pieceSidesForms[SIDE_TOP] = (upperPieceBottomSide == SIDE_FORM_CONCAVE)
            ? SIDE_FORM_CONVEX : SIDE_FORM_CONCAVE;
      }

      if (isLastPieceInColumn) {
        sidesDescription.pieceSidesForms[SIDE_BOTTOM] = SIDE_FORM_FLAT;
      } else {
        sidesDescription.pieceSidesForms[SIDE_BOTTOM] = getRandomSideType();
      }

      sidesDescriptions.set(pieceNumber, sidesDescription);
    }
  }

  public int getPuzzleColumnsCount() {
    return puzzleColumnsCount;
  }

  public int getPuzzleRowsCount() {
    return puzzleRowsCount;
  }

  public JigsawPiece.SidesDescription getSidesDescription(int pieceNumber) {
    return sidesDescriptions.get(pieceNumber);
  }

  public int getPiecesCount() {
    return puzzlePiecesCount;
  }
}

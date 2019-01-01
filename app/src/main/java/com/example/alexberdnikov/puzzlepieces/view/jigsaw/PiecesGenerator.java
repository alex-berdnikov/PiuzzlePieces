package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_BOTTOM;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_CONVEX;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_FLAT;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_LEFT;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_RIGHT;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_TOP;


public class PiecesGenerator {
  private List<JigsawPiece.SidesDescription> sidesDescriptions;
  private final int puzzlePiecesCount;
  private final int puzzleColumnsCount;
  private final int puzzleRowsCount;

  public PiecesGenerator(int columnsCount, int rowsCount) {
    puzzleColumnsCount = columnsCount;
    puzzleRowsCount = rowsCount;
    puzzlePiecesCount = columnsCount * rowsCount;
    generatePuzzle();
  }

  private void generatePuzzle() {
    sidesDescriptions = new ArrayList<JigsawPiece.SidesDescription>(puzzlePiecesCount) {{
      for (int i = 0; i < puzzlePiecesCount; i++) {
        add(new JigsawPiece.SidesDescription());
      }
    }};

    generateLeftAndRightSides();
    generateTopAndBottomSides();
  }

  private int getRandomSideCurve() {
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

      sidesDescription.pieceSidesForms[SIDE_RIGHT] = isLastPieceInRow ? SIDE_FORM_FLAT : getRandomSideCurve();
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
        sidesDescription.pieceSidesForms[SIDE_BOTTOM] = getRandomSideCurve();
      }

      sidesDescriptions.set(pieceNumber, sidesDescription);
    }
  }

  int getPuzzleColumnsCount() {
    return puzzleColumnsCount;
  }

  int getPuzzleRowsCount() {
    return puzzleRowsCount;
  }

  public JigsawPiece.SidesDescription getSidesDescription(int pieceNumber) {
    return sidesDescriptions.get(pieceNumber);
  }

  public int getPiecesCount() {
    return puzzlePiecesCount;
  }
}

package com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw;

import com.afterglowgames.alexberdnikov.puzzlepieces.view.Piece;
import com.afterglowgames.alexberdnikov.puzzlepieces.view.PiecesGroup;

public class JigsawPiecesGroup extends PiecesGroup {

  private JigsawPiece leftMostPiece;
  private JigsawPiece topMostPiece;
  private JigsawPiece rightMostPiece;
  private JigsawPiece bottomMostPiece;

  JigsawPiecesGroup(JigsawPiece piece) {
    super(piece);
    leftMostPiece = topMostPiece = rightMostPiece = bottomMostPiece = piece;
  }

  @Override public void alignGroupByPiece(Piece piece) {
    if (!getPieces().contains(piece)) {
      throw new IllegalStateException("Provided piece doesn't belong to this group.");
    }

    JigsawPiece pieceToAlignWith = (JigsawPiece) piece;
    int[] pieceOffset = pieceToAlignWith.getPieceOffsetInPuzzle();
    int pieceInPuzzleOffsetX = pieceOffset[0];
    int pieceInPuzzleOffsetY = pieceOffset[1];

    int entirePuzzleOffsetX = pieceToAlignWith.getX() - pieceInPuzzleOffsetX;
    int entirePuzzleOffsetY = pieceToAlignWith.getY() - pieceInPuzzleOffsetY;

    for (Piece groupPiece : getPieces()) {
      JigsawPiece jigsawPiece = (JigsawPiece) groupPiece;
      if (pieceToAlignWith == jigsawPiece) {
        continue;
      }

      pieceOffset = jigsawPiece.getPieceOffsetInPuzzle();
      pieceInPuzzleOffsetX = pieceOffset[0];
      pieceInPuzzleOffsetY = pieceOffset[1];

      jigsawPiece.setX(pieceInPuzzleOffsetX + entirePuzzleOffsetX);
      jigsawPiece.setY(pieceInPuzzleOffsetY + entirePuzzleOffsetY);
    }
  }

  @Override public void mergeWith(PiecesGroup piecesGroup) {
    super.mergeWith(piecesGroup);
    setLeftMostPiece();
    setTopMostPiece();
    setRightMostPiece();
    setBottomMostPiece();
  }

  private Piece getPieceByNumber(int number) {
    for (Piece piece : getPieces()) {
      if (piece.getNumber() == number) {
        return piece;
      }
    }
    throw new IllegalArgumentException(String.format("There's no piece with number %d", number));
  }

  private void setTopMostPiece() {
    topMostPiece = (JigsawPiece) getPieces().get(0);
  }

  private void setLeftMostPiece() {
    JigsawPiece justPiece = (JigsawPiece) getPieces().get(0);
    this.leftMostPiece = justPiece;
    int leftMostPieceNumberInRow = justPiece.getNumber() % justPiece.puzzleColumnsCount;
    for (Piece piece : getPieces()) {
      int pieceNumberInRow = piece.getNumber() % justPiece.puzzleColumnsCount;
      if (pieceNumberInRow < leftMostPieceNumberInRow) {
        leftMostPieceNumberInRow = pieceNumberInRow;
        this.leftMostPiece = (JigsawPiece) piece;
      }
    }
  }

  private void setRightMostPiece() {
    JigsawPiece justPiece = (JigsawPiece) getPieces().get(0);
    this.rightMostPiece = justPiece;
    int rightMostPieceNumberInRow = justPiece.getNumber() % justPiece.puzzleColumnsCount;

    for (Piece piece : getPieces()) {
      int pieceNumberInRow = piece.getNumber() % justPiece.puzzleColumnsCount;
      if (rightMostPieceNumberInRow < pieceNumberInRow) {
        rightMostPieceNumberInRow = pieceNumberInRow;
        this.rightMostPiece = (JigsawPiece) piece;
      }
    }
  }

  private void setBottomMostPiece() {
    bottomMostPiece = (JigsawPiece) getPieces().get(getPieces().size() - 1);
  }

  @Override public JigsawPiece getTopMostPiece() {
    return topMostPiece;
  }

  @Override public JigsawPiece getLeftMostPiece() {
    return leftMostPiece;
  }

  @Override public JigsawPiece getRightMostPiece() {
    return rightMostPiece;
  }

  @Override public JigsawPiece getBottomMostPiece() {
    return bottomMostPiece;
  }
}

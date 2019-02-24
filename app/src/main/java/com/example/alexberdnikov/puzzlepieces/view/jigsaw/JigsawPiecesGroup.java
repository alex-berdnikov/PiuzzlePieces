package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import android.graphics.Point;
import com.example.alexberdnikov.puzzlepieces.view.Piece;
import com.example.alexberdnikov.puzzlepieces.view.PiecesGroup;
import timber.log.Timber;

public class JigsawPiecesGroup extends PiecesGroup {

  JigsawPiecesGroup(JigsawPiece piece) {
    super(piece);
  }

  @Override public void alignGroupByPiece(Piece piece) {
    if (!getPieces().contains(piece)) {
      throw new IllegalStateException("Provided piece doesn't belong to this group.");
    }

    JigsawPiece pieceToAlignWith = (JigsawPiece) piece;
    int[] pieceOffset = getPieceOffsetInPuzzle(pieceToAlignWith);
    int pieceInPuzzleOffsetX = pieceOffset[0];
    int pieceInPuzzleOffsetY = pieceOffset[1];

    int entirePuzzleOffsetX = piece.getX() - pieceInPuzzleOffsetX;
    int entirePuzzleOffsetY = piece.getY() - pieceInPuzzleOffsetY;

    for (Piece groupPiece : getPieces()) {
      JigsawPiece jigsawPiece = (JigsawPiece) groupPiece;
      if (pieceToAlignWith == jigsawPiece) {
        continue;
      }

      pieceOffset = getPieceOffsetInPuzzle(jigsawPiece);
      pieceInPuzzleOffsetX = pieceOffset[0];
      pieceInPuzzleOffsetY = pieceOffset[1];

      jigsawPiece.setX(pieceInPuzzleOffsetX + entirePuzzleOffsetX);
      jigsawPiece.setY(pieceInPuzzleOffsetY + entirePuzzleOffsetY);
    }
  }

  private int[] getPieceOffsetInPuzzle(JigsawPiece piece) {
    int pieceNumberInRow = piece.getNumber() % piece.puzzleColumnsCount;
    int pieceInPuzzleOffsetX = piece.getSquareWidth() * pieceNumberInRow;
    if (piece.getLeftSideDescription() == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      pieceInPuzzleOffsetX -= JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    int pieceNumberInColumn = piece.getNumber() / piece.puzzleColumnsCount;
    int pieceInPuzzleOffsetY = piece.getSquareWidth() * pieceNumberInColumn;

    if (piece.getTopSideDescription() == JigsawPiece.SidesDescription.SIDE_FORM_CONVEX) {
      pieceInPuzzleOffsetY -= JigsawPiece.PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    return new int[] {pieceInPuzzleOffsetX, pieceInPuzzleOffsetY};
  }

  @Override public JigsawPiece getTopMostPiece() {
    //  getPieces()
    return null;
  }

  @Override public JigsawPiece getLeftMostPiece() {
    Timber.d("---------- +++++ ---------");
    for (Piece piece : getPieces()) {
      Timber.d("------- piece: %s", piece);
    }
    return null;
  }

  @Override public Piece getRightMostPiece() {
    return null;
  }

  @Override public Piece getBottomMostPiece() {
    return null;
  }

  @Override public int getWidth() {
    return 0;
  }

  @Override public int getHeight() {
    return 0;
  }
}

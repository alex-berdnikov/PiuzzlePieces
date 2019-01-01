package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import com.example.alexberdnikov.puzzlepieces.view.Piece;
import com.example.alexberdnikov.puzzlepieces.view.PiecesPicker;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class JigsawPiecesPicker extends PiecesPicker {
  private List<Set<JigsawPiece>> connectedPiecesBlocks = new LinkedList<>();

  JigsawPiecesPicker(List<Piece> pieces, int screenWidth, int screenHeight) {
    super(pieces, screenWidth, screenHeight);
    for (final Piece piece : pieces) {
      connectedPiecesBlocks.add(new HashSet<JigsawPiece>() {{
        add(new JigsawPiece(piece));
      }});
    }
  }

  protected Piece getCapturedPieceFromCoordinates(float touchX, float touchY) {
    for (Piece piece : getPieces()) {
      if (isTouchInPieceBounds(piece, touchX, touchY)) {
        return piece;
      }
    }
    return null;
  }

  private boolean isTouchInPieceBounds(Piece piece, float touchX, float touchY) {
    return piece.getX() <= touchX && touchX <= piece.getX() + piece.getPieceWidth()
        && piece.getY() <= touchY && touchY <= piece.getY() + piece.getPieceHeight();
  }

  @Override protected void handlePiecesConnections(Piece draggedPiece) {
    //Set<Piece> draggedPieceGroup = getGroupWithPiece(draggedPiece);
    //handleConnectionForPieces(draggedPieceGroup);
  }

  private void handleConnectionForPieces(Set<Piece> pieces) {

  }

  private Set<JigsawPiece> getGroupWithPiece(JigsawPiece piece) {
    for (Set<JigsawPiece> group : connectedPiecesBlocks) {
      if (group.contains(piece)) {
        return group;
      }
    }
    throw new IllegalStateException("Piece must belong to some group.");
  }
}
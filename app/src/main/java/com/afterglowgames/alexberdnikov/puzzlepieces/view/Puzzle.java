package com.afterglowgames.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import com.afterglowgames.alexberdnikov.puzzlepieces.utils.ScreenUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Puzzle {
  private Bitmap imageBitmap;
  private Size puzzleAreaSize;
  private Size screenSize;

  private List<Piece> pieces = new LinkedList<>();
  private Map<Integer, Bitmap> piecesShadows = new HashMap<>();
  private PiecesPicker piecesPicker;

  public Puzzle(Context context) {
    calculateAndSetupPuzzleArea(context);
  }

  private void calculateAndSetupPuzzleArea(Context context) {
    this.screenSize = ScreenUtils.getScreenSize(context);
    //puzzleAreaSize = new Size(Math.round(screenSize.getWidth() * 0.8f),
    //    Math.round(screenSize.getHeight() * 0.8f));
    puzzleAreaSize = new Size(1000, 1000);
  }

  protected Bitmap getImageBitmap() {
    return imageBitmap;
  }

  protected Size getPuzzleAreaSize() {
    return puzzleAreaSize;
  }

  void setImageBitmap(Bitmap imageBitmap) {
    this.imageBitmap = imageBitmap;
  }

  protected List<Piece> getPieces() {
    return pieces;
  }

  protected Map<Integer, Bitmap> getShadows() {
    return piecesShadows;
  }

  void onTouchStart(float x, float y) {
    if (piecesPicker != null) {
      piecesPicker.onTouchStart(x, y);
    }
  }

  void onMove(float x, float y) {
    if (piecesPicker != null) {
      synchronized (DrawThread.lock) {
        piecesPicker.onMove(x, y);
      }
    }
  }

  void onTouchEnd(float x, float y) {
    if (piecesPicker != null) {
      piecesPicker.onTouchEnd(x, y);
    }
  }

  protected void onGenerated() {
    piecesPicker = createPiecesPicker(screenSize.getWidth(), screenSize.getHeight());
    createPiecesShadows();
  }

  private void createPiecesShadows() {
    List<PiecesGroup> piecesGroups = piecesPicker.getPiecesGroups();
    for (int i = 0; i < piecesGroups.size(); i++) {
      PiecesGroup group = piecesGroups.get(i);
      getShadows().put(group.getPieces().get(0).getNumber(),
          createPiecesGroupShadow(piecesGroups.get(i)));
    }
  }

  protected PiecesPicker getPiecesPicker() {
    return piecesPicker;
  }

  abstract public void generate();

  abstract protected Piece createPiece(int number, int x, int y);

  abstract protected PiecesPicker createPiecesPicker(int screenWidth, int screenHeight);

  abstract protected Bitmap createPiecesGroupShadow(PiecesGroup piecesGroup);
}

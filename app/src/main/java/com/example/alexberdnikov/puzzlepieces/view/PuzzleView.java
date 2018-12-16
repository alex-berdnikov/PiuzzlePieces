package com.example.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import com.example.alexberdnikov.puzzlepieces.R;
import com.example.alexberdnikov.puzzlepieces.utils.ScreenUtils;
import timber.log.Timber;

public class PuzzleView extends View {

  private int pieceSquareWidth;
  private int pieceSquareHeight;
  private int pieceConvexConcaveCubicWidth = 16;
  private int pieceConvexConcaveCubicHeight = 24;
  
  private Puzzle puzzle;
  private Bitmap imageBitmap;
  private Paint piecePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Size puzzleAreaSize;

  public PuzzleView(Context context) {
    super(context);
  }

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public void setupPuzzle() {
    puzzle = new Puzzle(16, 9);
    calculateAndSetPuzzleArea();
    definePieceSquareSize();
    loadBitmap();
  }

  protected void setImageBitmap(Bitmap bitmap) {
    imageBitmap = bitmap;
    definePieceSquareSize();
  }
  
  private void definePieceSquareSize() {
    pieceSquareWidth = puzzleAreaSize.getWidth() / puzzle.getPuzzleColumnsCount();
    pieceSquareHeight = puzzleAreaSize.getHeight() / puzzle.getPuzzleRowsCount();
  }

  protected void calculateAndSetPuzzleArea() {
    Size screenSize = ScreenUtils.getScreenSize(getContext());
    puzzleAreaSize = new Size(Math.round(screenSize.getWidth() * 0.8f),
        Math.round(screenSize.getHeight() * 0.8f));
  }

  protected Size getPuzzleAreaSize() {
    return puzzleAreaSize;
  }

  private void loadBitmap() {
    new LoadBitmapTask(getResources(), this).execute(R.drawable.autumn_1);
  }

  private Bitmap createPieceFromNumber(int pieceNumber) {
    Path piecePath = createPiecePath(pieceNumber);
    Size pieceSize = calculatePieceBitmapSize(pieceNumber);

    Bitmap cutPieceBitmap =
        Bitmap.createBitmap(pieceSize.getWidth(), pieceSize.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas pieceCanvas = new Canvas(cutPieceBitmap);

    piecePaint.setColor(0xFF000000);
    pieceCanvas.drawPath(piecePath, piecePaint);
    piecePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));


    Point pieceImageCoordinates = calculatePieceImageCoordinates(pieceNumber);
    Timber.d("----------imageBitmap width: %d, pieceNumber: %d, left side: %s, pieceImageCoordinates.x: %d, pieceSize.getHeight(): %d, pieceSize.getWidth(): %d",
        imageBitmap.getWidth(), pieceNumber, puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_LEFT), pieceImageCoordinates.x, pieceSize.getHeight(), pieceSize.getWidth());
    Bitmap pieceBitmap = Bitmap.createBitmap(
        imageBitmap,
        pieceImageCoordinates.x,
        pieceImageCoordinates.y,
        pieceSize.getWidth(),
        pieceSize.getHeight());

    pieceCanvas.drawBitmap(pieceBitmap, 0, 0, piecePaint);
    piecePaint.setXfermode(null);

    return cutPieceBitmap;
  }

  private Size calculatePieceBitmapSize(int pieceNumber) {
    Puzzle.Piece piece = puzzle.getPiece(pieceNumber);
    int pieceWidth = pieceSquareWidth;
    int pieceHeight = pieceSquareHeight;

    if (piece.getSideForm(Puzzle.Piece.SIDE_TOP) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      pieceHeight += pieceConvexConcaveCubicHeight;
    }

    if (piece.getSideForm(Puzzle.Piece.SIDE_BOTTOM) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      pieceHeight += pieceConvexConcaveCubicHeight;
    }

    if (piece.getSideForm(Puzzle.Piece.SIDE_LEFT) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      pieceWidth += pieceConvexConcaveCubicHeight;
    }

    if (piece.getSideForm(Puzzle.Piece.SIDE_RIGHT) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      pieceWidth += pieceConvexConcaveCubicHeight;
    }

    return new Size(pieceWidth, pieceHeight);
  }

  private Point calculatePieceImageCoordinates(int pieceNumber) {
    Puzzle.Piece piece = puzzle.getPiece(pieceNumber);

    int pieceNumberInRow = pieceNumber % puzzle.getPuzzleColumnsCount();
    int imageLeft = pieceSquareWidth * pieceNumberInRow;
    if (pieceNumberInRow != 0
        && piece.getSideForm(Puzzle.Piece.SIDE_LEFT) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      imageLeft -= pieceConvexConcaveCubicHeight;
    }

    int pieceNumberInColumn = pieceNumber / puzzle.getPuzzleColumnsCount();
    int imageTop = pieceSquareHeight * pieceNumberInColumn;
    if (pieceNumberInColumn != 0
        && piece.getSideForm(Puzzle.Piece.SIDE_TOP) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      imageTop -= pieceConvexConcaveCubicHeight;
    }

    return new Point(imageLeft, imageTop);
  }

  private Path createPiecePath(int pieceNumber) {
    Path pieceFormPath = new Path();
    final int START_X = 0;
    final int START_Y = 1;

    int[] pathStartPoint = setPathStartPoint(pieceFormPath, pieceNumber);
    pieceFormPath.moveTo(pathStartPoint[START_X], pathStartPoint[START_Y]);
    drawTopSide(pieceFormPath, pieceNumber, pathStartPoint[START_X], pathStartPoint[START_Y]);
    drawRightSide(pieceFormPath, pieceNumber, pathStartPoint[START_X], pathStartPoint[START_Y]);
    drawBottomSide(pieceFormPath, pieceNumber, pathStartPoint[START_X], pathStartPoint[START_Y]);
    drawLeftSide(pieceFormPath, pieceNumber, pathStartPoint[START_X], pathStartPoint[START_Y]);
    pieceFormPath.close();

    return pieceFormPath;
  }

  private int[] setPathStartPoint(Path path, int pieceNumber) {
    final int leftSide = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_LEFT);
    final int startX;
    switch (leftSide) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        startX = 0;
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        startX = 0;
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        startX = pieceConvexConcaveCubicHeight;
        break;
      default:
        throw new IllegalStateException("Piece side form has undefined value.");
    }

    final int topSideForm = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_TOP);
    final int startY;
    switch (topSideForm) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        startY = 0;
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        startY = 0;
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        startY = pieceConvexConcaveCubicHeight;
        break;
      default:
        throw new IllegalStateException("Piece side form has undefined value.");
    }

    path.moveTo(startX, startY);
    return new int[] { startX, startY };
  }

  /**
   * Draws top side of a piece. Must be called first in a series of methods drawing piece sides.
   *
   * @param path path to draw
   */
  private void drawTopSide(Path path, int pieceNumber, int startX, int startY) {
    final int TOP_SIDE_FORM = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_TOP);
    switch (TOP_SIDE_FORM) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        path.lineTo(startX + pieceSquareWidth, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + pieceSquareWidth / 3, startY);
        path.cubicTo(startX + (pieceSquareWidth / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceConvexConcaveCubicHeight,
            startX + ((pieceSquareWidth / 3) * 2) + pieceConvexConcaveCubicWidth,
            startY + pieceConvexConcaveCubicHeight,
            startX + ((pieceSquareWidth / 3) * 2), startY);
        path.lineTo(startX + pieceSquareWidth, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + pieceSquareWidth / 3, startY);
        path.cubicTo(startX + (pieceSquareWidth / 3) - pieceConvexConcaveCubicHeight, 0,
            startX + ((pieceSquareWidth / 3) * 2) + pieceConvexConcaveCubicHeight, 0,
            startX + ((pieceSquareWidth / 3) * 2), startY);
        path.lineTo(startX + pieceSquareWidth, startY);
        break;
    }
  }

  /**
   * Must be called after {@link #drawTopSide(Path, int, int, int)} and before
   * {@link #drawBottomSide(Path, int, int, int)}.
   */
  private void drawRightSide(Path path, int pieceNumber, int startX, int startY) {
    final int RIGHT_SIDE = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_RIGHT);
    switch (RIGHT_SIDE) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareWidth);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareWidth / 3);
        path.cubicTo(
            startX + pieceSquareWidth - pieceConvexConcaveCubicHeight,
            startY + (pieceSquareWidth / 3) - pieceConvexConcaveCubicWidth,

            startX + pieceSquareWidth - pieceConvexConcaveCubicHeight,
            startY + (pieceSquareWidth / 3) * 2 + pieceConvexConcaveCubicWidth,

            startX + pieceSquareWidth,
            startY + (pieceSquareWidth / 3) * 2);
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareWidth);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareWidth / 3);
        path.cubicTo(
            startX + pieceSquareWidth + pieceConvexConcaveCubicHeight,
            startY + (pieceSquareWidth / 3) - pieceConvexConcaveCubicWidth,

            startX + pieceSquareWidth + pieceConvexConcaveCubicHeight,
            startY + (pieceSquareWidth / 3) * 2 + pieceConvexConcaveCubicWidth,

            startX + pieceSquareWidth,
            startY + (pieceSquareWidth / 3) * 2);
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareWidth);
        break;
    }
  }

  /**
   * Must be called after {@link #drawRightSide(Path, int, int, int)} and before
   * {@link #drawLeftSide(Path, int, int, int)}.
   */
  private void drawBottomSide(Path path, int pieceNumber, int startX, int startY) {
    final int BOTTOM_SIDE = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_BOTTOM);
    switch (BOTTOM_SIDE) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        path.lineTo(startX, startY + pieceSquareWidth);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + (pieceSquareWidth / 3) * 2, startY + pieceSquareWidth);
        path.cubicTo(
            startX + (pieceSquareWidth / 3) * 2 + pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight - pieceConvexConcaveCubicHeight,

            startX + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight - pieceConvexConcaveCubicHeight,

            startX + pieceSquareWidth / 3,
            startY + pieceSquareHeight);
        path.lineTo(startX, startY + pieceSquareWidth);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + (pieceSquareWidth / 3) * 2, startY + pieceSquareWidth);
        path.cubicTo(
            startX + (pieceSquareWidth / 3) * 2 + pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight + pieceConvexConcaveCubicHeight,

            startX + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight + pieceConvexConcaveCubicHeight,

            startX + pieceSquareWidth / 3,
            startY + pieceSquareHeight);
        path.lineTo(startX, startY + pieceSquareWidth);
        break;
    }
  }

  /**
   * Must be called last, after {@link #drawBottomSide(Path, int, int, int)},
   * in a series of methods drawing piece sides.
   */
  private void drawLeftSide(Path path, int pieceNumber, int startX, int startY) {
    final int LEFT_SIDE = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_LEFT);
    switch (LEFT_SIDE) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        path.lineTo(startX, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX, startY + (pieceSquareHeight / 3) * 2);
        path.cubicTo(
            startX + pieceConvexConcaveCubicHeight,
            startY + ((pieceSquareHeight / 3) * 2) + pieceConvexConcaveCubicWidth,

            startX + pieceConvexConcaveCubicHeight,
            startY + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,

            startX,
            startY + (pieceSquareHeight / 3));
        path.lineTo(startX, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX, startY + (pieceSquareHeight / 3) * 2);
        path.cubicTo(
            startX - pieceConvexConcaveCubicHeight,
            startY + ((pieceSquareHeight / 3) * 2) + pieceConvexConcaveCubicWidth,

            startX - pieceConvexConcaveCubicHeight,
            startY + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,

            startX,
            startY + (pieceSquareHeight / 3));
        path.lineTo(startX, startY);
        break;
    }
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (imageBitmap == null) {
      return;
    }


    for (int i = 0; i < puzzle.getPiecesCount(); i++) {
      canvas.drawBitmap(createPieceFromNumber(i),
          (i % 16 ) * (pieceSquareWidth + 18) + 40,
          (i / 16) * (pieceSquareHeight + 18) + 40,
          null);
    }
  }
}

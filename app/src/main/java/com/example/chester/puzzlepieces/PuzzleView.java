package com.example.chester.puzzlepieces;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import com.example.chester.puzzlepieces.utils.ScreenUtils;
import java.lang.ref.WeakReference;
import timber.log.Timber;

public class PuzzleView extends View {

  private int pieceScreenWidth;
  private int pieceSquareHeight;
  private int pieceConvexConcaveCubicWidth = 16;
  private int pieceConvexConcaveCubicHeight = 24;
  
  private Puzzle puzzle;
  private Bitmap imageBitmap;
  private Paint piecePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public PuzzleView(Context context) {
    super(context);
  }

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    setupPuzzle();
  }

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setupPuzzle();
  }

  public PuzzleView(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setupPuzzle();
  }

  private void setupPuzzle() {
    puzzle = new Puzzle(16, 9);
    loadBitmap();
  }

  private void setImageBitmap(Bitmap bitmap) {
    imageBitmap = bitmap;
    definePieceSquareSize();
  }
  
  private void definePieceSquareSize() {
    pieceScreenWidth = imageBitmap.getWidth() / puzzle.getPuzzleColumnsCount();
    pieceSquareHeight = imageBitmap.getHeight() / puzzle.getPuzzleRowsCount();
  }

  protected Size getPuzzleAreSize() {
    Size screenSize = ScreenUtils.getScreenSize(getContext());
    return new Size(Math.round(screenSize.getWidth() * 0.7f),
        Math.round(screenSize.getHeight() * 0.7f));
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
    int pieceWidth = pieceScreenWidth;
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
    int imageLeft = pieceScreenWidth * pieceNumberInRow;
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
        path.lineTo(startX + pieceScreenWidth, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + pieceScreenWidth / 3, startY);
        path.cubicTo(startX + (pieceScreenWidth / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceConvexConcaveCubicHeight,
            startX + ((pieceScreenWidth / 3) * 2) + pieceConvexConcaveCubicWidth,
            startY + pieceConvexConcaveCubicHeight,
            startX + ((pieceScreenWidth / 3) * 2), startY);
        path.lineTo(startX + pieceScreenWidth, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + pieceScreenWidth / 3, startY);
        path.cubicTo(startX + (pieceScreenWidth / 3) - pieceConvexConcaveCubicHeight, 0,
            startX + ((pieceScreenWidth / 3) * 2) + pieceConvexConcaveCubicHeight, 0,
            startX + ((pieceScreenWidth / 3) * 2), startY);
        path.lineTo(startX + pieceScreenWidth, startY);
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
        path.lineTo(startX + pieceScreenWidth, startY + pieceScreenWidth);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + pieceScreenWidth, startY + pieceScreenWidth / 3);
        path.cubicTo(
            startX + pieceScreenWidth - pieceConvexConcaveCubicHeight,
            startY + (pieceScreenWidth / 3) - pieceConvexConcaveCubicWidth,

            startX + pieceScreenWidth - pieceConvexConcaveCubicHeight,
            startY + (pieceScreenWidth / 3) * 2 + pieceConvexConcaveCubicWidth,

            startX + pieceScreenWidth,
            startY + (pieceScreenWidth / 3) * 2);
        path.lineTo(startX + pieceScreenWidth, startY + pieceScreenWidth);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + pieceScreenWidth, startY + pieceScreenWidth / 3);
        path.cubicTo(
            startX + pieceScreenWidth + pieceConvexConcaveCubicHeight,
            startY + (pieceScreenWidth / 3) - pieceConvexConcaveCubicWidth,

            startX + pieceScreenWidth + pieceConvexConcaveCubicHeight,
            startY + (pieceScreenWidth / 3) * 2 + pieceConvexConcaveCubicWidth,

            startX + pieceScreenWidth,
            startY + (pieceScreenWidth / 3) * 2);
        path.lineTo(startX + pieceScreenWidth, startY + pieceScreenWidth);
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
        path.lineTo(startX, startY + pieceScreenWidth);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + (pieceScreenWidth / 3) * 2, startY + pieceScreenWidth);
        path.cubicTo(
            startX + (pieceScreenWidth / 3) * 2 + pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight - pieceConvexConcaveCubicHeight,

            startX + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight - pieceConvexConcaveCubicHeight,

            startX + pieceScreenWidth / 3,
            startY + pieceSquareHeight);
        path.lineTo(startX, startY + pieceScreenWidth);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + (pieceScreenWidth / 3) * 2, startY + pieceScreenWidth);
        path.cubicTo(
            startX + (pieceScreenWidth / 3) * 2 + pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight + pieceConvexConcaveCubicHeight,

            startX + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight + pieceConvexConcaveCubicHeight,

            startX + pieceScreenWidth / 3,
            startY + pieceSquareHeight);
        path.lineTo(startX, startY + pieceScreenWidth);
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

    Timber.d("------------ onDraw()");

    canvas.drawBitmap(createPieceFromNumber(0), 150, 10, null);
    canvas.drawBitmap(createPieceFromNumber(16), 150, 96, null);
    canvas.drawBitmap(createPieceFromNumber(32), 150, 214, null);
    canvas.drawBitmap(createPieceFromNumber(48), 150, 314, null);
  }

  private static class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
    private Resources resources;
    private WeakReference<PuzzleView> puzzleViewWeakReference;

    LoadBitmapTask(Resources resources, PuzzleView puzzleView) {
      this.resources = resources;
      this.puzzleViewWeakReference = new WeakReference<>(puzzleView);
    }

    @Override protected Bitmap doInBackground(Integer... bitmapResource) {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeResource(resources, bitmapResource[0], options);

      if (puzzleViewWeakReference.get() == null) {
        return null;
      }

      Size puzzleAreaSize = puzzleViewWeakReference.get().getPuzzleAreSize();
      options.inSampleSize = ScreenUtils.calculateInSampleSize(
          options, puzzleAreaSize.getWidth(), puzzleAreaSize.getHeight());
      options.inJustDecodeBounds = false;

      return BitmapFactory.decodeResource(resources, bitmapResource[0], options);
    }

    @Override protected void onPostExecute(Bitmap bitmap) {
      if (bitmap == null) {
        Timber.e("Bitmap is null.");
        return;
      }

      if (puzzleViewWeakReference.get() == null) {
        Timber.e("PuzzleView instance is null.");
        return;
      }

      PuzzleView puzzleView = puzzleViewWeakReference.get();
      puzzleView.setImageBitmap(bitmap);
      puzzleView.invalidate();
    }
  }
}

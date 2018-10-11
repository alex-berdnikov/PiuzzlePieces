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
import java.lang.ref.WeakReference;
import timber.log.Timber;

public class PuzzleView extends View {

  final int PIECE_MIN_SQUARE_WIDTH = 125;
  final int PIECE_MIN_SQUARE_HEIGHT = 125;
  final int PIECE_CONVEX_CONCAVE_CUBIC_WIDTH = 16;
  final int PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT = 24;

  private Puzzle puzzle;
  private Bitmap imageBitmap;
  private Paint piecePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private WeakReference<Bitmap> bitmapWeakReference;

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
    loadBitmap();

    //imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
    puzzle = new Puzzle(10, 5);
  }

  private void loadBitmap() {
    new LoadBitmapTask(getResources(), this).execute(R.drawable.image);
  }

  private Bitmap createPieceFromNumber(int pieceNumber) {
    return createPieceBitmap(pieceNumber);
  }

  private Size calculatePieceBitmapSize(int pieceNumber) {
    Puzzle.Piece piece = puzzle.getPiece(pieceNumber);
    int pieceWidth = PIECE_MIN_SQUARE_WIDTH;
    int pieceHeight = PIECE_MIN_SQUARE_HEIGHT;

    if (piece.getSideForm(Puzzle.Piece.SIDE_TOP) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      pieceHeight += PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    if (piece.getSideForm(Puzzle.Piece.SIDE_BOTTOM) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      pieceHeight += PIECE_MIN_SQUARE_HEIGHT;
    }

    if (piece.getSideForm(Puzzle.Piece.SIDE_LEFT) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      pieceWidth += PIECE_MIN_SQUARE_WIDTH;
    }

    if (piece.getSideForm(Puzzle.Piece.SIDE_RIGHT) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      pieceWidth += PIECE_MIN_SQUARE_WIDTH;
    }

    return new Size(pieceWidth, pieceHeight);
  }

  private Point calculatePieceImageCoordinates(int pieceNumber) {
    Puzzle.Piece piece = puzzle.getPiece(pieceNumber);

    int pieceNumberInRow = pieceNumber % puzzle.getPuzzleColumnsCount();
    int imageLeft = PIECE_MIN_SQUARE_WIDTH * pieceNumberInRow;
    if (pieceNumberInRow != 0
        && piece.getSideForm(Puzzle.Piece.SIDE_LEFT) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      imageLeft -= PIECE_CONVEX_CONCAVE_CUBIC_WIDTH;
    }

    int pieceNumberInColumn = pieceNumber / puzzle.getPuzzleColumnsCount();
    int imageTop = PIECE_MIN_SQUARE_HEIGHT * pieceNumberInColumn;
    if (pieceNumberInColumn != 0
        && piece.getSideForm(Puzzle.Piece.SIDE_TOP) == Puzzle.Piece.SIDE_FORM_CONVEX) {
      imageTop -= PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
    }

    return new Point(imageLeft, imageTop);
  }

  private Bitmap createPieceBitmap(int pieceNumber) {
    // Calculate piece
    Path piecePath = createPiecePath(pieceNumber);
    Size pieceSize = calculatePieceBitmapSize(pieceNumber);

    Bitmap cutPieceBitmap =
        Bitmap.createBitmap(pieceSize.getWidth(), pieceSize.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas pieceCanvas = new Canvas(cutPieceBitmap);

    piecePaint.setColor(0xFF000000);
    pieceCanvas.drawPath(piecePath, piecePaint);
    piecePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

    Point pieceImageCoordinates = calculatePieceImageCoordinates(pieceNumber);
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

  private Path createPiecePath(int pieceNumber) {
    Path pieceFormPath = new Path();
    final int START_X = 0;
    final int START_Y = 1;

    int[] pathStartPoint = setPathStartPoint(pieceFormPath, pieceNumber);
    Timber.d("====================== pieceNumber: %d ====================== ", pieceNumber);
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
        startX = PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
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
        startY = PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT;
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
    Timber.d("-------- TOP ---------------- ");
    Timber.d("-------- startX: %d, startY: %d", startX, startY);
    Timber.d("-------- toX: %d, toY: %d", startX + PIECE_MIN_SQUARE_WIDTH, startY);
    //path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY);
    switch (TOP_SIDE_FORM) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY);
        Timber.d("-------- toX: %d, toY: %d", startX + PIECE_MIN_SQUARE_WIDTH, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH / 3, startY);
        path.cubicTo(startX + (PIECE_MIN_SQUARE_WIDTH / 3) - PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startX + ((PIECE_MIN_SQUARE_WIDTH / 3) * 2) + PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startX + ((PIECE_MIN_SQUARE_WIDTH / 3) * 2), startY);
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH / 3, startY);
        path.cubicTo(startX + (PIECE_MIN_SQUARE_WIDTH / 3) - PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT, 0,
            startX + ((PIECE_MIN_SQUARE_WIDTH / 3) * 2) + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT, 0,
            startX + ((PIECE_MIN_SQUARE_WIDTH / 3) * 2), startY);
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY);
        break;
    }
  }

  /**
   * Must be called after {@link #drawTopSide(Path, int, int, int)} and before
   * {@link #drawBottomSide(Path, int, int, int)}.
   */
  private void drawRightSide(Path path, int pieceNumber, int startX, int startY) {
    final int RIGHT_SIDE = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_RIGHT);
    Timber.d("-------- RIGHT ---------------- ");
    Timber.d("-------- startX: %d, startY: %d", startX, startY);
    Timber.d("-------- toX: %d, toY: %d", startX + PIECE_MIN_SQUARE_WIDTH,
        startY + PIECE_MIN_SQUARE_WIDTH);
    //   path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY + PIECE_MIN_SQUARE_WIDTH);
    switch (RIGHT_SIDE) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY + PIECE_MIN_SQUARE_WIDTH);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY + PIECE_MIN_SQUARE_WIDTH / 3);
        path.cubicTo(
            startX + PIECE_MIN_SQUARE_WIDTH - PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (PIECE_MIN_SQUARE_WIDTH / 3) - PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + PIECE_MIN_SQUARE_WIDTH - PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (PIECE_MIN_SQUARE_WIDTH / 3) * 2 + PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + PIECE_MIN_SQUARE_WIDTH,
            startY + (PIECE_MIN_SQUARE_WIDTH / 3) * 2);
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY + PIECE_MIN_SQUARE_WIDTH);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY + PIECE_MIN_SQUARE_WIDTH / 3);
        path.cubicTo(
            startX + PIECE_MIN_SQUARE_WIDTH + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (PIECE_MIN_SQUARE_WIDTH / 3) - PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + PIECE_MIN_SQUARE_WIDTH + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (PIECE_MIN_SQUARE_WIDTH / 3) * 2 + PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + PIECE_MIN_SQUARE_WIDTH,
            startY + (PIECE_MIN_SQUARE_WIDTH / 3) * 2);
        path.lineTo(startX + PIECE_MIN_SQUARE_WIDTH, startY + PIECE_MIN_SQUARE_WIDTH);
        break;
    }
  }

  /**
   * Must be called after {@link #drawRightSide(Path, int, int, int)} and before
   * {@link #drawLeftSide(Path, int, int, int)}.
   */
  private void drawBottomSide(Path path, int pieceNumber, int startX, int startY) {
    final int BOTTOM_SIDE = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_BOTTOM);
    Timber.d("-------- BOTTOM---------------- ");
    Timber.d("-------- startX: %d, startY: %d", startX, startY);
    Timber.d("-------- toX: %d, toY: %d", startX, startY + PIECE_MIN_SQUARE_WIDTH);
    switch (BOTTOM_SIDE) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        path.lineTo(startX, startY + PIECE_MIN_SQUARE_WIDTH);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX + (PIECE_MIN_SQUARE_WIDTH / 3) * 2, startY + PIECE_MIN_SQUARE_WIDTH);
        path.cubicTo(
            startX + (PIECE_MIN_SQUARE_WIDTH / 3) * 2 + PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + PIECE_MIN_SQUARE_HEIGHT - PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + (PIECE_MIN_SQUARE_HEIGHT / 3) - PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + PIECE_MIN_SQUARE_HEIGHT - PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + PIECE_MIN_SQUARE_WIDTH / 3,
            startY + PIECE_MIN_SQUARE_HEIGHT);
        path.lineTo(startX, startY + PIECE_MIN_SQUARE_WIDTH);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX + (PIECE_MIN_SQUARE_WIDTH / 3) * 2, startY + PIECE_MIN_SQUARE_WIDTH);
        path.cubicTo(
            startX + (PIECE_MIN_SQUARE_WIDTH / 3) * 2 + PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + PIECE_MIN_SQUARE_HEIGHT + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + (PIECE_MIN_SQUARE_HEIGHT / 3) - PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,
            startY + PIECE_MIN_SQUARE_HEIGHT + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,

            startX + PIECE_MIN_SQUARE_WIDTH / 3,
            startY + PIECE_MIN_SQUARE_HEIGHT);
        path.lineTo(startX, startY + PIECE_MIN_SQUARE_WIDTH);
        break;
    }
  }

  /**
   * Must be called last, after {@link #drawBottomSide(Path, int, int, int)},
   * in a series of methods drawing piece sides.
   */
  private void drawLeftSide(Path path, int pieceNumber, int startX, int startY) {
    final int LEFT_SIDE = puzzle.getPiece(pieceNumber).getSideForm(Puzzle.Piece.SIDE_LEFT);
    Timber.d("-------- LEFT ---------------- ");
    Timber.d("-------- startX: %d, startY: %d", startX, startY);
    Timber.d("-------- toX: %d, toY: %d", startX, startY);
    switch (LEFT_SIDE) {
      case Puzzle.Piece.SIDE_FORM_FLAT:
        path.lineTo(startX, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONCAVE:
        path.lineTo(startX, startY + (PIECE_MIN_SQUARE_HEIGHT / 3) * 2);
        path.cubicTo(
            startX + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + ((PIECE_MIN_SQUARE_HEIGHT / 3) * 2) + PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX + PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (PIECE_MIN_SQUARE_HEIGHT / 3) - PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX,
            startY + (PIECE_MIN_SQUARE_HEIGHT / 3));
        path.lineTo(startX, startY);
        break;
      case Puzzle.Piece.SIDE_FORM_CONVEX:
        path.lineTo(startX, startY + (PIECE_MIN_SQUARE_HEIGHT / 3) * 2);
        path.cubicTo(
            startX - PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + ((PIECE_MIN_SQUARE_HEIGHT / 3) * 2) + PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX - PIECE_CONVEX_CONCAVE_CUBIC_HEIGHT,
            startY + (PIECE_MIN_SQUARE_HEIGHT / 3) - PIECE_CONVEX_CONCAVE_CUBIC_WIDTH,

            startX,
            startY + (PIECE_MIN_SQUARE_HEIGHT / 3));
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

    canvas.drawBitmap(createPieceFromNumber(7), 20, 30, null);
    canvas.drawBitmap(createPieceFromNumber(12), 100, 123, null);
    canvas.drawBitmap(createPieceFromNumber(0), 100, 100, null);
    canvas.drawBitmap(createPieceFromNumber(17), 140, 130, null);
    canvas.drawBitmap(createPieceFromNumber(1), 23, 421, null);
    canvas.drawBitmap(createPieceFromNumber(20), 643, 33, null);
    canvas.drawBitmap(createPieceFromNumber(35), 11, 780, null);
    canvas.drawBitmap(createPieceFromNumber(23), 778, 90, null);
    canvas.drawBitmap(createPieceFromNumber(37), 718, 457, null);
    canvas.drawBitmap(createPieceFromNumber(42), 500, 457, null);
    canvas.drawBitmap(createPieceFromNumber(42), 800, 800, null);
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

      options.inJustDecodeBounds = false;
      return BitmapFactory.decodeResource(resources, bitmapResource[0], options);
    }

    @Override protected void onPostExecute(Bitmap bitmap) {
      if (puzzleViewWeakReference.get() == null) {
        return;
      }

      PuzzleView puzzleView = puzzleViewWeakReference.get();
      puzzleView.imageBitmap = bitmap;
      Timber.d("------- L O O O L ------ Bitmap: %s", bitmap);
      puzzleView.invalidate();
    }
  }
}

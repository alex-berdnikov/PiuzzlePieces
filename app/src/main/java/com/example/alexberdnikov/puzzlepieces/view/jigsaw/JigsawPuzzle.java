package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.BuildConfig;
import com.example.alexberdnikov.puzzlepieces.view.Piece;
import com.example.alexberdnikov.puzzlepieces.view.PiecesPicker;
import com.example.alexberdnikov.puzzlepieces.view.Puzzle;

public class JigsawPuzzle extends Puzzle {
  private int pieceSquareWidth;
  private int pieceSquareHeight;
  private int pieceConvexConcaveCubicWidth = 16;
  private int pieceConvexConcaveCubicHeight = 24;

  private PiecesGenerator piecesGenerator;
  private Paint piecePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public JigsawPuzzle(Context context, int columnsCount, int rowsCount) {
    super(context);
    piecesGenerator = new PiecesGenerator(columnsCount, rowsCount);
    definePieceSquareSize();
  }

  private void definePieceSquareSize() {
    pieceSquareWidth = getPuzzleAreaSize().getWidth() / piecesGenerator.getPuzzleColumnsCount();
    pieceSquareHeight = getPuzzleAreaSize().getHeight() / piecesGenerator.getPuzzleRowsCount();
  }

  @Override protected Piece createPiece(int number, int x, int y) {
      return new Piece(createPieceImage(number), number, x, y);
  }

  private Bitmap createPieceImage(int pieceNumber) {
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
        getImageBitmap(),
        pieceImageCoordinates.x,
        pieceImageCoordinates.y,
        pieceSize.getWidth(),
        pieceSize.getHeight());

    pieceCanvas.drawBitmap(pieceBitmap, 0, 0, piecePaint);
    piecePaint.setXfermode(null);

    if (BuildConfig.DEBUG) {
      piecePaint.setAntiAlias(true);
      piecePaint.setColor(Color.WHITE);
      piecePaint.setTextSize(34);
      piecePaint.setStyle(Paint.Style.FILL);
      pieceCanvas.drawText(Integer.toString(pieceNumber),
          pieceSize.getWidth() / 2 - 20, pieceSize.getHeight() / 2, piecePaint);
    }

    return cutPieceBitmap;
  }

  @Override protected PiecesPicker createPiecesPicker(int screenWidth, int screenHeight) {
    return new JigsawPiecesPicker(
        getPieces(), screenWidth, screenHeight);
  }

  @Override public void generate() {
    for (int i = 0; i < getPiecesCount(); i++) {
      // Just put the pieces consequently
      getPieces().add(createPiece(i, (i % 16 ) * (96 + 18) + 40, (i / 16) * (96 + 18) + 40));
    }
  }

  private Size calculatePieceBitmapSize(int pieceNumber) {
    PiecesGenerator.PieceDescription pieceDescription = piecesGenerator.getPiece(pieceNumber);
    int pieceWidth = pieceSquareWidth;
    int pieceHeight = pieceSquareHeight;

    if (pieceDescription.getSideForm(PiecesGenerator.PieceDescription.SIDE_TOP)
        == PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX) {
      pieceHeight += pieceConvexConcaveCubicHeight;
    }

    if (pieceDescription.getSideForm(PiecesGenerator.PieceDescription.SIDE_BOTTOM)
        == PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX) {
      pieceHeight += pieceConvexConcaveCubicHeight;
    }

    if (pieceDescription.getSideForm(PiecesGenerator.PieceDescription.SIDE_LEFT)
        == PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX) {
      pieceWidth += pieceConvexConcaveCubicHeight;
    }

    if (pieceDescription.getSideForm(PiecesGenerator.PieceDescription.SIDE_RIGHT)
        == PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX) {
      pieceWidth += pieceConvexConcaveCubicHeight;
    }

    return new Size(pieceWidth, pieceHeight);
  }

  private Point calculatePieceImageCoordinates(int pieceNumber) {
    PiecesGenerator.PieceDescription pieceDescription = piecesGenerator.getPiece(pieceNumber);

    int pieceNumberInRow = pieceNumber % piecesGenerator.getPuzzleColumnsCount();
    int imageLeft = pieceSquareWidth * pieceNumberInRow;
    if (pieceNumberInRow != 0
        && pieceDescription.getSideForm(PiecesGenerator.PieceDescription.SIDE_LEFT)
        == PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX) {
      imageLeft -= pieceConvexConcaveCubicHeight;
    }

    int pieceNumberInColumn = pieceNumber / piecesGenerator.getPuzzleColumnsCount();
    int imageTop = pieceSquareHeight * pieceNumberInColumn;
    if (pieceNumberInColumn != 0
        && pieceDescription.getSideForm(PiecesGenerator.PieceDescription.SIDE_TOP)
        == PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX) {
      imageTop -= pieceConvexConcaveCubicHeight;
    }

    return new Point(imageLeft, imageTop);
  }

  @Override public int getPiecesCount() {
    return piecesGenerator.getPiecesCount();
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
    final int leftSide = piecesGenerator.getPiece(pieceNumber)
        .getSideForm(PiecesGenerator.PieceDescription.SIDE_LEFT);
    final int startX;
    switch (leftSide) {
      case PiecesGenerator.PieceDescription.SIDE_FORM_FLAT:
        startX = 0;
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONCAVE:
        startX = 0;
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX:
        startX = pieceConvexConcaveCubicHeight;
        break;
      default:
        throw new IllegalStateException("PieceDescription side form has undefined value.");
    }

    final int topSideForm = piecesGenerator.getPiece(pieceNumber)
        .getSideForm(PiecesGenerator.PieceDescription.SIDE_TOP);
    final int startY;
    switch (topSideForm) {
      case PiecesGenerator.PieceDescription.SIDE_FORM_FLAT:
        startY = 0;
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONCAVE:
        startY = 0;
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX:
        startY = pieceConvexConcaveCubicHeight;
        break;
      default:
        throw new IllegalStateException("PieceDescription side form has undefined value.");
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
    final int TOP_SIDE_FORM = piecesGenerator.getPiece(pieceNumber)
        .getSideForm(PiecesGenerator.PieceDescription.SIDE_TOP);
    switch (TOP_SIDE_FORM) {
      case PiecesGenerator.PieceDescription.SIDE_FORM_FLAT:
        path.lineTo(startX + pieceSquareWidth, startY);
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONCAVE:
        path.lineTo(startX + pieceSquareWidth / 3, startY);
        path.cubicTo(startX + (pieceSquareWidth / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceConvexConcaveCubicHeight,
            startX + ((pieceSquareWidth / 3) * 2) + pieceConvexConcaveCubicWidth,
            startY + pieceConvexConcaveCubicHeight,
            startX + ((pieceSquareWidth / 3) * 2), startY);
        path.lineTo(startX + pieceSquareWidth, startY);
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX:
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
    final int RIGHT_SIDE = piecesGenerator.getPiece(pieceNumber)
        .getSideForm(PiecesGenerator.PieceDescription.SIDE_RIGHT);
    switch (RIGHT_SIDE) {
      case PiecesGenerator.PieceDescription.SIDE_FORM_FLAT:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight);
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONCAVE:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight / 3);
        path.cubicTo(
            startX + pieceSquareWidth - pieceConvexConcaveCubicHeight,
            startY + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,

            startX + pieceSquareWidth - pieceConvexConcaveCubicHeight,
            startY + (pieceSquareHeight / 3) * 2 + pieceConvexConcaveCubicWidth,

            startX + pieceSquareWidth,
            startY + (pieceSquareHeight / 3) * 2);
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight);
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX:
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight / 3);
        path.cubicTo(
            startX + pieceSquareWidth + pieceConvexConcaveCubicHeight,
            startY + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,

            startX + pieceSquareWidth + pieceConvexConcaveCubicHeight,
            startY + (pieceSquareHeight / 3) * 2 + pieceConvexConcaveCubicWidth,

            startX + pieceSquareWidth,
            startY + (pieceSquareHeight / 3) * 2);
        path.lineTo(startX + pieceSquareWidth, startY + pieceSquareHeight);
        break;
    }
  }

  /**
   * Must be called after {@link #drawRightSide(Path, int, int, int)} and before
   * {@link #drawLeftSide(Path, int, int, int)}.
   */
  private void drawBottomSide(Path path, int pieceNumber, int startX, int startY) {
    final int BOTTOM_SIDE = piecesGenerator.getPiece(pieceNumber)
        .getSideForm(PiecesGenerator.PieceDescription.SIDE_BOTTOM);
    switch (BOTTOM_SIDE) {
      case PiecesGenerator.PieceDescription.SIDE_FORM_FLAT:
        path.lineTo(startX, startY + pieceSquareHeight);
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONCAVE:
        path.lineTo(startX + (pieceSquareWidth / 3) * 2, startY + pieceSquareHeight);
        path.cubicTo(
            startX + (pieceSquareWidth / 3) * 2 + pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight - pieceConvexConcaveCubicHeight,

            startX + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight - pieceConvexConcaveCubicHeight,

            startX + pieceSquareWidth / 3,
            startY + pieceSquareHeight);
        path.lineTo(startX, startY + pieceSquareHeight);
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX:
        path.lineTo(startX + (pieceSquareWidth / 3) * 2, startY + pieceSquareHeight);
        path.cubicTo(
            startX + (pieceSquareWidth / 3) * 2 + pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight + pieceConvexConcaveCubicHeight,

            startX + (pieceSquareHeight / 3) - pieceConvexConcaveCubicWidth,
            startY + pieceSquareHeight + pieceConvexConcaveCubicHeight,

            startX + pieceSquareWidth / 3,
            startY + pieceSquareHeight);
        path.lineTo(startX, startY + pieceSquareHeight);
        break;
    }
  }

  /**
   * Must be called last, after {@link #drawBottomSide(Path, int, int, int)},
   * in a series of methods drawing piece sides.
   */
  private void drawLeftSide(Path path, int pieceNumber, int startX, int startY) {
    final int LEFT_SIDE = piecesGenerator.getPiece(pieceNumber)
        .getSideForm(PiecesGenerator.PieceDescription.SIDE_LEFT);
    switch (LEFT_SIDE) {
      case PiecesGenerator.PieceDescription.SIDE_FORM_FLAT:
        path.lineTo(startX, startY);
        break;
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONCAVE:
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
      case PiecesGenerator.PieceDescription.SIDE_FORM_CONVEX:
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
}

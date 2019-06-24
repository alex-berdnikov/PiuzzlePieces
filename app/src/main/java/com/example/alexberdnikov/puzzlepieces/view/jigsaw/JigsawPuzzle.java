package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.BuildConfig;
import com.example.alexberdnikov.puzzlepieces.view.Piece;
import com.example.alexberdnikov.puzzlepieces.view.PiecesPicker;
import com.example.alexberdnikov.puzzlepieces.view.Puzzle;
import timber.log.Timber;

public class JigsawPuzzle extends Puzzle {
  private final boolean DRAW_NUMBERS_ON_PIECES = true;
  private int pieceSquareWidth;
  private int pieceSquareHeight;

  private PiecesSidesGenerator piecesSidesGenerator;
  private JigsawPathsGenerator jigsawPathsGenerator;
  private Paint piecePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public JigsawPuzzle(Context context, int columnsCount, int rowsCount) {
    super(context);
    piecesSidesGenerator = new PiecesSidesGenerator(columnsCount, rowsCount);
    definePieceSquareSize();
    jigsawPathsGenerator =
        new JigsawPathsGenerator(piecesSidesGenerator, pieceSquareWidth, pieceSquareHeight);
  }

  private void definePieceSquareSize() {
    pieceSquareWidth =
        getPuzzleAreaSize().getWidth() / piecesSidesGenerator.getPuzzleColumnsCount();
    pieceSquareHeight = getPuzzleAreaSize().getHeight() / piecesSidesGenerator.getPuzzleRowsCount();
  }

  @Override protected Piece createPiece(int number, int x, int y) {
    return new JigsawPiece(
        createPieceImage(number),
        piecesSidesGenerator.getSidesDescription(number),
        piecesSidesGenerator.getPuzzleColumnsCount(),
        piecesSidesGenerator.getPuzzleRowsCount(),
        number,
        x,
        y);
  }

  private Bitmap createPieceImage(int pieceNumber) {
    JigsawPathsGenerator.PathData pieceData = jigsawPathsGenerator.createPathForPiece(pieceNumber);
    Size pieceSize = pieceData.getRectSize();
    //pieceData.
    Bitmap cutPieceBitmap =
        Bitmap.createBitmap(pieceSize.getWidth(), pieceSize.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas pieceCanvas = new Canvas(cutPieceBitmap);

    piecePaint.setColor(0xFF000000);
    piecePaint.setStrokeWidth(1);
    piecePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    pieceCanvas.drawPath(pieceData.getPath(), piecePaint);
    piecePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    Point pieceImageSpriteCoordinates = pieceData.getOriginalCoordinates();

    Timber.d("-------- Bitmap w/h: %d/%d", getImageBitmap().getWidth(),
        getImageBitmap().getHeight());
    Timber.d("--------#%d ==> x: %d, y: %d, width: %d, height: %d",
        pieceNumber,
        pieceImageSpriteCoordinates.x,
        pieceImageSpriteCoordinates.y,
        pieceSize.getWidth(),
        pieceSize.getHeight());

    int imageSpriteWidth =
        (getImageBitmap().getWidth() < pieceImageSpriteCoordinates.x + pieceSize.getWidth())
            ? getImageBitmap().getWidth() - pieceImageSpriteCoordinates.x
            : pieceSize.getWidth();

    int imageSpriteHeight =
        (getImageBitmap().getHeight() < pieceImageSpriteCoordinates.y + pieceSize.getHeight())
            ? getImageBitmap().getHeight() - pieceImageSpriteCoordinates.y
            : pieceSize.getHeight();

    Bitmap pieceBitmap = Bitmap.createBitmap(
        getImageBitmap(),
        pieceImageSpriteCoordinates.x,
        pieceImageSpriteCoordinates.y,
        imageSpriteWidth,
        imageSpriteHeight);

    pieceCanvas.drawBitmap(pieceBitmap, 0, 0, piecePaint);
    piecePaint.setXfermode(null);

    drawPeaceNumberIfNeeded(pieceCanvas, pieceSize, pieceNumber);

    piecePaint.setColor(Color.BLACK);
    piecePaint.setStyle(Paint.Style.STROKE);
    pieceCanvas.drawPath(pieceData.getPath(), piecePaint);

    return cutPieceBitmap;
  }

  private void drawPeaceNumberIfNeeded(Canvas pieceCanvas, Size pieceSize, int pieceNumber) {
    if (DRAW_NUMBERS_ON_PIECES && BuildConfig.DEBUG) {
      piecePaint.setAntiAlias(true);
      piecePaint.setColor(Color.WHITE);
      piecePaint.setTextSize(34);
      piecePaint.setStyle(Paint.Style.FILL);
      pieceCanvas.drawText(Integer.toString(pieceNumber),
          pieceSize.getWidth() / 2f - 20, pieceSize.getHeight() / 2f, piecePaint);
    }
  }

  @Override protected PiecesPicker createPiecesPicker(int screenWidth, int screenHeight) {
    return new JigsawPiecesPicker(getPieces(), screenWidth, screenHeight);
  }

  @Override public void generate() {
    for (int i = 0; i < piecesSidesGenerator.getPiecesCount(); i++) {
      // Just put the pieces consequently
      getPieces().add(createPiece(i, (i % 16) * (96 + 18) + 40, (i / 16) * (96 + 18) + 40));
    }
    onGenerated();
  }
}

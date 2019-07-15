package com.afterglowgames.alexberdnikov.puzzlepieces.view.jigsaw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Size;
import com.afterglowgames.alexberdnikov.puzzlepieces.BuildConfig;
import com.afterglowgames.alexberdnikov.puzzlepieces.view.Piece;
import com.afterglowgames.alexberdnikov.puzzlepieces.view.PiecesGroup;
import com.afterglowgames.alexberdnikov.puzzlepieces.view.PiecesPicker;
import com.afterglowgames.alexberdnikov.puzzlepieces.view.Puzzle;
import java.util.List;
import timber.log.Timber;

public class JigsawPuzzle extends Puzzle implements PiecesPicker.EventsListener {
  private final boolean DRAW_NUMBERS_ON_PIECES = true;
  private final boolean DRAW_PIVOTS_ON_PIECES = false;

  private int pieceSquareWidth;
  private int pieceSquareHeight;

  private PiecesSidesGenerator piecesSidesGenerator;
  private JigsawPathsGenerator jigsawPathsGenerator;
  private Paint piecePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private final static int SHADOW_OFFSET = 5;
  private final static int SHADOW_RADIUS = 10;

  public JigsawPuzzle(Context context, int columnsCount, int rowsCount) {
    super(context);
    piecesSidesGenerator = new PiecesSidesGenerator(columnsCount, rowsCount);
    definePieceSquareSize();
    jigsawPathsGenerator =
        new JigsawPathsGenerator(piecesSidesGenerator, pieceSquareWidth, pieceSquareHeight);

    shadowPaint.setColor(0xFF000000);
    shadowPaint.setStyle(Paint.Style.FILL);
    shadowPaint.setShadowLayer(SHADOW_RADIUS, SHADOW_OFFSET, SHADOW_OFFSET, Color.DKGRAY);
  }

  private void definePieceSquareSize() {
    pieceSquareWidth =
        getPuzzleAreaSize().getWidth() / piecesSidesGenerator.getPuzzleColumnsCount();
    pieceSquareHeight = getPuzzleAreaSize().getHeight() / piecesSidesGenerator.getPuzzleRowsCount();
  }

  @Override protected Piece createPiece(int number, int x, int y) {
    JigsawPathsGenerator.PathData piecePathData = jigsawPathsGenerator.createPathForPiece(number);
    return new JigsawPiece(
        createPieceImage(piecePathData, number),
        piecesSidesGenerator,
        number,
        piecePathData.getOriginalCoordinates().x,
        piecePathData.getOriginalCoordinates().y,
        piecePathData.getPiecePivotX(),
        piecePathData.getPiecePivotY(),
        x,
        y);
  }

  private Bitmap createPieceAndShadowImage(JigsawPathsGenerator.PathData piecePathData,
      int pieceNumber) {

    Bitmap pieceImage = createPieceImage(piecePathData, pieceNumber);
    Bitmap pieceWithShadowImage = Bitmap.createBitmap(pieceImage.getWidth() + SHADOW_RADIUS,
        pieceImage.getHeight() + SHADOW_RADIUS, Bitmap.Config.ARGB_8888);

    Canvas pieceWithShadowCanvas = new Canvas(pieceWithShadowImage);
    pieceWithShadowCanvas.drawPath(piecePathData.getPath(), shadowPaint);
    pieceWithShadowCanvas.drawBitmap(pieceImage, 0, 0, null);

    return pieceWithShadowImage;
  }

  private Bitmap createPieceImage(JigsawPathsGenerator.PathData piecePathData, int pieceNumber) {
    Size pieceSize = piecePathData.getRectSize();

    Bitmap cutPieceBitmap =
        Bitmap.createBitmap(pieceSize.getWidth(),
            pieceSize.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas pieceCanvas = new Canvas(cutPieceBitmap);

    piecePaint.setColor(0xFF000000);
    piecePaint.setStrokeWidth(1);
    piecePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    pieceCanvas.drawPath(piecePathData.getPath(), piecePaint);

    piecePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    Point pieceImageSpriteCoordinates = piecePathData.getOriginalCoordinates();

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
    drawPiecePivotIfNeeded(pieceCanvas, piecePathData);

    piecePaint.setColor(Color.BLACK);
    piecePaint.setStyle(Paint.Style.STROKE);
    pieceCanvas.drawPath(piecePathData.getPath(), piecePaint);

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

  private void drawPiecePivotIfNeeded(Canvas pieceCanvas, JigsawPathsGenerator.PathData pieceData) {
    if (DRAW_PIVOTS_ON_PIECES && BuildConfig.DEBUG) {
      piecePaint.setColor(Color.RED);
      pieceCanvas.drawCircle(pieceData.getPiecePivotX(), pieceData.getPiecePivotY(), 5, piecePaint);
    }
  }

  @Override protected PiecesPicker createPiecesPicker(int screenWidth, int screenHeight) {
    PiecesPicker piecesPicker =
        new JigsawPiecesPicker(getPieces(), pieceSquareWidth, pieceSquareHeight, screenWidth,
            screenHeight);
    piecesPicker.setEventsListener(this);
    return piecesPicker;
  }

  @Override protected Bitmap createPiecesGroupShadow(PiecesGroup group) {
    Piece leftMostPiece = group.getLeftMostPiece();
    Piece rightMostPiece = group.getRightMostPiece();
    Piece topMostPiece = group.getTopMostPiece();
    Piece bottomMostPiece = group.getBottomMostPiece();

    int groupOffsetX = leftMostPiece.getPieceOffsetInPuzzle()[0];
    int groupOffsetY = topMostPiece.getPieceOffsetInPuzzle()[1];

    int shadowRectWidth =
        (rightMostPiece.getPieceOffsetInPuzzle()[0] + rightMostPiece.getPieceWidth())
            - leftMostPiece.getPieceOffsetInPuzzle()[0];
    int shadowRectHeight =
        (bottomMostPiece.getPieceOffsetInPuzzle()[1] + bottomMostPiece.getPieceHeight())
            - topMostPiece.getPieceOffsetInPuzzle()[1];

    Bitmap shadowBitmap =
        Bitmap.createBitmap(shadowRectWidth + SHADOW_RADIUS * 3,
            shadowRectHeight + SHADOW_RADIUS * 3,
            Bitmap.Config.ARGB_4444);
    Canvas shadowCanvas = new Canvas(shadowBitmap);

    List<Piece> pieces = group.getPieces();

    Piece firstPieceInShadow = pieces.get(0);
    JigsawPathsGenerator.PathData firstPiecePathData =
        jigsawPathsGenerator.createPathForPiece(firstPieceInShadow.getNumber());

    Path shadowPath = new Path(firstPiecePathData.getPath());
    shadowPath.offset(firstPieceInShadow.getPieceOffsetInPuzzle()[0] - groupOffsetX,
        firstPieceInShadow.getPieceOffsetInPuzzle()[1] - groupOffsetY);

    for (int i = 1; i < pieces.size(); i++) {
      JigsawPiece piece = (JigsawPiece) pieces.get(i);
      JigsawPathsGenerator.PathData piecePathData =
          jigsawPathsGenerator.createPathForPiece(piece.getNumber());
      Path piecePath = new Path(piecePathData.getPath());
      piecePath.offset(piece.getPieceOffsetInPuzzle()[0] - groupOffsetX,
          piece.getPieceOffsetInPuzzle()[1] - groupOffsetY);
      shadowPath.op(piecePath, Path.Op.UNION);
    }

    shadowCanvas.drawPath(shadowPath, shadowPaint);
    return shadowBitmap;
  }

  @Override public void generate() {
    for (int i = 0; i < piecesSidesGenerator.getPiecesCount(); i++) {
      // Just put the pieces consequently
      getPieces().add(createPiece(i, (i % 5) * 150 + 40, (i / 5) * 150 + 40));
    }
    onGenerated();
  }

  @Override public void onPiecesGroupUpdated(PiecesGroup newGroup) {
    for (Piece piece : newGroup.getPieces()) {
      getShadows().remove(piece.getNumber());
    }

    getShadows().put(newGroup.getPieces().get(0).getNumber(), createPiecesGroupShadow(newGroup));

    Timber.d("---shadows: %s", getShadows());
  }
}

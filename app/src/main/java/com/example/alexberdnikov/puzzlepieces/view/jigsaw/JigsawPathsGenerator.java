package com.example.alexberdnikov.puzzlepieces.view.jigsaw;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Size;
import com.example.alexberdnikov.puzzlepieces.view.Piece;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_BOTTOM;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_CONCAVE;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_CONVEX;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_FORM_FLAT;
import static com.example.alexberdnikov.puzzlepieces.view.jigsaw.JigsawPiece.SidesDescription.SIDE_RIGHT;

class JigsawPathsGenerator {
  private PiecesSidesGenerator piecesSidesGenerator;
  private int pieceSquareWidth;
  private int pieceSquareHeight;

  private List<Path> rowsPaths = new ArrayList<>();
  private List<Path> columnsPaths = new ArrayList<>();
  private Integer previousPieceSideType;

  JigsawPathsGenerator(PiecesSidesGenerator piecesSidesGenerator, int pieceSquareWidth,
      int pieceSquareHeight) {
    this.piecesSidesGenerator = piecesSidesGenerator;
    this.pieceSquareWidth = pieceSquareWidth;
    this.pieceSquareHeight = pieceSquareHeight;

    definePaths();
  }

  private void definePaths() {
    for (int row = 0; row < piecesSidesGenerator.getPuzzleRowsCount(); row++) {
      generateBottomSidePathForRow(row);
    }

    for (int column = 0; column < piecesSidesGenerator.getPuzzleColumnsCount(); column++) {
      generateRightSidePathForColumn(column);
    }
  }

  private void generateBottomSidePathForRow(int rowNumber) {
    int firstPieceInRow = rowNumber * piecesSidesGenerator.getPuzzleColumnsCount();
    int lastPieceInRow = (firstPieceInRow + piecesSidesGenerator.getPuzzleColumnsCount()) - 1;

    // Draw path starting from the bottom left corner, counterclockwise
    Path path = new Path();
    path.moveTo(0, pieceSquareHeight * (rowNumber + 1));

    for (int pieceNumber = firstPieceInRow; pieceNumber <= lastPieceInRow; pieceNumber++) {
      //int bottomSideForm = bottomSidesDescription.get(pieceNumber);
      int bottomSideForm =
          piecesSidesGenerator.getSidesDescription(pieceNumber).getSideForm(SIDE_BOTTOM);
      int pieceNumberInRow = pieceNumber % piecesSidesGenerator.getPuzzleColumnsCount();
      int pieceNumberInColumn = pieceNumber / piecesSidesGenerator.getPuzzleColumnsCount();

      if (bottomSideForm == SIDE_FORM_FLAT) {
        drawFlatBottomSide(path, pieceNumberInRow, pieceNumberInColumn);
      } else if (bottomSideForm == SIDE_FORM_CONCAVE) {
        drawConcaveBottomSide(path, pieceNumberInRow, pieceNumberInColumn);
      } else if (bottomSideForm == SIDE_FORM_CONVEX) {
        drawConvexBottomSide(path, pieceNumberInRow, pieceNumberInColumn);
      }
    }

    path.lineTo(pieceSquareWidth * piecesSidesGenerator.getPuzzleColumnsCount(), 0);
    path.lineTo(0, 0);
    path.close();

    rowsPaths.add(rowNumber, path);
  }

  private void generateRightSidePathForColumn(int columnNumber) {
    int firstPieceInColumn = columnNumber;
    int lastPieceInColumn = columnNumber
        + (piecesSidesGenerator.getPuzzleRowsCount() - 1)
        * piecesSidesGenerator.getPuzzleColumnsCount();

    // Draw path starting from the top right corner, clockwise
    Path path = new Path();
    path.moveTo(pieceSquareWidth * (columnNumber + 1), 0);

    for (int pieceNumber = firstPieceInColumn; pieceNumber <= lastPieceInColumn;
        pieceNumber += piecesSidesGenerator.getPuzzleColumnsCount()) {
      //int rightSideForm = rightSidesDescription.get(pieceNumber);
      int rightSideForm =
          piecesSidesGenerator.getSidesDescription(pieceNumber).getSideForm(SIDE_RIGHT);
      int pieceNumberInRow = pieceNumber % piecesSidesGenerator.getPuzzleColumnsCount();
      int pieceNumberInColumn = pieceNumber / piecesSidesGenerator.getPuzzleColumnsCount();

      if (rightSideForm == SIDE_FORM_FLAT) {
        drawFlatRightSide(path, pieceNumberInRow, pieceNumberInColumn);
      } else if (rightSideForm == SIDE_FORM_CONCAVE) {
        drawConcaveRightSide(path, pieceNumberInRow, pieceNumberInColumn);
      } else if (rightSideForm == SIDE_FORM_CONVEX) {
        drawConvexRightSide(path, pieceNumberInRow, pieceNumberInColumn);
      }
    }

    path.lineTo(0, piecesSidesGenerator.getPuzzleColumnsCount() * pieceSquareHeight);
    path.lineTo(0, 0);
    path.close();

    columnsPaths.add(columnNumber, path);
  }

  private void drawConcaveBottomSide(Path path, int pieceNumberInRow, int pieceNumberInColumn) {
    float concaveStartPointX = pieceSquareWidth * pieceNumberInRow + (pieceSquareWidth / 5f) * 2;
    float concaveStartPointY = pieceSquareHeight * (pieceNumberInColumn + 1);

    float concaveEndPointX = pieceSquareWidth * pieceNumberInRow + (pieceSquareWidth / 5f) * 3;
    float concaveEndPointY = concaveStartPointY;

    boolean isFirstPieceInRow = pieceNumberInRow == 0;
    boolean lastFirstPieceInRow =
        pieceNumberInRow == piecesSidesGenerator.getPuzzleColumnsCount() - 1;

    if (isFirstPieceInRow) {
      path.cubicTo(concaveStartPointX - pieceSquareWidth / 11f,
          concaveStartPointY + pieceSquareHeight / 11f,

          concaveStartPointX,
          concaveStartPointY + pieceSquareHeight / 11f,

          concaveStartPointX,
          concaveStartPointY);
    } else if (previousPieceSideType == SIDE_FORM_CONCAVE) {
      path.cubicTo(concaveStartPointX - ((pieceSquareWidth / 5f) * 4) - pieceSquareWidth / 11f,
          concaveStartPointY + pieceSquareHeight / 6f,

          concaveStartPointX + pieceSquareWidth / 11f,
          concaveEndPointY + pieceSquareHeight / 6f,

          concaveStartPointX,
          concaveStartPointY);
    } else if (previousPieceSideType == SIDE_FORM_CONVEX) {
      path.cubicTo(concaveStartPointX - ((pieceSquareWidth / 5f) * 4) - pieceSquareWidth / 11f,
          concaveStartPointY - pieceSquareHeight / 3.4f,

          concaveStartPointX + pieceSquareWidth / 11f,
          concaveEndPointY + pieceSquareHeight / 3.4f,

          concaveStartPointX,
          concaveStartPointY);
    }

    path.cubicTo(concaveStartPointX - pieceSquareWidth / 7f,
        concaveStartPointY - pieceSquareHeight / 3f,
        concaveEndPointX + pieceSquareWidth / 7f,
        concaveEndPointY - pieceSquareHeight / 3f,
        concaveEndPointX, concaveEndPointY);

    if (lastFirstPieceInRow) {
      path.quadTo(concaveEndPointX - pieceSquareWidth / 11f,
          concaveEndPointY + pieceSquareHeight / 6f,
          pieceSquareWidth * piecesSidesGenerator.getPuzzleColumnsCount(),
          pieceSquareHeight * (pieceNumberInColumn + 1));
    }

    previousPieceSideType = SIDE_FORM_CONCAVE;
  }

  private void drawConvexBottomSide(Path path, int pieceNumberInRow, int pieceNumberInColumn) {
    float concaveStartPointX = pieceSquareWidth * pieceNumberInRow + (pieceSquareWidth / 5f) * 2;
    float concaveStartPointY =
        pieceSquareHeight * (pieceNumberInColumn + 1) - pieceSquareHeight / 17f;

    float concaveEndPointX = pieceSquareWidth * pieceNumberInRow + (pieceSquareWidth / 5f) * 3;
    float concaveEndPointY = concaveStartPointY;

    boolean isFirstPieceInRow = pieceNumberInRow == 0;
    boolean lastFirstPieceInRow =
        pieceNumberInRow == piecesSidesGenerator.getPuzzleColumnsCount() - 1;

    if (isFirstPieceInRow) {
      path.cubicTo(concaveStartPointX - pieceSquareWidth / 11f,
          concaveStartPointY - pieceSquareHeight / 11f,
          concaveStartPointX, concaveStartPointY - pieceSquareHeight / 11f,
          concaveStartPointX, concaveStartPointY);
    } else if (previousPieceSideType == SIDE_FORM_CONCAVE) {
      path.cubicTo(concaveStartPointX - ((pieceSquareWidth / 5f) * 4) - pieceSquareWidth / 11f,
          concaveStartPointY + pieceSquareHeight / 2.5f,
          concaveStartPointX + pieceSquareWidth / 11f,
          concaveEndPointY - pieceSquareHeight / 4f,
          concaveStartPointX, concaveStartPointY);
    } else if (previousPieceSideType == SIDE_FORM_CONVEX) {
      path.cubicTo(concaveStartPointX - ((pieceSquareWidth / 5f) * 4) - pieceSquareWidth / 11f,
          concaveStartPointY - pieceSquareHeight / 6f,
          concaveStartPointX + pieceSquareWidth / 11f,
          concaveEndPointY - pieceSquareHeight / 6f,
          concaveStartPointX, concaveStartPointY);
    }

    path.cubicTo(concaveStartPointX - pieceSquareWidth / 7f,
        concaveStartPointY + pieceSquareHeight / 3f,
        concaveEndPointX + pieceSquareWidth / 7f,
        concaveEndPointY + pieceSquareHeight / 3f,
        concaveEndPointX, concaveEndPointY);

    if (lastFirstPieceInRow) {
      path.quadTo(concaveEndPointX - pieceSquareWidth / 11f,
          concaveEndPointY - pieceSquareHeight / 6f,
          pieceSquareWidth * piecesSidesGenerator.getPuzzleColumnsCount(),
          pieceSquareHeight * (pieceNumberInColumn + 1));
    }

    previousPieceSideType = SIDE_FORM_CONVEX;
  }

  private void drawFlatBottomSide(Path path, int pieceNumberInRow, int pieceNumberInColumn) {
    path.lineTo(pieceSquareWidth * (pieceNumberInRow + 1),
        pieceSquareHeight * (pieceNumberInColumn + 1));
    previousPieceSideType = SIDE_FORM_FLAT;
  }

  private void drawConcaveRightSide(Path path, int pieceNumberInRow, int pieceNumberInColumn) {
    float concaveStartPointX = pieceSquareWidth * (pieceNumberInRow + 1);
    float concaveStartPointY =
        pieceSquareHeight * pieceNumberInColumn + (pieceSquareHeight / 5f) * 2;

    float concaveEndPointX = concaveStartPointX;
    float concaveEndPointY = pieceSquareHeight * pieceNumberInColumn + (pieceSquareHeight / 5f) * 3;

    boolean isFirstPieceInColumn = pieceNumberInColumn == 0;
    boolean lastFirstPieceInColumn =
        pieceNumberInColumn == piecesSidesGenerator.getPuzzleRowsCount() - 1;

    if (isFirstPieceInColumn) {
      path.cubicTo(concaveStartPointX + pieceSquareWidth / 11f,
          concaveStartPointY - pieceSquareHeight / 11f,

          concaveStartPointX + pieceSquareWidth / 11f,
          concaveStartPointY,

          concaveStartPointX,
          concaveStartPointY);
    } else if (previousPieceSideType == SIDE_FORM_CONCAVE) {
      path.cubicTo(concaveStartPointX + pieceSquareWidth / 6f,
          concaveStartPointY - ((pieceSquareHeight / 5f) * 4) - pieceSquareHeight / 11f,

          concaveStartPointX + pieceSquareWidth / 4f,
          concaveEndPointY,

          concaveStartPointX,
          concaveStartPointY);
    } else if (previousPieceSideType == SIDE_FORM_CONVEX) {
      path.cubicTo(concaveStartPointX - pieceSquareWidth / 3.4f,
          concaveStartPointY - ((pieceSquareHeight / 5f) * 4) - pieceSquareHeight / 11f,

          concaveStartPointX + pieceSquareWidth / 3f,
          concaveEndPointY,

          concaveStartPointX,
          concaveStartPointY);
    }

    path.cubicTo(concaveStartPointX - pieceSquareWidth / 3f,
        concaveStartPointY - pieceSquareHeight / 7f,
        concaveEndPointX - pieceSquareWidth / 3f,
        concaveEndPointY + pieceSquareHeight / 7f,
        concaveEndPointX, concaveEndPointY);

    if (lastFirstPieceInColumn) {
      path.quadTo(concaveEndPointX + pieceSquareWidth / 6f,
          concaveEndPointY - pieceSquareHeight / 11f,
          pieceSquareWidth * (pieceNumberInRow + 1) + pieceSquareWidth / 11f,
          pieceSquareHeight * piecesSidesGenerator.getPuzzleRowsCount());
    }

    previousPieceSideType = SIDE_FORM_CONCAVE;
  }

  private void drawConvexRightSide(Path path, int pieceNumberInRow, int pieceNumberInColumn) {
    float concaveStartPointX = pieceSquareWidth * (pieceNumberInRow + 1);
    float concaveStartPointY =
        pieceSquareHeight * pieceNumberInColumn + (pieceSquareHeight / 5f) * 2;

    float concaveEndPointX = concaveStartPointX;
    float concaveEndPointY = pieceSquareHeight * pieceNumberInColumn + (pieceSquareHeight / 5f) * 3;

    boolean isFirstPieceInColumn = pieceNumberInColumn == 0;
    boolean lastFirstPieceInColumn =
        pieceNumberInColumn == piecesSidesGenerator.getPuzzleRowsCount() - 1;

    if (isFirstPieceInColumn) {
      path.cubicTo(concaveStartPointX - pieceSquareWidth / 11f,
          concaveStartPointY - pieceSquareHeight / 11f,
          concaveStartPointX - pieceSquareWidth / 11f, concaveStartPointY,
          concaveStartPointX, concaveStartPointY);
    } else if (previousPieceSideType == SIDE_FORM_CONCAVE) {
      path.cubicTo(concaveStartPointX + pieceSquareWidth / 3.4f,
          concaveStartPointY - ((pieceSquareHeight / 5f) * 4) - pieceSquareHeight / 11f,
          concaveStartPointX - pieceSquareWidth / 3.4f,
          concaveEndPointY - pieceSquareHeight / 11f,
          concaveStartPointX, concaveStartPointY);
    } else if (previousPieceSideType == SIDE_FORM_CONVEX) {
      path.cubicTo(concaveStartPointX - pieceSquareWidth / 5f,
          concaveStartPointY - ((pieceSquareHeight / 5f) * 4) - pieceSquareHeight / 11f,
          concaveStartPointX - pieceSquareWidth / 5f,
          concaveEndPointY - pieceSquareHeight / 6f,
          concaveStartPointX, concaveStartPointY);
    }

    path.cubicTo(concaveStartPointX + pieceSquareWidth / 3f,
        concaveStartPointY - pieceSquareHeight / 7f,
        concaveEndPointX + pieceSquareWidth / 3f,
        concaveEndPointY + pieceSquareHeight / 7f,
        concaveEndPointX, concaveEndPointY);

    if (lastFirstPieceInColumn) {
      path.quadTo(concaveEndPointX - pieceSquareWidth / 6f,
          concaveEndPointY - pieceSquareHeight / 11f,
          pieceSquareWidth * (pieceNumberInRow + 1),
          pieceSquareHeight * piecesSidesGenerator.getPuzzleRowsCount());
    }

    previousPieceSideType = SIDE_FORM_CONVEX;
  }

  private void drawFlatRightSide(Path path, int pieceNumberInRow, int pieceNumberInColumn) {
    path.lineTo(pieceSquareWidth * (pieceNumberInRow + 1),
        pieceSquareHeight * piecesSidesGenerator.getPuzzleRowsCount());
    previousPieceSideType = SIDE_FORM_FLAT;
  }

  private Path createRowPath(int rowNumber) {
    if (rowNumber == 0) {
      return rowsPaths.get(0);
    }

    Path rowPath = new Path(rowsPaths.get(rowNumber));
    rowPath.op(rowsPaths.get(rowNumber - 1), Path.Op.DIFFERENCE);
    return rowPath;
  }

  private Path createColumnPath(int columnNumber) {
    if (columnNumber == 0) {
      return columnsPaths.get(0);
    }

    Path columnPath = new Path(columnsPaths.get(columnNumber));
    columnPath.op(columnsPaths.get(columnNumber - 1), Path.Op.DIFFERENCE);
    return columnPath;
  }

  PathData createPathForPiece(int pieceNumber) {
    int pieceRow = pieceNumber / piecesSidesGenerator.getPuzzleColumnsCount();
    int pieceColumn = pieceNumber % piecesSidesGenerator.getPuzzleColumnsCount();

    Path rowPath = createRowPath(pieceRow);
    Path columnPath = createColumnPath(pieceColumn);

    Path piecePath = new Path(rowPath);
    piecePath.op(columnPath, Path.Op.INTERSECT);

    RectF pieceBounds = new RectF();
    piecePath.computeBounds(pieceBounds, false);
    piecePath.offset(-pieceBounds.left, -pieceBounds.top);

    return new PathData(piecePath, pieceBounds);
  }

  static class PathData {
    private Path path;
    private RectF bounds;

    private PathData(Path path, RectF bounds) {
      Timber.d("---- bounds: %s", bounds);
      this.path = path;
      this.bounds = bounds;
    }

    Path getPath() {
      return path;
    }

    Size getRectSize() {
      int pieceWidth = (int) (bounds.right - bounds.left);
      int pieceHeight = (int) (bounds.bottom - bounds.top);
      return new Size(pieceWidth, pieceHeight);
    }

    Point getOriginalCoordinates() {
      return new Point((int) bounds.left, (int) bounds.top);
    }
  }
}

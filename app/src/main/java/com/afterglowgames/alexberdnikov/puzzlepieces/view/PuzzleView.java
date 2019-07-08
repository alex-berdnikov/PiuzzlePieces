package com.afterglowgames.alexberdnikov.puzzlepieces.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Size;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.Nullable;
import com.afterglowgames.alexberdnikov.puzzlepieces.R;

public class PuzzleView extends SurfaceView implements SurfaceHolder.Callback {

  private Puzzle puzzle;
  private SurfaceHolder surfaceHolder;
  private DrawThread drawThread;

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

  public void setupPuzzle(Puzzle puzzle) {
    this.puzzle = puzzle;
    setupSurface();
  }

  public Size getPuzzleAreaSize() {
    return puzzle.getPuzzleAreaSize();
  }

  private void loadBitmap() {
    //new LoadBitmapTask(getResources(), this).execute(R.drawable.autumn_1);
    new LoadBitmapTask(getResources(), this).execute(R.drawable.emma_stone);
  }

  private void setupSurface() {
    surfaceHolder = getHolder();
    surfaceHolder.addCallback(this);
  }

  protected void onImageLoaded(Bitmap bitmap) {
    puzzle.setImageBitmap(bitmap);
    puzzle.generate();

    startDrawing();
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        puzzle.onTouchStart(event.getX(), event.getY());
        break;
      case MotionEvent.ACTION_MOVE:
        puzzle.onMove(event.getX(), event.getY());
        updateView();
        break;
      case MotionEvent.ACTION_UP:
        puzzle.onTouchEnd(event.getX(), event.getY());
        updateView();
        return performClick();
    }
    return true;
  }

  private void updateView() {
    if (drawThread.getHandler() != null) {
      Message message = drawThread.getHandler().obtainMessage(0);
      drawThread.getHandler().sendMessage(message);
    }
  }

  @Override public boolean performClick() {
    return super.performClick();
  }

  private void startDrawing() {
    drawThread = new DrawThread(surfaceHolder, puzzle);
    drawThread.start();
  }

  @Override public void surfaceCreated(SurfaceHolder holder) {
    loadBitmap();
  }

  @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  @Override public void surfaceDestroyed(SurfaceHolder holder) {

  }
}

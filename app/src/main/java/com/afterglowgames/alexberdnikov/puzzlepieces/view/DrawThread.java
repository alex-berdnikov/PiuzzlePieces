package com.afterglowgames.alexberdnikov.puzzlepieces.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.view.SurfaceHolder;

public class DrawThread extends HandlerThread {
  private final static String THREAD_NAME = DrawThread.class.getSimpleName();
  public final static Object lock = new Object();

  private final SurfaceHolder surfaceHolder;
  private Puzzle puzzle;
  private Handler handler;
  private Canvas canvas;

  public DrawThread(SurfaceHolder surfaceHolder, Puzzle puzzle) {
    super(THREAD_NAME, Process.THREAD_PRIORITY_DISPLAY);
    this.surfaceHolder = surfaceHolder;
    this.puzzle = puzzle;
  }

  @Override protected void onLooperPrepared() {
    super.onLooperPrepared();
    handler = new Handler(getLooper()) {
      @Override public void handleMessage(Message message) {
        super.handleMessage(message);
        processMessage(message);
      }
    };

    // Draw immediately
    handler.sendMessage(Message.obtain());
  }

  @SuppressWarnings("unchecked")
  private void processMessage(Message message) {
    synchronized (surfaceHolder) {
      canvas = surfaceHolder.getSurface().lockCanvas(null);
      canvas.drawColor(Color.WHITE);

      synchronized (lock) {
        for (Piece piece : puzzle.getPieces()) {
          canvas.drawBitmap(piece.getPieceImage(), piece.getX(), piece.getY(), null);
        }
      }

      surfaceHolder.getSurface().unlockCanvasAndPost(canvas);
    }
  }

  Handler getHandler() {
    return handler;
  }
}

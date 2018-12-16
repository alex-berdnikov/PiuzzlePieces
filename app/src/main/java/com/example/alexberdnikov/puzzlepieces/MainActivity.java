package com.example.alexberdnikov.puzzlepieces;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.alexberdnikov.puzzlepieces.view.PuzzleView;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.puzzle_view) PuzzleView puzzleView;

  @Override public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.d("-------- onCreate() --> %s", getRequestedOrientation());
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @Override protected void onResume() {
    super.onResume();
    setupUi();
  }

  private void setupUi() {
    Timber.d("--- setupUi();");
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    puzzleView.setupPuzzle();
  }
}

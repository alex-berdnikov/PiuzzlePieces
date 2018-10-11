package com.example.chester.puzzlepieces;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import timber.log.Timber;

public class PuzzleApp extends Application {
  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    if (!LeakCanary.isInAnalyzerProcess(this)) {
      LeakCanary.install(this);
    }
  }
}
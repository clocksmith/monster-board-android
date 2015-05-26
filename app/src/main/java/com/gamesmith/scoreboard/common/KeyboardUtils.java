package com.gamesmith.scoreboard.common;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by clocksmith on 5/24/15.
 */
public class KeyboardUtils {
  public static void forceShowKeyboard(Activity activity) {
    showKeyboard(activity, InputMethodManager.SHOW_FORCED);
  }

  public static void hideKeyboard(Activity activity, EditText input) {
    if (activity.getCurrentFocus() != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }
  }

  private static void showKeyboard(Activity activity, int inputMethodManagerValue) {
    if (activity.getCurrentFocus() != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputMethodManager.showSoftInput(activity.getCurrentFocus(), inputMethodManagerValue);
    }
  }
}

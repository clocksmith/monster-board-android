package com.gamesmith.monsterboard.common.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by clocksmith on 5/24/15.
 */
public class KeyboardUtils {
  public static void forceShowKeyboard(Activity activity) {
    showKeyboard(activity, InputMethodManager.SHOW_FORCED);
  }

  private static void showKeyboard(Activity activity, int inputMethodManagerValue) {
    if (activity.getCurrentFocus() != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputMethodManager.toggleSoftInput(inputMethodManagerValue, 0);
    }
  }

  public static void hideKeyboard(Activity activity) {
    if (activity.getCurrentFocus() != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
  }
}

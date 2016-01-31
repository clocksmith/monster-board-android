package com.gamesmith.monsterboard.room;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * For this: http://stackoverflow.com/questions/24233556/changing-numberpicker-divider-color
 * Based on this: http://stackoverflow.com/a/20291416/2915480
 */
public class ScoreboardNumberPicker extends NumberPicker {
  private static final String TAG = ScoreboardNumberPicker.class.getSimpleName();

  private float mTextSize;

  public ScoreboardNumberPicker(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.setMinValue(0);
    this.setWrapSelectorWheel(false);
    this.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
  }

  public void setDividerColor(int color) {
    java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
    for (java.lang.reflect.Field pf : pickerFields) {
      if (pf.getName().equals("mSelectionDivider")) {
        pf.setAccessible(true);
        try {
          pf.set(this, new ColorDrawable(color));
        } catch (Exception e) {
          Log.e(TAG, "Nope", e);
        }
        break;
      }
    }
  }

  public void setTextColor(int color) {
    java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
    for (java.lang.reflect.Field pf : pickerFields) {
      if (pf.getName().equals("mSelectorWheelPaint")) {
        pf.setAccessible(true);
        try {
          ((Paint) pf.get(this)).setColor(color);
        } catch (Exception e) {
          Log.e(TAG, "Nope", e);
        }
        break;
      }
    }

    final int count = this.getChildCount();
    for (int i = 0; i < count; i++) {
      View child = this.getChildAt(i);
      if (child instanceof EditText) {
        try {
          ((EditText) child).setTextColor(color);
          this.invalidate();
        }
        catch(IllegalArgumentException e){
          Log.e(TAG, "Nope", e);
        }
      }
    }
  }

  public void setTextSize(int textSizePx) {
    java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
    for (java.lang.reflect.Field pf : pickerFields) {
      if (pf.getName().equals("mSelectorWheelPaint")) {
        pf.setAccessible(true);
        try {
          ((Paint) pf.get(this)).setTextSize(textSizePx);
        } catch (Exception e) {
          Log.e(TAG, "Nope", e);
        }
        break;
      }
    }

    final int count = this.getChildCount();
    for (int i = 0; i < count; i++) {
      View child = this.getChildAt(i);
      if (child instanceof EditText) {
        try {
          float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
          float textSizeSp = textSizePx / scaledDensity;
          ((EditText) child).setTextSize(textSizeSp);
          this.invalidate();
        }
        catch(IllegalArgumentException e){
          Log.e(TAG, "Nope", e);
        }
      }
    }
  }
}
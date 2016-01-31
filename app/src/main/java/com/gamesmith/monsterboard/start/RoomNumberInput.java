package com.gamesmith.monsterboard.start;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.common.providers.BusProvider;
import com.gamesmith.monsterboard.common.Constants;
import com.gamesmith.monsterboard.common.utils.KeyboardUtils;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by clocksmith on 5/29/15.
 */
public class RoomNumberInput extends FrameLayout {
  private static final String TAG = RoomNumberInput.class.getSimpleName();

  public class RoomNumberInputFinishedEvent {
    public final int roomNumber;

    public RoomNumberInputFinishedEvent(int roomNumber) {
      this.roomNumber = roomNumber;
    }
  }

  public class HideEvent {
    public HideEvent() {}
  }

  private Context mContext;
  private EditText mEditText;
  private List<TextView> mDigitFields;

  public RoomNumberInput(Context context) {
    this(context, null);
  }

  public RoomNumberInput(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RoomNumberInput(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mContext = context;

    mDigitFields = Lists.newArrayList();
    LinearLayout digitFieldContainer = new LinearLayout(mContext);
    digitFieldContainer.setLayoutParams(
        new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    LinearLayout.LayoutParams digitFieldLayoutParams =
        new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
    digitFieldLayoutParams.weight = 1;
    for (int digitFieldIndex = 0; digitFieldIndex < Constants.NUM_ROOM_NUMBER_DIGITS; digitFieldIndex++) {
      TextView digitField = new TextView(mContext);
      digitField.setLayoutParams(digitFieldLayoutParams);
      digitField.setGravity(Gravity.CENTER);
      digitField.setTextColor(mContext.getResources().getColor(R.color.accent_color_highlight));
      digitField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36f);
      digitFieldContainer.addView(digitField);
      mDigitFields.add(digitField);
    }
    this.addView(digitFieldContainer);

    mEditText = new EditText(mContext) {
      @Override
      public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
          BusProvider.getInstance().post(new HideEvent());
          return true;
        }
        return super.dispatchKeyEvent(event);
      }
    };

    mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_PREVIOUS) {
          BusProvider.getInstance().post(new HideEvent());
          return true;
        } else {
          return false;
        }
      }
    });

    mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean focus) {
        if (!focus) {
          BusProvider.getInstance().post(new HideEvent());
        }
      }
    });

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1, 1);
    mEditText.setLayoutParams(lp);
    mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
    InputFilter[] filterArray = new InputFilter[1];
    filterArray[0] = new InputFilter.LengthFilter(Constants.NUM_ROOM_NUMBER_DIGITS);
    mEditText.setFilters(filterArray);
    mEditText.setImeOptions(EditorInfo.IME_ACTION_PREVIOUS);
    this.addView(mEditText, lp);

    mEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = getText();
        setDisplayText(text);
        if (text.length() == 6) {
          KeyboardUtils.hideKeyboard((Activity) mContext);
          try {
            int roomNumber = Integer.parseInt(text);
            BusProvider.getInstance().post(new RoomNumberInputFinishedEvent(roomNumber));
          } catch (NumberFormatException e) {
            Log.e(TAG, "bad number format: " + text);
          }
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });

    digitFieldContainer.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mEditText.requestFocus();
        KeyboardUtils.forceShowKeyboard((Activity) mContext);
      }
    });
  }

  public EditText getEditText() {
    return mEditText;
  }

  public void clear() {
    mEditText.setText("");
    setDisplayText("");
  }

  private void setDisplayText(String text) {
    char[] charArray = text.toCharArray();
    for (int charIndex = 0; charIndex < Constants.NUM_ROOM_NUMBER_DIGITS; charIndex++) {
      if (charIndex < charArray.length) {
        char c = charArray[charIndex];
        if (Character.isDigit(c)) {
          mDigitFields.get(charIndex).setText(String.valueOf(Character.isDigit(c) ? c : 0));
        }
      } else {
        mDigitFields.get(charIndex).setText("-");
      }
    }
  }

  public String getText() {
    if (mEditText != null) {
      if (mEditText.getText() != null) {
        return mEditText.getText().toString();
      } else {
        return "";
      }
    } else {
      return null;
    }
  }
}

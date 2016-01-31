package com.gamesmith.monsterboard.common.listeners;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by clocksmith on 1/30/16.
 */
public abstract class SimpleTextWatcher implements TextWatcher {
  @Override
  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    // Do nothing.
  }

  @Override
  public void afterTextChanged(Editable editable) {
    // Do nothing.
  }
}

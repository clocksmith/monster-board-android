package com.gamesmith.monsterboard.room;

import android.content.Context;
import android.util.AttributeSet;

import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.common.Constants;

/**
 * Created by clocksmith on 5/27/15.
 */
public class HpNumberPicker extends ScoreboardNumberPicker {
  public HpNumberPicker(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.setMaxValue(Constants.MAX_HP.intValue());
    this.setDividerColor(getResources().getColor(R.color.hp_color));
    this.setTextColor(getResources().getColor(R.color.hp_color));
  }
}

package com.gamesmith.scoreboard.room;

import android.content.Context;
import android.util.AttributeSet;

import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.common.Constants;
import com.gamesmith.scoreboard.common.ScoreboardNumberPicker;

/**
 * Created by clocksmith on 5/27/15.
 */
public class HpNumberPicker extends ScoreboardNumberPicker {
  public HpNumberPicker(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.setMaxValue(Constants.MAX_HP);
    this.setDividerColor(getResources().getColor(R.color.hp_color));
    this.setTextColor(getResources().getColor(R.color.hp_color));
  }
}

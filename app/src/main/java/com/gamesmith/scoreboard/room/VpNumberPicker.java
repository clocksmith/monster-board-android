package com.gamesmith.scoreboard.room;

import android.content.Context;
import android.util.AttributeSet;

import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.common.Constants;
import com.gamesmith.scoreboard.common.ScoreboardNumberPicker;

/**
 * Created by clocksmith on 5/27/15.
 */
public class VpNumberPicker extends ScoreboardNumberPicker {
  public VpNumberPicker(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.setMaxValue(Constants.MAX_VP);
    this.setDividerColor(getResources().getColor(R.color.vp_color));
    this.setTextColor(getResources().getColor(R.color.vp_color));
  }
}

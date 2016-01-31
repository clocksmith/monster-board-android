package com.gamesmith.monsterboard.room;

import android.content.Context;
import android.util.AttributeSet;

import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.common.Constants;

/**
 * Created by clocksmith on 5/27/15.
 */
public class VpNumberPicker extends ScoreboardNumberPicker {
  public VpNumberPicker(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.setMaxValue(Constants.MAX_VP.intValue());
    this.setDividerColor(getResources().getColor(R.color.vp_color));
    this.setTextColor(getResources().getColor(R.color.vp_color));
  }
}

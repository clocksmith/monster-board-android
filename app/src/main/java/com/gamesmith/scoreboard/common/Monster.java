package com.gamesmith.scoreboard.common;

import com.gamesmith.scoreboard.R;

/**
 * Created by clocksmith on 5/26/15.
 */
public enum Monster {
  CAPTAIN_FISH(R.drawable.captain_fish),
  DRAKONIS(R.drawable.drakonis),
  KONG(R.drawable.kong),
  MANTIS(R.drawable.mantis),
  ROB(R.drawable.rob),
  SHERIFF(R.drawable.sheriff);

  private int mImageResId;

  Monster(int imageResId) {
    mImageResId = imageResId;
  }

  public int getImageResId() {
    return mImageResId;
  }
}

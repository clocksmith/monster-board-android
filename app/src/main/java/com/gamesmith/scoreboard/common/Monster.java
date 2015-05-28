package com.gamesmith.scoreboard.common;

import com.gamesmith.scoreboard.R;

/**
 * Created by clocksmith on 5/26/15.
 */
public enum Monster {
  // King of New York
  CAPTAIN_FISH(R.drawable.captain_fish_384, R.drawable.captain_fish_128),
  DRAKONIS(R.drawable.drakonis_384, R.drawable.drakonis_128),
  KONG(R.drawable.kong_384, R.drawable.kong_128),
  MANTIS(R.drawable.mantis_384, R.drawable.mantis_128),
  ROB(R.drawable.rob_384, R.drawable.rob_128),
  SHERIFF(R.drawable.sheriff_384, R.drawable.sheriff_128);

  private int mLargeImageResId;
  private int mSmallImageResId;

  Monster(int largeImageResId, int smallImageResId) {
    mLargeImageResId = largeImageResId;
    mSmallImageResId = smallImageResId;
  }

  public int getLargeImageResId() {
    return mLargeImageResId;
  }

  public int getSmallImageResId() {
    return mSmallImageResId;
  }

  public static Monster getEnum(String value) {
    return valueOf(value.replace(' ', '_'));
  }

  public String getName() {
    return name().replace('_', ' ');
  }
}

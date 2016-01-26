package com.gamesmith.scoreboard.common;

import com.gamesmith.scoreboard.R;

/**
 * Created by clocksmith on 5/26/15.
 */
public enum Monster {

  // King of Tokyo
  ALIENOID(R.drawable.alienoid_384, R.string.king_of_tokyo),
  CYBER_BUNNY(R.drawable.cyber_bunny_384, R.string.king_of_tokyo),
  GIGAZAUR(R.drawable.gigazaur_384, R.string.king_of_tokyo),
  KRAKEN(R.drawable.kraken_384, R.string.king_of_tokyo),
  MEKA_DRAGON(R.drawable.meka_dragon_384, R.string.king_of_tokyo),
  THE_KING(R.drawable.the_king_384, R.string.king_of_tokyo),

  // King of New York
  CAPTAIN_FISH(R.drawable.captain_fish_384, R.string.king_of_new_york),
  DRAKONIS(R.drawable.drakonis_384, R.string.king_of_new_york),
  KONG(R.drawable.kong_384, R.string.king_of_new_york),
  MANTIS(R.drawable.mantis_384, R.string.king_of_new_york),
  ROB(R.drawable.rob_384, R.string.king_of_new_york),
  SHERIFF(R.drawable.sheriff_384, R.string.king_of_new_york),

  // King of Toyko: Halloween
  BOOGIE_WOOGIE(R.drawable.boogie_woogie_384, R.string.king_of_tokyo_halloween),
  PUMPKIN_JACK(R.drawable.pumpkin_jack_384, R.string.king_of_tokyo_halloween),

  // King of Tokyo: Power up
  PANDAKAI(R.drawable.pandakai_384, R.string.king_of_tokyo_power_up);

  private int mImageResId;
  private int mSourceTitleResId;

  Monster(int imageResId, int sourceTitleResId) {
    mImageResId = imageResId;
    mSourceTitleResId = sourceTitleResId;
  }

  public int getImageResId() {
    return mImageResId;
  }

  public int getSourceTitleResId() {
    return mSourceTitleResId;
  }

  public static Monster getEnum(String value) {
    return valueOf(value.replace(' ', '_'));
  }

  public String getName() {
    return name().replace('_', ' ');
  }
}

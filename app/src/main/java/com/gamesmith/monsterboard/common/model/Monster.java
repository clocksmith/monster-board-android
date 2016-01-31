package com.gamesmith.monsterboard.common.model;

import com.gamesmith.monsterboard.R;

/**
 * Created by clocksmith on 5/26/15.
 */
public enum Monster {

  // King of Tokyo
  ALIENOID(R.drawable.alienoid_384, R.drawable.alienoid_128, R.string.king_of_tokyo),
  CYBER_BUNNY(R.drawable.cyber_bunny_384, R.drawable.cyber_bunny_128, R.string.king_of_tokyo),
  GIGAZAUR(R.drawable.gigazaur_384, R.drawable.gigazaur_128, R.string.king_of_tokyo),
  KRAKEN(R.drawable.kraken_384, R.drawable.kraken_128, R.string.king_of_tokyo),
  MEKA_DRAGON(R.drawable.meka_dragon_384, R.drawable.meka_dragon_128, R.string.king_of_tokyo),
  THE_KING(R.drawable.the_king_384, R.drawable.the_king_128, R.string.king_of_tokyo),

  // King of New York
  CAPTAIN_FISH(R.drawable.captain_fish_384, R.drawable.captain_fish_128, R.string.king_of_new_york),
  DRAKONIS(R.drawable.drakonis_384, R.drawable.drakonis_128, R.string.king_of_new_york),
  KONG(R.drawable.kong_384, R.drawable.kong_128, R.string.king_of_new_york),
  MANTIS(R.drawable.mantis_384, R.drawable.mantis_128, R.string.king_of_new_york),
  ROB(R.drawable.rob_384, R.drawable.rob_128, R.string.king_of_new_york),
  SHERIFF(R.drawable.sheriff_384, R.drawable.sheriff_128, R.string.king_of_new_york),

  // King of Toyko: Halloween
  BOOGIE_WOOGIE(R.drawable.boogie_woogie_384, R.drawable.boogie_woogie_128, R.string.king_of_tokyo_halloween),
  PUMPKIN_JACK(R.drawable.pumpkin_jack_384, R.drawable.pumpkin_jack_128, R.string.king_of_tokyo_halloween),

  // King of Tokyo: Power up
  PANDAKAI(R.drawable.pandakai_384, R.drawable.pandakai_128, R.string.king_of_tokyo_power_up);

  private int mImageResId;
  private int mSmallImageResId;
  private int mSourceTitleResId;

  Monster(int imageResId, int smallImageResId, int sourceTitleResId) {
    mImageResId = imageResId;
    mSmallImageResId = smallImageResId;
    mSourceTitleResId = sourceTitleResId;
  }

  public int getImageResId() {
    return mImageResId;
  }

  public int getSmallImageResId() {
    return mSmallImageResId;
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

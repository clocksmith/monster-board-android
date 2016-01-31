package com.gamesmith.monsterboard.common.listeners;

import android.support.v4.view.ViewPager;

/**
 * Created by clocksmith on 1/30/16.
 */
public abstract class SimplePageChangeListener implements ViewPager.OnPageChangeListener {
  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    // Do nothing.
  }

  @Override
  public void onPageScrollStateChanged(int state) {
    // Do nothing.
  }
}

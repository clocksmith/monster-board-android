package com.gamesmith.scoreboard.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by clocksmith on 10/17/15.
 */
public class AnimationUtils {
  private static final String TAG = AnimationUtils.class.getSimpleName();

  public static abstract class OnAnimationEndOnlyListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation) {
      // Do nothing.
    }

    @Override
    public void onAnimationCancel(Animator animation) {
      // Do nothing.
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
      // Do nothing.
    }
  }

  public interface OnAnimationEndListener {
    void onAnimationEnd(Animator animation);
  }

  public static void animate(List<Animator> animators,
      int durationMs,
      final OnAnimationEndListener onAnimationEndListener) {
    if (!animators.isEmpty()) {
      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.playTogether(animators);
      animatorSet.setDuration(durationMs);
      animatorSet.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
          // Do nothing.
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          if (onAnimationEndListener != null) {
            onAnimationEndListener.onAnimationEnd(animation);
          }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
          // Do nothing.
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
          // Do nothing.
        }
      });
      animatorSet.start();
    }
  }

  // Full service animators.

  public static void pulse(final View view, final float scale) {
    List<Animator> growAnimators = Lists.newArrayList();
    growAnimators.addAll(getScaleCenterAnimators(view, 1f, scale));
    animate(
        growAnimators,
        Constants.PULSE_ANIMATION_DURATION_MILLIS / 2,
        new OnAnimationEndListener() {
          @Override
          public void onAnimationEnd(Animator animation) {
            List<Animator> ungrowAnimators = Lists.newArrayList();
            ungrowAnimators.addAll(getScaleCenterAnimators(view, scale, 1f));
            animate(
                ungrowAnimators,
                Constants.PULSE_ANIMATION_DURATION_MILLIS / 2,
                null);
          }
        });
  }

  private static List<Animator> getScaleCenterAnimators(View view, float startScale, float endScale) {
    List<Animator> animators = Lists.newArrayList();
    view.setPivotX(view.getWidth() / 2);
    ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
    animators.add(scaleXAnimator);
    view.setPivotY(view.getHeight() / 2);
    ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
    animators.add(scaleYAnimator);
    return animators;
  }
}
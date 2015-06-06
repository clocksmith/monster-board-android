package com.gamesmith.scoreboard.start;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gamesmith.scoreboard.common.BusProvider;
import com.gamesmith.scoreboard.common.Constants;
import com.gamesmith.scoreboard.common.KeyboardUtils;
import com.gamesmith.scoreboard.R;

/**
 * Created by clocksmith on 5/24/15.
 */
public class CreateOrJoinView extends FrameLayout {
  private static final String TAG = CreateOrJoinView.class.getSimpleName();

  public class CreateButtonClickedEvent {}

  private Context mContext;
  private boolean mIsRoomNumberInputVisible;

  private LinearLayout mButtonContainer;
  private Button mCreateButton;
  private Button mJoinButton;
  private FrameLayout mRoomNumberInputContainer;
  private ImageView mCloseIcon;
  private RoomNumberInput mRoomNumberInput;

  public CreateOrJoinView(Context context) {
    this(context, null);
  }

  public CreateOrJoinView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CreateOrJoinView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mContext = context;
    mIsRoomNumberInputVisible = false;

    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = layoutInflater.inflate(R.layout.view_create_or_join, this, true);

    mButtonContainer = (LinearLayout) view.findViewById(R.id.view_create_or_join_buttonContainer);
    mCreateButton = (Button) view.findViewById(R.id.view_create_or_join_createButton);
    mJoinButton = (Button) view.findViewById(R.id.view_create_or_join_joinButton);
    mRoomNumberInputContainer = (FrameLayout) view.findViewById(R.id.view_create_or_join_roomNumberInputContainer);
//    mCloseIcon = (ImageView) view.findViewById(R.id.view_create_or_join_closeIcon);
    mRoomNumberInput = (RoomNumberInput) view.findViewById(R.id.view_create_or_join_roomNumberInput);

    mRoomNumberInputContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        showStartingState();
        mRoomNumberInputContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });

    mCreateButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        BusProvider.getInstance().post(new CreateButtonClickedEvent());
      }
    });

    mJoinButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mIsRoomNumberInputVisible) {
          hideRoomNumberInput();
        } else {
          showRoomNumberInput();
        }
      }
    });

//    mCloseIcon.setOnClickListener(new OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        hideRoomNumberInput();
//      }
//    });
  }

  public void showStartingState() {
    mIsRoomNumberInputVisible = false;
    mButtonContainer.setTranslationX(0);
    mRoomNumberInputContainer.setTranslationX(mRoomNumberInputContainer.getWidth());
  }

  private void hideRoomNumberInput() {
    mIsRoomNumberInputVisible = false;
    KeyboardUtils.hideKeyboard((Activity) mContext, mRoomNumberInput.getEditText());
    animate(-mButtonContainer.getWidth(), 0, 0, mRoomNumberInputContainer.getWidth());
  }

  private void showRoomNumberInput() {
    mIsRoomNumberInputVisible = true;
    mRoomNumberInput.requestFocus();
    mRoomNumberInput.getEditText().requestFocus();
    KeyboardUtils.forceShowKeyboard((Activity) mContext);
    mRoomNumberInput.clear();
    animate(0, -mButtonContainer.getWidth(), mRoomNumberInputContainer.getWidth(), 0);
  }

  private void animate(int buttonStartX, int buttonEndX, int pinStartX, int pinEndX) {
    ObjectAnimator buttonAnimator = ObjectAnimator.ofFloat(mButtonContainer, "translationX", buttonStartX, buttonEndX);
    ObjectAnimator pinInputContainerAnimator =
        ObjectAnimator.ofFloat(mRoomNumberInputContainer, "translationX", pinStartX, pinEndX);

    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(buttonAnimator, pinInputContainerAnimator);
    animatorSet.setDuration(Constants.DEFAULT_ANIMATION_TIME_MILLIS);
    animatorSet.start();
  }
}

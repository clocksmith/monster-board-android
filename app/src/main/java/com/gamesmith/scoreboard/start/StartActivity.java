package com.gamesmith.scoreboard.start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.gamesmith.scoreboard.common.Monster;
import com.gamesmith.scoreboard.common.NameProvider;
import com.gamesmith.scoreboard.common.firebase.FirebaseUtils;
import com.gamesmith.scoreboard.common.firebase.Player;
import com.gamesmith.scoreboard.common.BusProvider;
import com.gamesmith.scoreboard.common.Constants;
import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.room.RoomActivity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Random;


public class StartActivity extends Activity {
  private static final String TAG = StartActivity.class.getSimpleName();

  // For a quick and dirty "infinite" pager
  private static final int BIG_NUMBER = Monster.values().length * 999999;
  private static final int MIDDLE_NUMBER = BIG_NUMBER / 2;

  private Firebase mFirebase;
  private List<String> mDefaultNames;
  private Random mRandom;
  private int mJoiningPlayerId;
  private Monster mSelectedMonster;

  private EditText mPlayerInput;
  private ViewPager mMonsterChooser;

  ProgressDialog mProgressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);

    BusProvider.getInstance().register(this);
    Firebase.setAndroidContext(this);
    mFirebase = new Firebase(Constants.FIREBASE_URL);
    mRandom = new Random();

    mPlayerInput = (EditText) findViewById(R.id.activity_start_playerInput);
    mMonsterChooser = (ViewPager) findViewById(R.id.activity_start_monsterChooser);

    mPlayerInput.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

    mMonsterChooser.setAdapter(new MonsterPagerAdapter(this));
    int padding = this.getResources().getDimensionPixelOffset(R.dimen.view_pager_padding);
    mMonsterChooser.setHorizontalFadingEdgeEnabled(true);
    mMonsterChooser.setFadingEdgeLength(padding);
    mMonsterChooser.setCurrentItem(MIDDLE_NUMBER + (new Random()).nextInt(Monster.values().length));
    mSelectedMonster = Monster.values()[mMonsterChooser.getCurrentItem() % Monster.values().length];
    mMonsterChooser.setOffscreenPageLimit(5);

    mMonsterChooser.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Do nothing.
      }

      @Override
      public void onPageSelected(int position) {
        mSelectedMonster = Monster.values()[position % Monster.values().length];
      }

      @Override
      public void onPageScrollStateChanged(int state) {
        // Do nothing.
      }
    });

    mDefaultNames = NameProvider.getNames(this);
    setRandomName();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    BusProvider.getInstance().unregister(this);
  }

  private void createRoom() {
    mProgressDialog = ProgressDialog.show(this, "Creating room", "hold tight...");
    final int roomNumber = getRandomRoomNumber();

    FirebaseUtils.getRooms(mFirebase).runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData currentData) {
        String roomNumberString = String.valueOf(roomNumber);
        if (currentData.hasChild(roomNumberString)
            && !((List) currentData.child(roomNumberString).getValue()).isEmpty()) {
          handleRoomTaken();
          return Transaction.abort();
        } else {
          List<Player> players = ImmutableList.of(getNewUser());
          currentData.child(roomNumberString).setValue(players);
          return Transaction.success(currentData);
        }
      }

      @Override
      public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
        if (mProgressDialog != null) {
          mProgressDialog.dismiss();
        }
        if (committed) {
          startRoomActivity(roomNumber, 0);
        }
      }
    });
  }

  private void joinRoom(int roomNumber) {
    if (!this.isFinishing()) {
      mProgressDialog = ProgressDialog.show(this, "Joining room", "hold tight...");
    }

    FirebaseUtils.getRoom(mFirebase, roomNumber).runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData currentData) {
        if (currentData != null) {
          Player newPlayer = getNewUser();

          int highestPlayerId = -1;
          for (MutableData playerData : currentData.getChildren()) {
            int playerId = Integer.parseInt(playerData.getKey());
            if (playerId > highestPlayerId) {
              highestPlayerId = playerId;
            }
          }
          mJoiningPlayerId = highestPlayerId + 1;

          currentData.child(String.valueOf(mJoiningPlayerId)).setValue(newPlayer);
          return Transaction.success(currentData);
        } else {
          handleInvalidRoom();
          return Transaction.abort();
        }
      }

      @Override
      public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
        if (mProgressDialog != null) {
          mProgressDialog.dismiss();
        }
        if (committed) {
          startRoomActivity(Integer.parseInt(currentData.getKey()), mJoiningPlayerId);
        } else {
          // TODO(clocksmith)
          Log.w(TAG, "not committed");
        }
      }
    });
  }

  public void handleRoomTaken() {
    // TODO(clocksmith): Do something in this extremely rare case.
    Log.w(TAG, "handleRoomTaken");
  }

  public void handleInvalidRoom() {
    // TODO(clocksmith)
    Log.w(TAG, "handleInvalidRoom");
  }

  private int getRandomRoomNumber() {
    return mRandom.nextInt(Constants.MAX_ROOM_NUMBER - Constants.MIN_ROOM_NUMBER + 1) + Constants.MIN_ROOM_NUMBER;
  }

  private Player getNewUser() {
    Player newPlayer = new Player();
    newPlayer.name = mPlayerInput.getText().toString();
    newPlayer.monster = mSelectedMonster.getName();
    newPlayer.hp = Constants.STARTING_HP;
    newPlayer.vp = Constants.STARTING_VP;
    return newPlayer;
  }

  private void startRoomActivity(int roomNumber, int playerId) {
    Log.d(TAG, "startRoomActivity()");
    Intent intent = new Intent(StartActivity.this, RoomActivity.class);
    intent.putExtra(Constants.ROOM_NUMBER, roomNumber);
    intent.putExtra(Constants.PLAYER_ID, playerId);
    startActivity(intent);
    finish();
  }

  private void setRandomName() {
    if (mPlayerInput.getText() == null || mPlayerInput.getText().toString().isEmpty()) {
      mPlayerInput.setText(mDefaultNames.get(mRandom.nextInt(mDefaultNames.size())));
    }
  }

  @Subscribe
  public void on(CreateOrJoinView.CreateButtonClickedEvent event) {
    createRoom();
  }

  @Subscribe
  public void on(RoomNumberInput.RoomNumberInputFinishedEvent event) {
    Log.d(TAG, "on(RoomNumberInput.RoomNumberInputFinishedEvent()");
    joinRoom(event.roomNumber);
  }

  private class MonsterPagerAdapter extends PagerAdapter {
    private Context mContext;

    public MonsterPagerAdapter(Context context) {
      mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      Monster monster = Monster.values()[position % Monster.values().length];

//      View view = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_dropdown_monster, container);
//      ((ImageView) view.findViewById(R.id.spinner_item_dropdown_monster_imageView))
//          .setImageDrawable(ContextCompat.getDrawable(mContext, monster.getImageResId()));
//      ((TextView) view.findViewById(R.id.spinner_item_dropdown_monster_textView)).setText(monster.getName());
//      int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.image_padding);
//      view.setPadding(padding, padding, padding, padding);
//      container.addView(view);
//      return view;

      ImageView imageView = new ImageView(mContext);
      imageView.setLayoutParams(new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT));
      int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.image_padding);

      imageView.setPadding(padding, padding, padding, padding);
      imageView.setImageDrawable(ContextCompat.getDrawable(mContext, monster.getImageResId()));
      container.addView(imageView);
      return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      unbindDrawables((View) object);
      container.removeView((View) object);
    }

    @Override
    public int getCount() {
      return BIG_NUMBER;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

//    @Override public float getPageWidth(int position) {
//      return(0.5f);
//    }



    private void unbindDrawables(View view) {
      if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
      }

      if (view instanceof ImageView) {
        ImageView imageView = (ImageView) view;
        imageView.setImageBitmap(null);
      } else if (view instanceof ViewGroup) {
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
          unbindDrawables(viewGroup.getChildAt(i));
        }

        if (!(view instanceof AdapterView)) {
          viewGroup.removeAllViews();
        }
      }
    }
  }
}

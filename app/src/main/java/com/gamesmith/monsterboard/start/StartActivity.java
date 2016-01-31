package com.gamesmith.monsterboard.start;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.gamesmith.monsterboard.common.listeners.SimpleTextWatcher;
import com.gamesmith.monsterboard.extra.BeatpadActivity;
import com.gamesmith.monsterboard.common.model.Monster;
import com.gamesmith.monsterboard.common.providers.NameProvider;
import com.gamesmith.monsterboard.common.utils.PreferencesUtils;
import com.gamesmith.monsterboard.common.utils.FirebaseUtils;
import com.gamesmith.monsterboard.common.model.Player;
import com.gamesmith.monsterboard.common.providers.BusProvider;
import com.gamesmith.monsterboard.common.Constants;
import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.room.RoomActivity;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Random;


public class StartActivity extends AppCompatActivity {
  private static final String TAG = StartActivity.class.getSimpleName();

  // Hack for infinite view pager.
  private static final int BIG_NUMBER = Monster.values().length * 1000;
  private static final int MIDDLE_NUMBER = BIG_NUMBER / 2; // must be multiple of length.

  private ProgressDialog mProgressDialog;

  private int mJoiningPlayerId;

  private CreateOrJoinView mCreateOrJoinView;
  private EditText mPlayerInput;
  private ViewPager mMonsterChooser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
    }

    BusProvider.getInstance().register(this);
    Firebase.setAndroidContext(this);

    mCreateOrJoinView = (CreateOrJoinView) findViewById(R.id.activity_start_createOrJoinView);
    mPlayerInput = (EditText) findViewById(R.id.activity_start_playerInput);
    mMonsterChooser = (ViewPager) findViewById(R.id.activity_start_monsterChooser);

    mPlayerInput.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

    // Easter egg.
    mPlayerInput.addTextChangedListener(new SimpleTextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().equals("BEATPAD")) {
          startActivity(new Intent(StartActivity.this, BeatpadActivity.class));
        }
      }
    });

    mMonsterChooser.setAdapter(new MonsterPagerAdapter(this));
    mMonsterChooser.setHorizontalFadingEdgeEnabled(true);
    mMonsterChooser.setFadingEdgeLength(getResources().getDimensionPixelOffset(R.dimen.view_pager_padding));
    mMonsterChooser.setOffscreenPageLimit(3);
  }

  @Override
  public void onResume() {
    super.onResume();

    String playerName = PreferencesUtils.getPlayerName(this);
    if (Strings.isNullOrEmpty(playerName)) {
      setRandomName();
    } else {
      mPlayerInput.setText(playerName);
    }

    String monsterName = PreferencesUtils.getMonsterName(this);
    if (Strings.isNullOrEmpty(monsterName)) {
      mMonsterChooser.setCurrentItem(MIDDLE_NUMBER + (new Random()).nextInt(Monster.values().length), false);
    } else {
      mMonsterChooser.setCurrentItem(MIDDLE_NUMBER + Monster.getEnum(monsterName).ordinal(), false);
    }

    final int roomNumber = PreferencesUtils.getRoomNumber(this);
    final int playerId = PreferencesUtils.getPlayerId(this);

    if (roomNumber != -1 && playerId != -1) {
      mProgressDialog = ProgressDialog.show(this, "Rejoining room", "hold tight...");
      FirebaseUtils.getPlayer(roomNumber, playerId).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          if (mProgressDialog != null) {
            mProgressDialog.dismiss();
          }

          if (dataSnapshot != null && dataSnapshot.exists()) {
            Firebase playerFirebase = FirebaseUtils.getPlayer(roomNumber, playerId);
            playerFirebase.child("name").setValue(getPlayerName());
            playerFirebase.child("monster").setValue(getMonster().getName());
            startRoomActivity(roomNumber, playerId);
          } else {
            Toast.makeText(StartActivity.this, "Could not rejoin!", Toast.LENGTH_SHORT).show();
          }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
          Log.w(TAG, "onCancelled()");
          if (mProgressDialog != null) {
            mProgressDialog.dismiss();
          }
          Toast.makeText(StartActivity.this, "Could not rejoin!", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    BusProvider.getInstance().unregister(this);
  }

  private void createRoom() {
    mProgressDialog = ProgressDialog.show(this, "Creating room", "hold tight...");
    final int roomNumber = getRandomRoomNumber();

    FirebaseUtils.getRooms().runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData currentData) {
        String roomNumberString = String.valueOf(roomNumber);
        if (currentData.hasChild(roomNumberString)
            && !((List) currentData.child(roomNumberString).getValue()).isEmpty()) {
          handleRoomTaken();
          return Transaction.abort();
        } else {
          List<Player> players = ImmutableList.of(createNewPlayer());
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
        } else {
          Toast.makeText(StartActivity.this, "Could not create room!", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void joinRoom(int roomNumber) {
    if (!this.isFinishing()) {
      mProgressDialog = ProgressDialog.show(this, "Joining room", "hold tight...");
    }

    FirebaseUtils.getRoom(roomNumber).runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData currentData) {
        if (currentData != null) {
          int highestPlayerId = -1;
          for (MutableData playerData : currentData.getChildren()) {
            int playerId = Integer.parseInt(playerData.getKey());
            if (playerId > highestPlayerId) {
              highestPlayerId = playerId;
            }
          }
          mJoiningPlayerId = highestPlayerId + 1;

          currentData.child(String.valueOf(mJoiningPlayerId)).setValue(createNewPlayer());
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
    Log.w(TAG, "handleRoomTaken");
    Toast.makeText(StartActivity.this, "Could not create room!", Toast.LENGTH_SHORT).show();
  }

  public void handleInvalidRoom() {
    Log.w(TAG, "handleInvalidRoom");
    Toast.makeText(StartActivity.this, "Could not create room!", Toast.LENGTH_SHORT).show();
  }

  private int getRandomRoomNumber() {
    return (new Random()).nextInt(
        Constants.MAX_ROOM_NUMBER - Constants.MIN_ROOM_NUMBER + 1) + Constants.MIN_ROOM_NUMBER;
  }

  private Player createNewPlayer() {
    Player player = new Player(
        getPlayerName(),
        getMonster().getName(),
        Constants.STARTING_HP,
        Constants.STARTING_VP
    );
    return player;
  }

  private String getPlayerName() {
    return mPlayerInput.getText().toString();
  }

  private Monster getMonster(int position) {
    return Monster.values()[position % Monster.values().length];
  }

  private Monster getMonster() {
    return getMonster(mMonsterChooser.getCurrentItem());
  }

  private void startRoomActivity(int roomNumber, int playerId) {
    PreferencesUtils.setRoomNumber(this, roomNumber);
    PreferencesUtils.setPlayerId(this, playerId);
    PreferencesUtils.setPlayerName(this, getPlayerName());
    PreferencesUtils.setMonsterName(this, getMonster().getName());

    Intent intent = new Intent(this, RoomActivity.class);
    intent.putExtra(Constants.ROOM_NUMBER, roomNumber);
    intent.putExtra(Constants.PLAYER_ID, playerId);
    startActivity(intent);
  }

  private void setRandomName() {
    List<String> defaultNames = NameProvider.getNames(this);
    if (mPlayerInput.getText() == null || getPlayerName().isEmpty()) {
      mPlayerInput.setText(defaultNames.get((new Random()).nextInt(defaultNames.size())));
    }
  }

  @Subscribe
  public void on(CreateOrJoinView.CreateButtonClickedEvent event) {
    createRoom();
  }

  @Subscribe
  public void on(RoomNumberInput.RoomNumberInputFinishedEvent event) {
    mCreateOrJoinView.hideRoomNumberInput();
    joinRoom(event.roomNumber);
  }

  @Subscribe
  public void on(RoomNumberInput.HideEvent event) {
    mCreateOrJoinView.hideRoomNumberInput();
  }

  private class MonsterPagerAdapter extends PagerAdapter {
    private Context mContext;

    public MonsterPagerAdapter(Context context) {
      mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      Monster monster = getMonster(position);

      MonsterView monsterView = new MonsterView(mContext, monster);
      monsterView.setLayoutParams(new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT));
      int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.image_padding);

      monsterView.setPadding(padding, padding, padding, padding);
      container.addView(monsterView);
      return monsterView;
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

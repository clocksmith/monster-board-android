package com.gamesmith.monsterboard.room;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.common.model.Player;
import com.gamesmith.monsterboard.common.providers.BusProvider;
import com.gamesmith.monsterboard.common.model.Monster;

/**
 * Created by clocksmith on 5/25/15.
 */
public class MainPlayerCard extends LinearLayout {
  public class MainMonsterChangedEvent {
    public final Monster monster;

    public MainMonsterChangedEvent(Monster monster) {
      this.monster = monster;
    }
  }

  public class MainHpChangedEvent {
    public final int hp;

    public MainHpChangedEvent(int hp) {
      this.hp = hp;
    }
  }

  public class MainVpChangedEvent {
    public final int vp;

    public MainVpChangedEvent(int vp) {
      this.vp = vp;
    }
  }

  private TextView mPlayerName;
  private Spinner mMonsterSpinner;
  private TextView mMonsterName;
  private HpNumberPicker mHpPicker;
  private VpNumberPicker mVpPicker;

  private OnMonsterItemSelectedListener mOnMonsterItemSelectedListener;
  private OnHpChangedListener mOnHpChangedListener;
  private OnVpChangedListener mOnVpChangedListener;

  public MainPlayerCard(Context context) {
    this(context, null);
  }

  public MainPlayerCard(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MainPlayerCard(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = layoutInflater.inflate(R.layout.view_main_player_card, this, true);

    mPlayerName = (TextView) view.findViewById(R.id.view_main_player_card_playerName);
    mMonsterSpinner = (Spinner) view.findViewById(R.id.view_main_player_card_monsterSpinner);
    mMonsterName = (TextView) view.findViewById(R.id.view_main_player_card_monsterName);
    mHpPicker = (HpNumberPicker) view.findViewById(R.id.view_main_player_card_hp);
    mVpPicker = (VpNumberPicker) view.findViewById(R.id.view_main_player_card_vp);

    mOnMonsterItemSelectedListener = new OnMonsterItemSelectedListener();
    mOnHpChangedListener = new OnHpChangedListener();
    mOnVpChangedListener = new OnVpChangedListener();

    mMonsterSpinner.setAdapter(new MonsterSpinnerAdapter(this.getContext(), Monster.values()));
    mHpPicker.setTextSize(getResources().getDimensionPixelSize(R.dimen.main_player_picker_text_size));
    mVpPicker.setTextSize(getResources().getDimensionPixelOffset(R.dimen.main_player_picker_text_size));
  }

  public void setPlayer(Player player) {
    setName(player.getName());
    setMonster(player.getMonster());
    setHp(player.getHp().intValue());
    setVp(player.getVp().intValue());
  }

  public void setName(String name) {
    mPlayerName.setText(name);
  }

  public void setMonster(String monster) {
    mMonsterSpinner.setOnItemSelectedListener(null);
    mMonsterSpinner.setSelection(Monster.getEnum(monster).ordinal());
    mMonsterName.setText(monster);
    mMonsterSpinner.setOnItemSelectedListener(mOnMonsterItemSelectedListener);
  }

  public void setHp(int hp) {
    mHpPicker.setOnValueChangedListener(null);
    mHpPicker.setValue(hp);
    mHpPicker.setOnValueChangedListener(mOnHpChangedListener);
  }

  public void setVp(int vp) {
    mVpPicker.setOnValueChangedListener(null);
    mVpPicker.setValue(vp);
    mVpPicker.setOnValueChangedListener(mOnVpChangedListener);
  }

  private class MonsterSpinnerAdapter extends ArrayAdapter<Monster> {
    public MonsterSpinnerAdapter(Context context, Monster[] objects) {
      super(context, 0, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        convertView = layoutInflater.inflate(R.layout.spinner_item_dropdown_monster, null);
      }

      Monster monster = getItem(position);
      ((ImageView) convertView.findViewById(R.id.spinner_item_dropdown_monster_imageView))
          .setImageDrawable(ContextCompat.getDrawable(this.getContext(), monster.getSmallImageResId()));
      ((TextView) convertView.findViewById(R.id.spinner_item_dropdown_monster_name)).setText(monster.getName());
      String sourceText = "(" + MainPlayerCard.this.getContext().getString(monster.getSourceTitleResId()) + ")";
      ((TextView) convertView.findViewById(R.id.spinner_item_dropdown_monster_source)).setText(sourceText);

      return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        convertView = layoutInflater.inflate(R.layout.spinner_item_monster, null);
      }

      Monster monster = getItem(position);
      ((ImageView) convertView.findViewById(R.id.spinner_item_monster_imageView))
          .setImageDrawable(ContextCompat.getDrawable(this.getContext(), monster.getImageResId()));

      return convertView;
    }
  }

  private class OnMonsterItemSelectedListener implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      Monster monster = (Monster) parent.getItemAtPosition(position);
      mMonsterName.setText(monster.getName());
      BusProvider.getInstance().post(new MainMonsterChangedEvent(monster));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
      // Do nothing.
    }
  }

  private class OnHpChangedListener implements NumberPicker.OnValueChangeListener {
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
      BusProvider.getInstance().post(new MainHpChangedEvent(newVal));
    }
  }

  private class OnVpChangedListener implements NumberPicker.OnValueChangeListener {
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
      BusProvider.getInstance().post(new MainVpChangedEvent(newVal));
    }
  }
}

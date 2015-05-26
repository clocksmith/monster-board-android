package com.gamesmith.scoreboard.room;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.common.Monster;

import java.util.List;

/**
 * Created by clocksmith on 5/25/15.
 */
public class MainUserCard extends LinearLayout {
  private TextView mNameTextView;
  private Spinner mMonsterSpinner;
  private TextView mMonsterNameTextView;

  public MainUserCard(Context context) {
    this(context, null);
  }

  public MainUserCard(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MainUserCard(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = layoutInflater.inflate(R.layout.view_main_user_card, this, true);

    mNameTextView = (TextView) view.findViewById(R.id.view_main_user_card_name);
    mMonsterSpinner = (Spinner) view.findViewById(R.id.view_main_user_card_monsterSpinner);
    mMonsterNameTextView = (TextView) view.findViewById(R.id.view_main_user_card_monsterName);

    mMonsterSpinner.setAdapter(new MonsterSpinnerAdapter(this.getContext(), Monster.values()));
  }

  public void setName(String name) {
    mNameTextView.setText(name);
  }

  public void setMonster(String monster) {
    mMonsterNameTextView.setText(monster);
  }

  private class MonsterSpinnerAdapter extends ArrayAdapter<Monster> {
    public MonsterSpinnerAdapter(Context context, Monster[] objects) {
      super(context, 0, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        convertView = layoutInflater.inflate(R.layout.spinner_item_dropdown_monster, parent);
      }

      Monster monster = getItem(position);
      ((ImageView) convertView.findViewById(R.id.spinner_item_dropdown_monster_imageView))
          .setImageDrawable(this.getContext().getDrawable(monster.getImageResId()));
      ((TextView) convertView.findViewById(R.id.spinner_item_dropdown_monster_textView)).setText(monster.name());

      return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        convertView = layoutInflater.inflate(R.layout.spinner_item_monster, parent);
      }

      Monster monster = getItem(position);
      ((ImageView) convertView.findViewById(R.id.spinner_item_monster_imageView))
          .setImageDrawable(this.getContext().getDrawable(monster.getImageResId()));

      return convertView;
    }
  }
}

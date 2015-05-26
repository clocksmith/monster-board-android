package com.gamesmith.scoreboard.room;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gamesmith.scoreboard.R;

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
  }

  public void setName(String name) {
    mNameTextView.setText(name);
  }

  public void setMonster(String monster) {
    mMonsterNameTextView.setText(monster);
  }
}

package com.gamesmith.monsterboard.start;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.common.model.Monster;

/**
 * Created by clocksmith on 1/29/16.
 */
public class MonsterView extends LinearLayout {
  public MonsterView(Context context, Monster monster) {
    super(context);

    LayoutInflater.from(context).inflate(R.layout.view_monster, this);

    ImageView imageView = (ImageView) findViewById(R.id.view_monster_imageView);
    TextView monsterName = (TextView) findViewById(R.id.view_monster_monsterName);
    TextView monsterSource = (TextView) findViewById(R.id.view_monster_monsterSource);

    imageView.setImageDrawable(ContextCompat.getDrawable(context, monster.getImageResId()));
    monsterName.setText(monster.getName());
    monsterSource.setText(context.getString(monster.getSourceTitleResId()));
  }
}

package com.gamesmith.scoreboard.room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.common.AnimationUtils;
import com.gamesmith.scoreboard.common.ScoreboardNumberPicker;
import com.gamesmith.scoreboard.common.Monster;
import com.gamesmith.scoreboard.common.firebase.Player;

/**
 * Created by clocksmith on 5/25/15.
 */
public class UserRecyclerViewHolder extends RecyclerView.ViewHolder {
  private static final String TAG = UserRecyclerViewHolder.class.getSimpleName();

  private Context mContext;

  private TextView mPlayerName;
  private ImageView mMonsterImage;
  private TextView mMonsterName;
  private ImageView mHpIcon;
  private ImageView mVpIcon;
  private TextView mHp;
  private TextView mVp;

  public UserRecyclerViewHolder(View itemView) {
    super(itemView);
    mContext = itemView.getContext();

    mPlayerName = (TextView) itemView.findViewById(R.id.list_item_player_playerName);
    mMonsterImage = (ImageView) itemView.findViewById(R.id.list_item_player_monsterImage);
    mMonsterName = (TextView) itemView.findViewById(R.id.list_item_player_monsterName);
    mHpIcon = (ImageView) itemView.findViewById(R.id.list_item_player_hpIcon);
    mVpIcon = (ImageView) itemView.findViewById(R.id.list_item_player_vpIcon);
    mHp = (TextView) itemView.findViewById(R.id.list_item_player_hp);
    mVp = (TextView) itemView.findViewById(R.id.list_item_player_vp);
  }

  public void updatePlayerData(Player player) {
    mPlayerName.setText(player.name);
    mMonsterImage.setImageDrawable(mContext.getDrawable(Monster.getEnum(player.monster).getSmallImageResId()));
    mMonsterName.setText(player.monster);
    updatePointText(mVp, mVpIcon, player.vp);
    updatePointText(mHp, mHpIcon, player.hp);
  }

  private void updatePointText(TextView pointTextView, ImageView iconView, int newValue) {
    String newText = String.valueOf(newValue);
    if (!pointTextView.getText().toString().equals(newText) && iconView.getScaleX() == 1f) {
      Log.d(TAG, "pulse");
      AnimationUtils.pulse(iconView, 1.5f);
    }
    pointTextView.setText(newText);
  }
}

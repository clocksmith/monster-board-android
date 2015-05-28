package com.gamesmith.scoreboard.room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.common.ScoreboardNumberPicker;
import com.gamesmith.scoreboard.common.Monster;
import com.gamesmith.scoreboard.firebase.Player;

/**
 * Created by clocksmith on 5/25/15.
 */
public class UserRecyclerViewHolder extends RecyclerView.ViewHolder {
  private Context mContext;

  private TextView mPlayerName;
  private ImageView mMonsterImage;
  private TextView mMonsterName;
  private HpNumberPicker mHp;
  private VpNumberPicker mVp;

  public UserRecyclerViewHolder(View itemView) {
    super(itemView);
    mContext = itemView.getContext();

    mPlayerName = (TextView) itemView.findViewById(R.id.list_item_player_playerName);
    mMonsterImage = (ImageView) itemView.findViewById(R.id.list_item_player_monsterImage);
    mMonsterName = (TextView) itemView.findViewById(R.id.list_item_player_monsterName);
    mHp = (HpNumberPicker) itemView.findViewById(R.id.list_item_player_hp);
    mVp = (VpNumberPicker) itemView.findViewById(R.id.list_item_player_vp);

    mHp.setEnabled(false);
    mVp.setEnabled(false);
  }

  public void updatePlayerData(Player player) {
    mPlayerName.setText(player.name);
    mMonsterImage.setImageDrawable(mContext.getDrawable(Monster.getEnum(player.monster).getSmallImageResId()));
    mMonsterName.setText(player.monster);
    mHp.setValue(player.hp);
    mVp.setValue(player.vp);
  }
}

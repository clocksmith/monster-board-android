package com.gamesmith.monsterboard.room;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.common.utils.AnimationUtils;
import com.gamesmith.monsterboard.common.model.Monster;
import com.gamesmith.monsterboard.common.model.Player;

/**
 * Created by clocksmith on 5/25/15.
 */
public class PlayerRecyclerViewHolder extends RecyclerView.ViewHolder {
  private static final String TAG = PlayerRecyclerViewHolder.class.getSimpleName();

  private Context mContext;

  private TextView mPlayerName;
  private ImageView mMonsterImage;
  private TextView mMonsterName;
  private ImageView mHpIcon;
  private ImageView mVpIcon;
  private TextView mHp;
  private TextView mVp;

  public PlayerRecyclerViewHolder(View itemView) {
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
    mPlayerName.setText(player.getName());
    mMonsterImage.setImageDrawable(ContextCompat.getDrawable(mContext,
        Monster.getEnum(player.getMonster()).getSmallImageResId()));
    mMonsterName.setText(player.getMonster());
    updatePointText(mHp, mHpIcon, player.getHp().intValue());
    updatePointText(mVp, mVpIcon, player.getVp().intValue());
  }

  private void updatePointText(TextView pointTextView, ImageView iconView, int newValue) {
    String newText = String.valueOf(newValue);
    if (!pointTextView.getText().toString().equals(newText) && iconView.getScaleX() == 1f) {
      AnimationUtils.pulse(iconView, 1.6f);
    }
    pointTextView.setText(newText);
  }
}

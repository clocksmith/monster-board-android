package com.gamesmith.scoreboard.room;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.firebase.User;

import java.util.List;

/**
 * Created by clocksmith on 5/25/15.
 */
public class RoomRecyclerViewHolder extends RecyclerView.ViewHolder {
  private User mUser;
  private List<String> mMonsters;

  private TextView mNameTextView;

  public RoomRecyclerViewHolder(View itemView) {
    super(itemView);

    mNameTextView = (TextView) itemView.findViewById(R.id.list_item_staging_user_name);
  }

  public void updatePlayerData(User user) {
    mUser = user;

    mNameTextView.setText(user.name);
  }

  public void updateMonsters(List<String> monsters) {
    mMonsters = monsters;

    // TODO(clocksmith): update spinner.
  }
}

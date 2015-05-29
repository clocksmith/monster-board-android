package com.gamesmith.scoreboard.room;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.firebase.Player;

import java.util.List;

/**
 * Created by clocksmith on 5/25/15.
 */
public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewHolder> {
  private static final String TAG = UserRecyclerViewAdapter.class.getSimpleName();

  private List<Player> mPlayers;
  private int mItemHeight;

  public UserRecyclerViewAdapter() {}

  @Override
  public UserRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_player, parent, false);
    view.getLayoutParams().height = mItemHeight;
    return new UserRecyclerViewHolder(view);
  }

  @Override
  public void onBindViewHolder(UserRecyclerViewHolder holder, int position) {
    holder.updatePlayerData(mPlayers.get(position));
  }

  @Override
  public int getItemCount() {
    return mPlayers == null ? 0 : mPlayers.size();
  }

  public void update(List<Player> players) {
    mPlayers = players;
    if (mItemHeight > 0) {
      notifyDataSetChanged();
    }
  }

  public void setItemHeight(int height) {
    mItemHeight = height;
    notifyDataSetChanged();
  }
}

package com.gamesmith.monsterboard.room;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamesmith.monsterboard.R;
import com.gamesmith.monsterboard.common.model.Player;

import java.util.List;

/**
 * Created by clocksmith on 5/25/15.
 */
public class PlayerRecyclerViewAdapter extends RecyclerView.Adapter<PlayerRecyclerViewHolder> {
  private static final String TAG = PlayerRecyclerViewAdapter.class.getSimpleName();

  private List<Player> mPlayers;
  private int mItemHeight;

  public PlayerRecyclerViewAdapter() {}

  @Override
  public PlayerRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_player, parent, false);
    view.getLayoutParams().height = mItemHeight;
    return new PlayerRecyclerViewHolder(view);
  }

  @Override
  public void onBindViewHolder(PlayerRecyclerViewHolder holder, int position) {
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

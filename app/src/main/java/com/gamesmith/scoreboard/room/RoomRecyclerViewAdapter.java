package com.gamesmith.scoreboard.room;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamesmith.scoreboard.R;
import com.gamesmith.scoreboard.firebase.User;

import java.util.List;

/**
 * Created by clocksmith on 5/25/15.
 */
public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewHolder> {
  private List<User> mUsers;

  public RoomRecyclerViewAdapter() {}

  @Override
  public RoomRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
    return new RoomRecyclerViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RoomRecyclerViewHolder holder, int position) {
    holder.updatePlayerData(mUsers.get(position));
  }

  @Override
  public int getItemCount() {
    return mUsers == null ? 0 : mUsers.size();
  }

  public void update(List<User> users) {
    mUsers = users;
    notifyDataSetChanged();
  }
}

package com.gamesmith.scoreboard.room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamesmith.scoreboard.R;

/**
 * Created by clocksmith on 5/27/15.
 */
public class RoomActionBar extends LinearLayout {
  public RoomActionBar(Context context, int roomNumber) {
    super(context);

    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = layoutInflater.inflate(R.layout.action_bar_room, this, true);
    ((TextView) view.findViewById(R.id.action_bar_room_title)).setText("ROOM: " + roomNumber);
  }
}

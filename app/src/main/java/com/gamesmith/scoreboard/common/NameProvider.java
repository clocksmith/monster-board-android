package com.gamesmith.scoreboard.common;

import android.content.Context;
import android.util.Log;

import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by clocksmith on 7/11/15.
 */
public class NameProvider {
  private static final String TAG = NameProvider.class.getSimpleName();

  private static final String JSON_FILE_NAME =  "scoreboard-player-names.json";

  private static final List<String> NAMES = Lists.newArrayList();

  private NameProvider() {}

  public static List<String> getNames(Context context) {
    if (NAMES.isEmpty()) {
      fillNamesFromJson(context);
    }
    return NAMES;
  }

  private static void fillNamesFromJson(Context context) {
    try {
      InputStream inputStream = context.getAssets().open(JSON_FILE_NAME);
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      String json = new String(buffer, "UTF-8");
      JSONObject jsonObject = new JSONObject(json);
      JSONArray namesArray = jsonObject.getJSONArray("names");
      for (int i = 0; i < namesArray.length(); i++) {
        NAMES.add(namesArray.getString(i));
      }
    } catch (IOException | JSONException e) {
      Log.e(TAG, "Could not read JSON.");
    }

  }
}

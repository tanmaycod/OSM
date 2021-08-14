package com.example.osm.Uploads;

import android.content.Context;
import android.content.SharedPreferences;

public class UserTheme {
    private String theme;
    Context context;
    SharedPreferences sharedPreferences;

    public UserTheme(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("theme_details", Context.MODE_PRIVATE);
    }

    public String getTheme() {
        theme = sharedPreferences.getString("theme", "");
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
        sharedPreferences.edit().putString("theme", theme).commit();
    }

    public void removeTheme() {
        sharedPreferences.edit().clear().commit();

    }
}

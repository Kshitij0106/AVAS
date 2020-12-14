package com.edu.avas.Common;

import android.content.Context;
import android.content.SharedPreferences;

public class  Session {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public Session(Context ctx){
        this.context = ctx;
        this.pref = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLoggedIn(boolean loggedIn){
        editor.putBoolean("loggedInMode",loggedIn);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return  pref.getBoolean("loggedInMode",false);
    }
}
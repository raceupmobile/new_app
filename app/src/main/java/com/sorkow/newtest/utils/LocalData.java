package com.sorkow.newtest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sorkow.newtest.AppLoadApp;

public class LocalData {
    public static final String NAME_OF_FILE = "name_of_file";

    public static final String PARAM1 = "param1";

    private static SharedPreferences getPreferences() {
        return AppLoadApp.getAppInstance().getSharedPreferences(NAME_OF_FILE, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditable() {
        return getPreferences().edit();
    }

    public static String getParam1() {
        return getPreferences().getString(PARAM1, "");
    }

    public static void setParam1(String param1) {
        getEditable().putString(PARAM1, param1).commit();
    }

}

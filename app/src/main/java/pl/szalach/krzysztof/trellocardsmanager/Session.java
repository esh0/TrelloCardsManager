package pl.szalach.krzysztof.trellocardsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pl.szalach.krzysztof.trellocardsmanager.helpers.PreferenceKeys;

/**
 * Created by kszalach on 2015-05-13.
 */
public class Session {
    private static Session sInstance;
    private final SharedPreferences mPreferences;

    private Session(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Session getInstance() {
        return sInstance;
    }

    public static void init(Context context) {
        sInstance = new Session(context);
    }

    public boolean isLoggedIn() {
        return mPreferences.getBoolean(PreferenceKeys.LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        mPreferences.edit().putBoolean(PreferenceKeys.LOGGED_IN, loggedIn).apply();
    }

    public String getToken() {
        return mPreferences.getString(PreferenceKeys.TOKEN, null);
    }

    public void setToken(String token) {
        mPreferences.edit().putString(PreferenceKeys.TOKEN, token).apply();
    }
}

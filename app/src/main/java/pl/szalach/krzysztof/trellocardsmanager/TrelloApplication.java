package pl.szalach.krzysztof.trellocardsmanager;

import android.app.Application;

import pl.szalach.krzysztof.trellocardsmanager.networking.VolleyManager;

/**
 * Created by kszalach on 2015-05-13.
 */
public class TrelloApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Session.init(getApplicationContext());
        VolleyManager.init(getApplicationContext());
    }
}

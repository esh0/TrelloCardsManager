package pl.szalach.krzysztof.trellocardsmanager.networking;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.NoCache;

/**
 * Created by kszalach on 2015-05-13.
 */
public class VolleyManager {

    public static final String TAG = VolleyManager.class
            .getSimpleName();
    private static VolleyManager sInstance;
    private static Context sContext;
    private RequestQueue mRequestQueue;

    protected VolleyManager() {

    }

    public static synchronized VolleyManager getInstance() {
        return sInstance;
    }

    public static void init(Context context) {
        sInstance = new VolleyManager();
        sContext = context;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = new RequestQueue(new NoCache(), new BasicNetwork(new OkHttpStack()));
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}


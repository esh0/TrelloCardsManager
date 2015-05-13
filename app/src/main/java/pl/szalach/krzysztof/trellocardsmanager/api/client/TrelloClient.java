package pl.szalach.krzysztof.trellocardsmanager.api.client;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.api.client.http.HttpMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.internal.http.HttpMethod;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import pl.szalach.krzysztof.trellocardsmanager.api.model.Board;
import pl.szalach.krzysztof.trellocardsmanager.networking.VolleyManager;

/**
 * Created by kszalach on 2015-05-13.
 */
public class TrelloClient {

    private final String mApiKey;
    private final String mToken;

    public TrelloClient(String apiKey, String token) {
        mApiKey = apiKey;
        mToken = token;
    }

    private String getUrl(String urlType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(urlType);
        stringBuilder.append("?key="); stringBuilder.append(mApiKey);
        stringBuilder.append("&token="); stringBuilder.append(mToken);

        return stringBuilder.toString();
    }

    public void getBoards(final BoardsListener listener) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
            getUrl(Constants.URL_BOARDS),
            new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                if (listener == null)
                    return;

                Type listType = new TypeToken<ArrayList<Board>>() {}.getType();
                List<Board> boards = new Gson().fromJson(response.toString(), listType);
                listener.onSuccess(boards);
            }
        },
        getErrorListener(listener));
        request.setShouldCache(false);
        queue.add(request);
    }

    private Response.ErrorListener getErrorListener(final BasicListener listener) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener == null)
                    return;

                if (error.networkResponse != null) {
                    listener.onFail(error.networkResponse.statusCode);
                } else {
                    listener.onFail(0);
                }
            }
        };
    }
}

package pl.szalach.krzysztof.trellocardsmanager.api.client;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloBoard;
import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;
import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloList;
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
        stringBuilder.append("?key=");
        stringBuilder.append(mApiKey);
        stringBuilder.append("&token=");
        stringBuilder.append(mToken);

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

                        Type listType = new TypeToken<ArrayList<TrelloBoard>>() {
                        }.getType();
                        List<TrelloBoard> boards = new Gson().fromJson(response.toString(), listType);
                        listener.onBoardsFetched(boards);
                    }
                },
                getErrorListener(listener));
        request.setShouldCache(false);
        queue.add(request);
    }

    public void getListsByBoard(String boardId, final ListsListener listener) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                getUrl(String.format(Constants.URL_LISTS_BY_BOARD, boardId)),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        if (listener == null)
                            return;

                        Type listType = new TypeToken<ArrayList<TrelloList>>() {
                        }.getType();
                        List<TrelloList> cards = new Gson().fromJson(response.toString(), listType);
                        listener.onListsFetched(cards);
                    }
                },
                getErrorListener(listener));
        request.setShouldCache(false);
        queue.add(request);
    }

    public void getCardsByList(String listId, final CardsListener listener) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                getUrl(String.format(Constants.URL_CARDS_BY_LIST, listId)),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        if (listener == null)
                            return;

                        Type listType = new TypeToken<ArrayList<TrelloCard>>() {
                        }.getType();
                        List<TrelloCard> cards = new Gson().fromJson(response.toString(), listType);
                        listener.onCardsFetched(cards);
                    }
                },
                getErrorListener(listener));
        request.setShouldCache(false);
        queue.add(request);
    }

    public void deleteCard(final TrelloCard card, final CardsDeleteListener listener) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.DELETE,
                getUrl(String.format(Constants.URL_CARD, card.getId())),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null)
                            listener.onCardDeleted(card);
                    }
                },
                getErrorListener(listener));
        request.setShouldCache(false);
        queue.add(request);
    }

    public void renameCard(final TrelloCard card, String newName, final CardsRenameListener listener) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue();
        JSONObject obj = new JSONObject();
        try {
            obj.put("value", newName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                getUrl(String.format(Constants.URL_CARD_NAME, card.getId())),
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TrelloCard card = new Gson().fromJson(response.toString(), TrelloCard.class);
                        if (listener != null)
                            listener.onCardRenamed(card);
                    }
                },
                getErrorListener(listener));
        request.setShouldCache(false);
        queue.add(request);
    }

    public void moveCard(final TrelloCard card, TrelloList newList, final CardsMoveListener listener) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue();
        JSONObject obj = new JSONObject();
        try {
            obj.put("value", newList.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                getUrl(String.format(Constants.URL_CARD_LIST, card.getId())),
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TrelloCard card = new Gson().fromJson(response.toString(), TrelloCard.class);
                        if (listener != null)
                            listener.onCardMoved(card);
                    }
                },
                getErrorListener(listener));
        request.setShouldCache(false);
        queue.add(request);
    }

    public void addCard(String name, String listId, final CardsAddListener listener) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue();
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("due", null);
            obj.put("idList", listId);
            obj.put("urlSource", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                getUrl(Constants.URL_CARDS),
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TrelloCard card = new Gson().fromJson(response.toString(), TrelloCard.class);
                        if (listener != null)
                            listener.onCardAdded(card);
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

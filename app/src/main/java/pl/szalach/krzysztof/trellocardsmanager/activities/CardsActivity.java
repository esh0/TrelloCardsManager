package pl.szalach.krzysztof.trellocardsmanager.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.R;
import pl.szalach.krzysztof.trellocardsmanager.Session;
import pl.szalach.krzysztof.trellocardsmanager.api.client.BoardsListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.Constants;
import pl.szalach.krzysztof.trellocardsmanager.api.client.TrelloClient;
import pl.szalach.krzysztof.trellocardsmanager.api.model.Board;
import pl.szalach.krzysztof.trellocardsmanager.helpers.IntentKeys;
import pl.szalach.krzysztof.trellocardsmanager.helpers.IntentRequestCodes;

public class CardsActivity extends ActionBarActivity implements BoardsListener {

    private TrelloClient mTrelloClient;
    private List<Board> mBoards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
    }

    private void initTrelloClient() {
        mTrelloClient = new TrelloClient(Constants.API_KEY, Session.getInstance().getToken());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Session.getInstance().isLoggedIn()) {
            initTrelloClient();
            refresh();
        } else {
            login();
        }
    }

    private void refresh() {
        mTrelloClient.getBoards(this);
    }

    private void login() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivityForResult(login, IntentRequestCodes.LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentRequestCodes.LOGIN && resultCode == RESULT_OK) {
            Session.getInstance().setLoggedIn(true);
            Session.getInstance().setToken(data.getStringExtra(IntentKeys.TOKEN));
        }
    }

    @Override
    public void onSuccess(List<Board> boards) {
        mBoards = boards;
        if (boards != null) {
            for (Board board : boards) {
                Log.i("board fetched", board.getName());
            }
        }
    }

    @Override
    public void onFail(int statusCode) {
        if (statusCode == 401) {
            Session.getInstance().setLoggedIn(false);
            Session.getInstance().setToken(null);
            login();
        } else {
            Toast.makeText(CardsActivity.this, R.string.operation_failed, Toast.LENGTH_LONG).show();
        }
    }
}

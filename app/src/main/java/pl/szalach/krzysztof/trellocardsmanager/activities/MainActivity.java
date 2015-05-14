package pl.szalach.krzysztof.trellocardsmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.R;
import pl.szalach.krzysztof.trellocardsmanager.Session;
import pl.szalach.krzysztof.trellocardsmanager.adapters.BoardSpinnerAdapter;
import pl.szalach.krzysztof.trellocardsmanager.adapters.ListAdapter;
import pl.szalach.krzysztof.trellocardsmanager.api.client.BoardsListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.Constants;
import pl.szalach.krzysztof.trellocardsmanager.api.client.ListsListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.TrelloClient;
import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloBoard;
import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloList;
import pl.szalach.krzysztof.trellocardsmanager.helpers.IntentKeys;
import pl.szalach.krzysztof.trellocardsmanager.helpers.IntentRequestCodes;

public class MainActivity extends ActionBarActivity implements TrelloActivity, ToolbarActivity, BoardsListener, AdapterView.OnItemSelectedListener, ListsListener {

    private TrelloClient mTrelloClient;
    private List<TrelloBoard> mBoards = new ArrayList<>();
    private BoardSpinnerAdapter mBoardsAdapter;
    private ListAdapter mListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar();
        initBoardsAdapter();
        initListsAdapter();
    }

    private void initListsAdapter() {
        mListsAdapter = new ListAdapter(getSupportFragmentManager());
        ((ViewPager) findViewById(R.id.container)).setAdapter(mListsAdapter);
    }

    private void initBoardsAdapter() {
        mBoardsAdapter = new BoardSpinnerAdapter(this, android.R.layout.simple_spinner_item, mBoards);
        mBoardsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spinner)).setAdapter(mBoardsAdapter);
        ((Spinner) findViewById(R.id.spinner)).setOnItemSelectedListener(this);
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
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
    public void onBoardsFetched(List<TrelloBoard> boards) {
        mBoards.clear();
        mBoards.addAll(boards);
        mBoardsAdapter.notifyDataSetChanged();
        if (mBoards.size() > 0) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onListsFetched(List<TrelloList> lists) {
        ArrayList<TrelloList> arrayList = new ArrayList<>(lists);
        mListsAdapter.setItems(arrayList);
        mListsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFail(int statusCode) {
        if (statusCode == 401) {
            Session.getInstance().setLoggedIn(false);
            Session.getInstance().setToken(null);
            login();
        } else {
            Toast.makeText(MainActivity.this, R.string.operation_failed, Toast.LENGTH_LONG).show();
        }
    }

    public Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.toolbar));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mListsAdapter.setItems(null);
        mListsAdapter.notifyDataSetChanged();

        TrelloBoard selectedBoard = mBoardsAdapter.getItem(i);
        mTrelloClient.getListsByBoard(selectedBoard.getId(), this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

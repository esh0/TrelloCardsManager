package pl.szalach.krzysztof.trellocardsmanager.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.R;
import pl.szalach.krzysztof.trellocardsmanager.Session;
import pl.szalach.krzysztof.trellocardsmanager.activities.TrelloActivity;
import pl.szalach.krzysztof.trellocardsmanager.adapters.CardAdapter;
import pl.szalach.krzysztof.trellocardsmanager.adapters.CardAdapterListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.CardsAddListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.CardsDeleteListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.CardsListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.CardsMoveListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.CardsRenameListener;
import pl.szalach.krzysztof.trellocardsmanager.api.client.Constants;
import pl.szalach.krzysztof.trellocardsmanager.api.client.TrelloClient;
import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;
import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloList;

public class TrelloListFragment extends Fragment implements CardsListener, CardAdapterListener, CardsDeleteListener, CardsRenameListener, CardsMoveListener, CardsAddListener {

    private static final String ARG_LIST_POSITION = "position";
    private static final String ARG_LISTS = "lists";
    private static final String UPDATE_INTENT = "update";
    private static final String LIST_ID = "list_id";

    private String mListId;
    private String mListName;

    private List<TrelloCard> mCards;
    private CardAdapter mAdapter;
    private int mPosition;
    private ArrayList<TrelloList> mLists;
    private TrelloClient mClient;
    private BroadcastReceiver mUpdateReceiver;

    public TrelloListFragment() {
    }

    public static TrelloListFragment newInstance(ArrayList<TrelloList> lists, int position) {
        TrelloListFragment fragment = new TrelloListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LIST_POSITION, position);
        args.putSerializable(ARG_LISTS, lists);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_LIST_POSITION);
            mLists = (ArrayList<TrelloList>) getArguments().getSerializable(ARG_LISTS);
            mListId = mLists.get(mPosition).getId();
            mListName = mLists.get(mPosition).getName();
        }

        mCards = new ArrayList<>();
        mAdapter = new CardAdapter(getActivity(), mCards, this);
        mClient = new TrelloClient(Constants.API_KEY, Session.getInstance().getToken());
        mClient.getCardsByList(mListId, this);

        mUpdateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(UPDATE_INTENT) && intent.getStringExtra(LIST_ID).equals(mListId)) {
                    mClient.getCardsByList(mListId, TrelloListFragment.this);
                }
            }
        };

        IntentFilter update = new IntentFilter(UPDATE_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateReceiver, update);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton addCard = (FloatingActionButton) root.findViewById(R.id.addCard);
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCard();
            }
        });

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
        manager.unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onCardsFetched(List<TrelloCard> cards) {
        synchronized (mCards) {
            mCards.clear();
            mCards.addAll(cards);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEditCard(final TrelloCard card) {
        final EditText et = new EditText(getActivity());
        et.setSelectAllOnFocus(true);
        et.setText(card.getName());

        new AlertDialog.Builder(getActivity()).setTitle(R.string.edit)
                .setView(et)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editCard(card, et.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void editCard(TrelloCard card, String newName) {
        mClient.renameCard(card, newName, this);
    }

    @Override
    public void onCardRenamed(TrelloCard card) {
        mCards.set(mCards.indexOf(card), card);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMoveCard(final TrelloCard card) {
        String[] items = new String[mLists.size()];
        for (int i = 0; i < items.length; i++)
            items[i] = mLists.get(i).getName();

        new AlertDialog.Builder(getActivity()).setTitle(R.string.move)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mPosition == i)
                            return;

                        moveCard(card, mLists.get(i));
                    }
                })
                .show();
    }

    private void moveCard(TrelloCard card, TrelloList newList) {
        mClient.moveCard(card, newList, this);
    }

    @Override
    public void onCardMoved(TrelloCard card) {
        if (!card.getIdList().equals(mListId)) {
            onCardDeleted(card);
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
            Intent update = new Intent(TrelloListFragment.UPDATE_INTENT);
            update.putExtra(TrelloListFragment.LIST_ID, card.getIdList());
            manager.sendBroadcast(update);
        }
    }

    @Override
    public void onDeleteCard(final TrelloCard card) {
        new AlertDialog.Builder(getActivity()).setMessage(R.string.delete)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCard(card);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void deleteCard(TrelloCard card) {
        mClient.deleteCard(card, this);
    }

    @Override
    public void onCardDeleted(TrelloCard card) {
        mCards.remove(card);
        mAdapter.notifyDataSetChanged();
    }

    private void addCard() {
        final EditText et = new EditText(getActivity());
        et.setSelectAllOnFocus(true);
        et.setText("");

        new AlertDialog.Builder(getActivity()).setTitle(R.string.add)
                .setView(et)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mClient.addCard(et.getText().toString(), mListId, TrelloListFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onCardAdded(TrelloCard card) {
        mCards.add(card);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFail(int statusCode) {
        ((TrelloActivity) getActivity()).onFail(statusCode);
    }
}

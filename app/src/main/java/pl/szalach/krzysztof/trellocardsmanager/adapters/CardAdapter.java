package pl.szalach.krzysztof.trellocardsmanager.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.R;
import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private final List<TrelloCard> mItems;
    private final Context mContext;
    private final CardAdapterListener mListener;
    private TrelloCard mItemSelected;

    public CardAdapter(Context context, List<TrelloCard> items, CardAdapterListener listener) {
        mContext = context;
        mItems = items;
        mListener = listener;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(mItems.get(position).getName());
        holder.mMenu.setTag(mItems.get(position));
        holder.mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemSelected = (TrelloCard) v.getTag();
                PopupMenu popup = new PopupMenu(mContext, v);
                popup.setOnMenuItemClickListener(CardAdapter.this);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_cards, popup.getMenu());
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (mListener == null)
            return false;

        switch (menuItem.getItemId()) {
            case R.id.action_edit:
                mListener.onEditCard(mItemSelected);
                break;
            case R.id.action_move:
                mListener.onMoveCard(mItemSelected);
                break;
            case R.id.action_delete:
                mListener.onDeleteCard(mItemSelected);
                break;
        }

        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mMenu;
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.content);
            mMenu = v.findViewById(R.id.menu);
        }
    }
}

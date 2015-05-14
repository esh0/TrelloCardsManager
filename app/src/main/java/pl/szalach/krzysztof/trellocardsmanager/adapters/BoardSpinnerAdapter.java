package pl.szalach.krzysztof.trellocardsmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloBoard;

/**
 * Created by kszalach on 2015-05-14.
 */
public class BoardSpinnerAdapter extends ArrayAdapter<TrelloBoard> {
    private final Context mContext;
    private final List<TrelloBoard> mItems;
    private final int mResource;
    private int mDropResource;

    public BoardSpinnerAdapter(Context context, int resource, List<TrelloBoard> objects) {
        super(context, resource, objects);
        mContext = context;
        mItems = objects;
        mResource = resource;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
        mDropResource = resource;
    }

    @Override
    public TrelloBoard getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            viewHolder = new ViewHolder(convertView);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.getText().setText(mItems.get(position).getName());
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mDropResource, parent, false);
            viewHolder = new ViewHolder(convertView);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.getText().setText(mItems.get(position).getName());
        convertView.setTag(viewHolder);
        return convertView;
    }

    class ViewHolder {
        TextView text;

        public ViewHolder(View layout) {
            text = (TextView) layout.findViewById(android.R.id.text1);
        }

        public TextView getText() {
            return text;
        }
    }
}

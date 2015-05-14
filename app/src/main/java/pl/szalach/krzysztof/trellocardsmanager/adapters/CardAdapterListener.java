package pl.szalach.krzysztof.trellocardsmanager.adapters;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;

/**
 * Created by kszalach on 2015-05-14.
 */
public interface CardAdapterListener {
    void onEditCard(TrelloCard card);

    void onMoveCard(TrelloCard card);

    void onDeleteCard(TrelloCard card);
}

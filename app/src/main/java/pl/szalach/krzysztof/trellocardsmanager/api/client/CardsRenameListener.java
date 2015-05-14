package pl.szalach.krzysztof.trellocardsmanager.api.client;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;

/**
 * Created by kszalach on 2015-05-14.
 */
public interface CardsRenameListener extends BasicListener {
    void onCardRenamed(TrelloCard card);
}


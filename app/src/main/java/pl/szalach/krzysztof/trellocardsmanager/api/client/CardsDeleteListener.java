package pl.szalach.krzysztof.trellocardsmanager.api.client;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;

/**
 * Created by kszalach on 2015-05-14.
 */
public interface CardsDeleteListener extends BasicListener {
    void onCardDeleted(TrelloCard card);
}

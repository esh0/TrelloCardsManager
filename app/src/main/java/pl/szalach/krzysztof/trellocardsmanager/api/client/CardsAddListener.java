package pl.szalach.krzysztof.trellocardsmanager.api.client;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;

public interface CardsAddListener extends BasicListener {
    void onCardAdded(TrelloCard card);
}

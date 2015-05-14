package pl.szalach.krzysztof.trellocardsmanager.api.client;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;

public interface CardsMoveListener extends BasicListener {
    void onCardMoved(TrelloCard card);
}


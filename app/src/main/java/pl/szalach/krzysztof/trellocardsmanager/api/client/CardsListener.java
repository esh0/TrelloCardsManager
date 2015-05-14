package pl.szalach.krzysztof.trellocardsmanager.api.client;

import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloCard;

public interface CardsListener extends BasicListener {
    void onCardsFetched(List<TrelloCard> cards);
}

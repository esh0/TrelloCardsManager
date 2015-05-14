package pl.szalach.krzysztof.trellocardsmanager.api.client;


import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloList;

public interface ListsListener extends BasicListener {
    void onListsFetched(List<TrelloList> lists);
}


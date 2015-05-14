package pl.szalach.krzysztof.trellocardsmanager.api.client;

import java.util.List;

import pl.szalach.krzysztof.trellocardsmanager.api.model.TrelloBoard;

/**
 * Created by kszalach on 2015-05-13.
 */
public interface BoardsListener extends BasicListener {
    void onBoardsFetched(List<TrelloBoard> boards);
}


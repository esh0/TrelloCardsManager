package pl.szalach.krzysztof.trellocardsmanager.api.model;

import java.io.Serializable;

/**
 * Created by kszalach on 2015-05-14.
 */
public class TrelloList implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

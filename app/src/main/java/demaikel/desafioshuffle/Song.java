package demaikel.desafioshuffle;

/**
 * Created by Kevin on 29-11-2016.
 */

public class Song {
    private long id;
    private String name;

    public Song(String id, String name) {
        this.id =Long.parseLong(id);
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

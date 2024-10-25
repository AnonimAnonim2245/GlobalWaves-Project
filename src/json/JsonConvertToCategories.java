package json;

import status.playerStatus;
import lombok.Getter;
import lombok.Setter;
import types.Episode;

import java.util.ArrayList;
/**
 * here is the class from where we extracted for each element
 * from the test json according to any criterion that can
 * exists in the json and we convert it into a class so that we can use them later
 */
@Getter
@Setter
public class JsonConvertToCategories {

    private Integer age;

    private String city;
    private String NextPage;
    private String command;
    private String username;
    private String name;
    private String releaseYear;
    private String artist;
    private String album;
    private String description;
    private Integer timestamp;
    private String type;
    private String playlistName;
    private Object filters;
    private String date;
    private Integer price;
    private String recommendationType;
    private Object songs;
    private ArrayList<Episode> episodes = new ArrayList<>();
    private int itemNumber;
    private int playlistId;
    private int seed;
    private playerStatus status;

}

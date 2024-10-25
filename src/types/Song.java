package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;
import lombok.Getter;
import lombok.Setter;
import status.playerLoad;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Am implementat o functie song unde am store-uit toate informatiile despre melodii,
 * precum nume, duratie, album, Userii care au dat like(de aici luam numarul
 * de like-uri
 * si putem da add si remove am facut
 * functiile. Mai avem gen, artist, release, versuri si tipul statului de repetare
 * (0- nu se repeta, 1 - se repeta o data, 2 - se repeta de mai multe ori)
 */
public final class Song {
    @Getter
    @Setter
    private int count4 = 0;
    @Getter
    private String name;
    @Getter
    private Integer duration;
    @Getter
    private String album;
    @Getter
    private List<User> userListened = new ArrayList<>();
    @Getter
    @Setter
    private Integer numberOfListens = 0;
    @Getter
    private final List<String> usersLiked = new ArrayList<>();
    @Getter
    @Setter
    private Double songRevenue = 0.0;
    @Getter
    private ArrayList<String> tags;
    @Getter
    private String lyrics;
    @Getter
    private String genre;
    private Integer releaseYear;
    @Getter
    private String artist;
    @Getter
    @Setter
    private int premiumCount = 0;
    @Getter
    @Setter
    private int adCount = 0;

    /**
     * set name
     *
     * @param name2
     */
    public void setName(final String name2) {
        this.name = name2;
    }

    /**
     * set name
     */
    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    /**
     * set name
     */
    public void setAlbum(final String album) {

        this.album = album;
    }

    /**
     * set name
     */
    public void setTags(final ArrayList<String> tags) {

        this.tags = tags;
    }

    /**
     * set name
     */
    public void setLyrics(final String lyrics) {

        this.lyrics = lyrics;
    }

    /**
     * set name
     */
    public void setGenre(final String genre) {

        this.genre = genre;
    }

    /**
     * set name
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * set name
     */
    public boolean searchUsersWhoLiked(final String name2) {
        if (usersLiked.isEmpty()) {
            return false;
        }
        for (String user : usersLiked) {
            if (user.equals(name2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * set name
     */
    public void addUsersWhoLiked(final String name2) {
        if (usersLiked.contains(name2)) {
            return;
        }
        usersLiked.add(name2);
    }

    /**
     * set name
     */
    public void removeUsersWhoLiked(final String name2) {
        usersLiked.remove(name2);
    }
    /**
     * set name
     */


    /**
     * arata top5 melodii
     *
     * @return ne returneaza top 5 melodii
     */
    public JsonNode top5Songs() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = new ObjectMapper().createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        ArrayNode outputs = objectMapper.createArrayNode();
        List<String> top5Songs = user.getUserDatabase().getTop5Songs();
        for (String top5 : top5Songs) {
            outputs.add(top5);
        }
        jsonObject.set("result", outputs);

        return jsonObject;

    }

    /**
     * face jsonul de care avem nevoie pentru like si ne arata statusul,(like,unlike,error)
     *
     * @return ne returneaza jsonul pentru statusul likeului
     */
    public JsonNode likejson() {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        int index = Library.searchSongByNameAndAlbum(user.getUserDatabase().getStatus().getName(),
                user.getUserDatabase().getStatus().getAlbum());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (user.getOnlineStatus().equals("online")) {
            if (!user.getUserDatabase().getLikestatus() || index == -1) {
                jsonObject.put("message", "Please load a source before liking or unliking.");
                return jsonObject;
            }
            if (user.getUserDatabase().getStatus().getType() != null) {
                if (user.getUserDatabase().getStatus().getType().equals("podcast")) {
                    jsonObject.put("message", "The loaded source is not a song.");
                    return jsonObject;
                }
            }
            Song song;
            assert user.getUserDatabase().getStatus().getType() != null;
            if (user.getUserDatabase().getStatus().
                    getType().equals("playlist")) {
                song = Library.getDatabase().getSongs().get(index);
            } else {
                song = Library.getDatabase().getSongs().get(index);
            }
            boolean likeStatus = song.searchUsersWhoLiked(Library.
                    getDatabase().getJson().getUsername());
            if (likeStatus) {
                song.removeUsersWhoLiked(Library.getDatabase().
                        getJson().getUsername());
                jsonObject.put("message",
                        "Unlike registered successfully.");
                user.getPrefferedSongs().removeIf(song2 -> song2.equals(song));

            } else {
                song.addUsersWhoLiked(Library.getDatabase().getJson().
                        getUsername());
                jsonObject.put("message",
                        "Like registered successfully.");

                user.addPrefferedSong(song);

            }
        } else {
            jsonObject.put("message", user.getUsername() + " is offline.");

        }
        return jsonObject;

    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    /**
     * we use the accept method in order to implement the visitor
     * design pattern for status json within status class
     *
     * @param visitor    the visitor which can be either a
     *                   playlist, album, song or podcast
     * @param playerLoad the loaded element
     * @param user       the current user
     * @param newI       the current timestamp
     * @return the element returned by the function
     */
    public playerLoad acceptNegativeRemaininingTimeForStatusJson(final Visitor visitor,
         final playerLoad playerLoad, final User user, final Integer newI) {
        return visitor.visitNegativeRemaininingTimeForStatusJson(this, playerLoad, user, newI);
    }

}

package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import status.playerLoad;
import visitor.NegativeTimeStampVisitor;
import visitor.Visitor;
import json.JsonConvertToCategories;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implemented the album function where we store all the information
 * about the album, such as name, artist, release year, description,
 * songs. And other functions related to the album, such as add album,
 * remove album, show album, top 5 albums, sum of likes of songs of an
 * album,get the index, get the randomized index, search the random index
 * after the shuffle status is true, make a shuffled list, add a list
 * of songs to song list.
 */
public class Album extends Playlist {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private List<Integer> songRandomList = new ArrayList<>();

    @Getter
    @Setter
    private List<Integer> songIndexList = new ArrayList<>();
    @Getter
    @Setter
    private String artist;
    @Getter
    @Setter
    private String releaseYear;
    private ArrayList<Song> songs = new ArrayList<>();
    @Getter
    @Setter
    private int seed = 0;
    @Getter
    @Setter
    private Boolean statusshuffle = false;
    @Getter
    @Setter
    private String description = "";


    public Album() {

    }

    public Album(final String name2, final String artist2,
                 final String releaseYear2, final String description2) {

        this.name = name2;
        this.artist = artist2;
        this.releaseYear = releaseYear2;
        this.description = description2;
    }

    /**
     * sets the normal song list
     */
    public void setSongList() {
        this.songIndexList.clear();
        for (int i = 0; i < this.getSongs().size(); i++) {
            this.songIndexList.add(i);
        }
    }

    /**
     * add album to the list
     * check if the album exists
     * check if the artist exists
     * check if the album has the same name
     * check if the album has the same song
     *
     * @return the jsonNode
     * @throws IOException for objectMapper
     */
    public JsonNode addAlbumJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {

            if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " is not an artist.");

            } else {
                jsonObject.put("message", "The username "
                        + jsonElement.getUsername() + " doesn't exist.");
            }
        } else {


            Artist artistInstance = Library.getDatabase().
                    getArtistInstance(jsonElement.getUsername());
            for (Album album : artistInstance.getAlbums()) {
                if (album.getName().equals(jsonElement.getName())) {
                    jsonObject.put("message",
                            jsonElement.getUsername() + " has another album with the same name.");
                    return jsonObject;
                }
            }

            Album album = new Album(jsonElement.getName(), jsonElement.getUsername(),
                    jsonElement.getReleaseYear(), jsonElement.getDescription());
            setName(album.getName());
            this.artist = album.artist;
            this.releaseYear = album.releaseYear;
            this.description = album.description;

            String songsCopy = objectMapper.writeValueAsString(jsonElement.getSongs());
            ArrayList<Song> songClass = objectMapper.readValue(songsCopy, new TypeReference<>() {
            });
            ArrayList<Song> songArrayList = new ArrayList<>();
            if (songClass != null) {
                for (Song song : songClass) {
                    song.setAlbum(album.getName());
                    songArrayList.add(song);
                }
            }

            for (int i = 0; i <= songArrayList.size() - 2; i++) {
                for (int j = i + 1; j <= songArrayList.size() - 1; j++) {
                    if (songArrayList.get(i).getName().equals(songArrayList.get(j).getName())) {
                        jsonObject.put("message", jsonElement.getUsername()
                                + " has the same song at least twice in this album.");
                        return jsonObject;
                    }
                }
            }

            Library.getDatabase().getSongs().addAll(songArrayList);
            List<String> songNames = Library.getDatabase().getSongs().stream()
                    .filter(song -> "Voulez-Vous".equals(song.getAlbum()))
                    .map(Song::getName)
                    .toList();

            this.addSongs(songArrayList);

            Library.getDatabase().addAlbum(this);
            artistInstance.addAlbum(this);
            for (User user : Library.getDatabase().getUsers()) {
                int i = 0;
                for (Artist artist7 : user.getArtistsNotification()) {
                    if (artist7.getUsername().equals(artistInstance.getUsername())) {
                        user.getNotification().add("Album  " + album.getName()
                                + "  " + artist7.getUsername());
                        break;
                    }

                    i++;
                }
            }
            jsonObject.put("message", jsonElement.getUsername()
                    + " has added new album successfully.");
        }

        return jsonObject;
    }

    /**
     * remove album
     * check if the artist is valid
     * check if the album exists already
     * checks if the song of an album is in a playlist
     * and if an album is loaded by a user
     *
     * @return
     */
    public JsonNode removeAlbumJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {

            if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " is not an artist.");

            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
            }
        } else {

            if (!Library.getDatabase().checkIfAlbumUserExists(jsonElement.getName(),
                    jsonElement.getUsername())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " doesn't have an album with the given name.");
            } else {
                for (Playlist playlist : Library.getDatabase().getPlaylistList()) {
                    for (Song song : Library.getDatabase().
                            searchAlbumByName(jsonElement.getName()).getSongs()) {
                        if (playlist.getSongList().contains(song.getName())) {
                            jsonObject.put("message",
                                    jsonElement.getUsername() + " can't delete this album.");
                            return jsonObject;
                        }
                    }
                }
                for (User user : Library.getDatabase().getUsers()) {
                    if (user.getUserDatabase().getLoadedElement().equals(jsonElement.getName())) {
                        jsonObject.put("message",
                                jsonElement.getUsername() + " can't delete this album.");
                        return jsonObject;
                    }
                }
                for (User user : Library.getDatabase().getUsers()) {

                    if (user.getUserDatabase().getLoadStatus()) {
                        playerLoad playerLoad = user.getUserDatabase().getStatus();
                        if (playerLoad.getAlbum().equals(jsonElement.getName())) {
                            jsonObject.put("message",
                                    jsonElement.getUsername() + " can't delete this album.");
                            return jsonObject;
                        }
                    }
                }

                Artist artistInstance = Library.getDatabase().
                        getArtistInstance(jsonElement.getUsername());

                for (Song song : Library.getDatabase().
                        searchAlbumByName(jsonElement.getName()).getSongs()) {
                    Library.getDatabase().getSongs().removeIf(song2 ->
                            song2.getName().equals(song.getName()) && song2.getAlbum().
                                    equals(jsonElement.getName()));
                    for (Playlist playlist : Library.getDatabase().getPlaylistList()) {
                        playlist.getSongList().removeIf(song3 ->
                                song3.equals(song.getName()) && song.getAlbum().
                                        equals(jsonElement.getName()));
                    }

                }
                for (User user : Library.getDatabase().getUsers()) {

                    for (Song song : Library.getDatabase().
                            searchAlbumByName(jsonElement.getName()).getSongs()) {
                        user.getPrefferedSongs().removeIf(song2 ->
                                song2.equals(song.getName()) && song.getAlbum().
                                        equals(jsonElement.getName()));
                    }

                }
                Library.getDatabase().getAlbums().removeIf(album ->
                        album.getName().equals(jsonElement.getName()));
                artistInstance.getAlbums().removeIf(album -> album.getName().
                        equals(jsonElement.getName()));


                for (User user : Library.getDatabase().getUsers()) {
                    int i = 0;
                    for (Artist artist5 : user.getArtistsNotification()) {
                        if (artist5.getUsername().equals(artistInstance.getUsername())) {
                            break;
                        }
                        i++;
                    }
                }

                jsonObject.put("message",
                        jsonElement.getUsername() + " deleted the album successfully.");
            }
        }

        return jsonObject;
    }

    /**
     * show all the albums of a user
     *
     * @return the jsonnode
     */
    public JsonNode showAlbumJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        ArrayList<ObjectNode> nodes = new ArrayList<>();

        for (Album album : Library.getDatabase().
                getArtistInstance(jsonElement.getUsername()).getAlbums()) {
            ObjectNode smallJsonObject = objectMapper.createObjectNode();

            smallJsonObject.put("name", album.getName());
            ArrayNode outputs = objectMapper.createArrayNode();
            for (Song song : album.getSongs()) {
                outputs.add(song.getName());
            }
            smallJsonObject.set("songs", outputs);
            nodes.add(smallJsonObject);


        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (ObjectNode node : nodes) {

            arrayNode.add(node);
        }
        jsonObject.put("result", arrayNode);


        return jsonObject;

    }

    /**
     * the sum of the likes of the songs of an album
     *
     * @return that sum
     */
    public Integer sumofLikesongAlbums() {
        Integer sum = 0;
        for (Song song : this.getSongs()) {
            sum += song.getUsersLiked().size();
        }
        return sum;
    }

    /**
     * returns the top5 albums by number of likes
     *
     * @return the json node
     */
    public JsonNode top5Album() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = new ObjectMapper().createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().
                getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        ArrayNode outputs = objectMapper.createArrayNode();
        List<String> top5Albums = user.getUserDatabase().getTop5Albums();
        for (String top5 : top5Albums) {
            outputs.add(top5);
        }
        jsonObject.set("result", outputs);

        return jsonObject;

    }

    /**
     * get the index
     *
     * @return the normal index
     */
    public int getnewSongIndex(final User user) {
        return songIndexList.get(user.getSongIndex());
    }

    /**
     * get the randomized index
     *
     * @return the randomized index
     */
    public int getnewRandomSongIndex(final User user) {
        return songRandomList.get(user.getSongIndex());
    }

    /**
     * searches the random index after the shuffle status
     * is true
     *
     * @param value the original value
     * @return the position where
     * it is in the randomized
     * list
     */
    public int searchRandomIndex(final int value) {
        int j = 0;
        while (songRandomList.get(j) != value) {
            j++;
        }
        return j;

    }

    /**
     * makes a shuffled list
     */
    public void makeShuffleList() {
        Random random = new Random(seed);
        if (this.songRandomList.isEmpty()) {
            this.songRandomList = this.songIndexList;
        } else {
            for (int i = 0; i < this.songIndexList.size(); i++) {
                this.songRandomList.set(i, i);
            }
        }

        Collections.shuffle(this.songRandomList, random);
    }


    /**
     * add a list of songs to song list
     *
     * @param songs2 song list
     */
    public void addSongs(final ArrayList<Song> songs2) {
        this.getSongs().addAll(songs2);
    }


    /**
     * get the songs
     *
     * @return the songs
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }

    /**
     * accept method for the visitor design pattern for
     * repeatAll
     *
     * @param visitor
     * @param playerLoad
     * @param user
     * @return
     */
    public int acceptEndForRepeatAll(final NegativeTimeStampVisitor visitor,
                                     final playerLoad playerLoad, final User user) {
        return visitor.visitEndForRepeatAll(this, playerLoad, user);
    }

    /**
     * accept method for the visitor design pattern for
     * negative remaining time for status class
     *
     * @param visitor    visitor
     * @param playerLoad the current load
     * @param user       the current user
     * @param newI       the negative timestamp
     * @return the visitor
     */
    @Override
    public playerLoad acceptNegativeRemaininingTimeForStatusJson(final Visitor visitor,
      final playerLoad playerLoad, final User user, final Integer newI) {
        return visitor.visitNegativeRemaininingTimeForStatusJson(this, playerLoad, user, newI);
    }

}

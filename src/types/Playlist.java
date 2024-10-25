package types;

import status.playerLoad;
import visitor.NegativeTimeStampVisitor;
import visitor.Visitor;
import json.JsonConvertToCategories;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * shows the playlist where we can add songs,
 * we have SongIndexList which represents the normal song index,
 * SongRandomlist(index shuffled
 * of the song), visibility, status shuffled,
 * song_index(current index, either used
 * for the random or normal index)
 * we can see the users who follow the song
 */
@Getter
public class Playlist extends JsonConvertToCategories {
    @Getter
    private List<String> songList = new ArrayList<>();
    @Getter
    private List<String> albumList = new ArrayList<>();
    @Getter
    private List<String> genreList = new ArrayList<>();
    @Setter
    private int followers;
    @Setter
    private String name;
    @Setter
    @Getter
    private String album;
    @Setter
    @Getter
    private String visibility;
    @Getter
    @Setter
    private String owner;
    @Getter
    @Setter
    private List<Integer> songIndexList = new ArrayList<>();
    @Getter
    @Setter
    private int seed = 0;

    @Getter
    @Setter
    private List<Integer> songRandomList = new ArrayList<>();


    @Getter
    @Setter
    private List<String> usersFollow = new ArrayList<>();



    /**
     * added users who follow a playlist
     *
     * @param name2 name of the person
     */
    public void addUsersWhoFollowed(final String name2) {
        if (usersFollow.contains(name2)) {
            return;
        }
        usersFollow.add(name2);
    }

    /**
     * search users who follow a playlist
     *
     * @param name2 name of the person
     */
    public boolean searchUsersWhoFollowed(final String name2) {
        if (usersFollow.isEmpty()) {
            return false;
        }
        for (String user : usersFollow) {
            if (user.equals(name2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * removed users who follow a playlist
     *
     * @param name2 name of the person
     */
    public void removeUsersWhoFollowed(final String name2) {
        usersFollow.remove(name2);
    }

    /**
     * removes all songs from a playlist
     */
    public void removeAllSongList() {
        songList.clear();
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
     * sets the normal song list
     */
    public void setSongList() {
        this.songIndexList.clear();
        for (int i = 0; i < this.getSongList().size(); i++) {
            this.songIndexList.add(i);
        }
    }

    /**
     * creates a playlist
     *
     * @return json
     */
    public JsonNode createPlaylist() {
        JsonConvertToCategories jsonElement = Library.
                getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();

        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (user.getUserDatabase().selectPlaylistByName(Library.
                getDatabase().getJson().getPlaylistName()) != null) {
            jsonObject.put("message",
                    "A playlist with the same name already exists.");
            return jsonObject;
        } else {
            this.owner = Library.getDatabase().getJson().getUsername();
            this.visibility = "public";
            this.name = Library.getDatabase().getJson().getPlaylistName();
            this.followers = 0;
            songList = new ArrayList<>();
            genreList = new ArrayList<>();
            Library.getDatabase().addPlaylistList(this);
            jsonObject.put("message",
                    "Playlist created successfully.");
        }
        return jsonObject;
    }

    /**
     * add or removes a playlist
     *
     * @return json
     */
    public JsonNode addremovePlaylist() {
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().
                getUserInstance(jsonElement.getUsername());
        int input = user.getUserDatabase().
                selectListById(Library.getDatabase().
                        getJson().getUsername(), Library.
                        getDatabase().getJson().getPlaylistId());

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if ((!user.getUserDatabase().getLoadStatus()
                || user.getUserDatabase().getLoadedPlaylist() == null)
                && !user.getUserDatabase().getLikestatus()) {
            jsonObject.put("message",
                    "Please load a source before "
                            + "adding to or removing from "
                            + "the playlist.");
            return jsonObject;
        }
        if (user.getUserDatabase().getStatus().getType().equals("podcast")) {
            jsonObject.put("message",
                    "The loaded source is not a song.");
            return jsonObject;
        }

        if (input >= 0) {

            Playlist playlist = Library.getDatabase().
                    getPlaylistList().get(input);
            String searchSong = user.getUserDatabase().
                    getStatus().getName();
            String searchAlbum = user.getUserDatabase().
                    getStatus().getAlbum();
            List<String> songs2 = playlist.getSongList();
            List<String> albums2 = playlist.getAlbumList();
            boolean removeElement = user.getUserDatabase().searchSongByName(songs2,
                    albums2, searchSong, searchAlbum);
            if (!removeElement) {
                jsonObject.put("message",
                        "Successfully added to playlist.");

                playlist.getSongList().add(user.
                        getUserDatabase().getStatus().getName());
                playlist.getAlbumList().add(user.getUserDatabase().
                        getStatus().getAlbum());
                playlist.getGenreList().add(user.getUserDatabase().
                        getStatus().getGenre());
                this.getSongList().add(user.getUserDatabase().
                        getStatus().getName());
                this.getSongList().add(user.getUserDatabase().
                        getStatus().getName());
                this.getAlbumList().add(user.getUserDatabase().
                        getStatus().getAlbum());
                this.getGenreList().add(user.getUserDatabase().
                        getStatus().getGenre());

            } else {
                jsonObject.put("message",
                        "Successfully removed from playlist.");
                Integer i = 0;

                List<String> tempSongs = new ArrayList<>();
                List<String> tempAlbums = new ArrayList<>();
                List<String> tempGenres = new ArrayList<>();
                for (String song : playlist.getSongList()) {
                    String statusName = user.getUserDatabase().getStatus().getName();
                    String statusAlbum = user.getUserDatabase().getStatus().getAlbum();
                    if (!(song.equals(statusName)
                            && playlist.getAlbumList().get(i).equals(statusAlbum))) {
                        tempSongs.add(song);
                        tempAlbums.add(playlist.getAlbumList().get(i));
                        tempGenres.add(playlist.getGenreList().get(i));
                    }
                    i++;
                }
                playlist.getSongList().clear();
                playlist.getAlbumList().clear();
                playlist.getGenreList().clear();
                playlist.getSongList().addAll(tempSongs);
                playlist.getAlbumList().addAll(tempAlbums);
                playlist.getGenreList().addAll(tempGenres);


            }
            this.owner = playlist.getOwner();
            this.followers = playlist.getFollowers();
            this.name = playlist.getName();
            this.genreList = playlist.getGenreList();
            this.songList = playlist.getSongList();
            this.albumList = playlist.getAlbumList();
            this.usersFollow = playlist.getUsersFollow();
            Library.getDatabase().getPlaylistList().set(input, this);

        } else {
            jsonObject.put("message",
                    "The specified playlist does not exist.");
        }
        user.getUserDatabase().setLoadStatus(true);
        return jsonObject;
    }

    /**
     * gets top5 playlists
     *
     * @return json
     */
    public JsonNode top5Playlists() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = new ObjectMapper().createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.
                getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        ArrayNode outputs = objectMapper.createArrayNode();
        List<String> top5Playlists = user.getUserDatabase().getTop5Playlists();
        for (String top5 : top5Playlists) {
            outputs.add(top5);
        }
        jsonObject.set("result", outputs);

        return jsonObject;

    }

    /**
     * shows all playlists of the owner
     *
     * @return
     */

    public JsonNode showPlaylist() {


        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = new ObjectMapper().createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        ArrayNode outputs = objectMapper.createArrayNode();

        List<Playlist> playlistUser = user.getUserDatabase().
                selectPlaylistByOwner(jsonElement.getUsername());

        for (Playlist element : playlistUser) {

            if (element.getVisibility() != null) {
                if (element.getVisibility().equals("private")
                        && !element.getOwner().
                        equals(jsonElement.getUsername())) {
                    continue;
                }
            }
            ObjectNode smallJsonObject = objectMapper.createObjectNode();

            smallJsonObject.put("name", element.getName());
            ArrayNode outputs2 = objectMapper.createArrayNode();
            for (String song : element.getSongList()) {
                outputs2.add(song);
            }
            smallJsonObject.set("songs", outputs2);
            if (element.getVisibility() == null) {
                smallJsonObject.put("visibility", "public");
                element.setVisibility("public");

            }

            smallJsonObject.put("visibility", element.getVisibility());
            smallJsonObject.put("followers", element.getUsersFollow().size());
            outputs.add(smallJsonObject);
        }

        jsonObject.set("result", outputs);
        return jsonObject;
    }

    /**
     * accept method for the visitor design pattern for
     * repeatAll
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
     * @param visitor visitor
     * @param playerLoad the current load
     * @param user the current user
     * @param newI the negative timestamp
     * @return the visitor
     */
    public playerLoad acceptNegativeRemaininingTimeForStatusJson(final Visitor visitor,
              final playerLoad playerLoad, final User user, final Integer newI) {
        return visitor.visitNegativeRemaininingTimeForStatusJson(this, playerLoad, user, newI);
    }

}


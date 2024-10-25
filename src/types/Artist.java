package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.Database;
import json.JsonConvertToCategories;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;

/**
 * Artist class
 * with username, events, merch
 * and the methods to add and remove events
 * and merch
 * and to check if the name of the event exists
 * and to check if the name of the merch exists
 * and to add albums
 * and to sum the likes of the songs of an artist
 */
@Getter
public class Artist {
    private static final Integer TOP5 = 5;
    @Setter
    private String pageStatus = "HOME";

    @Getter
    @Setter
    private List<String> notifications;

    @Getter
    @Setter
    private String mostProfitableSong = "";
    @Getter
    @Setter
    private Double songRevenue2 = 0.000;
    @Getter
    @Setter
    private String mostProfitableSong2 = "";
    @Getter
    @Setter
    private int count = 0;
    @Getter
    @Setter
    private Double maxi = -1.0;
    @Getter
    @Setter
    private int premiumCount = 0;
    @Getter
    @Setter
    private int adCount = 0;
    @Getter
    @Setter
    private Double merchRevenue = 0.0;

    private String username;
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Merch> merchList = new ArrayList<>();
    private final Database userDatabase;
    private ArrayList<Album> albums = new ArrayList<>();

    public Artist() {
        this.userDatabase = new Database();
    }

    /**
     * tops of the albums listened by the user
     */
    public JsonNode getTopAlbumsListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> albumListens = new HashMap<>();
        for (User user : Library.getDatabase().getUsers()) {
            if (!user.getSongListen().isEmpty()) {
                for (Song song : user.getListenedSongs()) {
                    if (song.getArtist().equals(username)) {
                        if (albumListens.containsKey(song.getAlbum())) {
                            int count3 = albumListens.get(song.getAlbum());
                            int countAlbum = user.getSongListenCount(song);
                            int countElement = countAlbum;

                            albumListens.put(song.getAlbum(), countElement + count3);
                        } else {
                            int countAlbum = user.getSongListenCount(song);
                            int countElement = countAlbum;
                            albumListens.put(song.getAlbum(), countElement);
                        }
                    }
                }
            }
        }

        List<String> top5Album = new ArrayList<>();
        int z = -1;
        int count3 = albumListens.entrySet().size();
        for (int i = 0; i < TOP5 && i < count3; i++) {
            int max = 0;
            String maxArtist = "";
            int k = 0;
            for (Map.Entry<String, Integer> entry : albumListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtist);

                if (((entry.getValue() > max) || (entry.getValue() == max && result < 0))
                        && !top5Album.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxArtist = entry.getKey();
                }
            }
            top5Album.add(maxArtist);
        }

        for (String album : top5Album) {
            jsonObject.put(album, albumListens.get(album));
        }
        return jsonObject;


    }


    /**
     * tops of the songs listened by the user
     */
    public JsonNode getTopSongsListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> songListens = new HashMap<>();
        for (User user : Library.getDatabase().getUsers()) {
            if (!user.getSongListen().isEmpty()) {
                for (Song song : user.getListenedSongs()) {
                    if (song.getArtist().equals(this.username)) {
                        if (songListens.containsKey(song.getName())) {
                            int count3 = songListens.get(song.getName());
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;

                            songListens.put(song.getName(), countElement + count3);
                        } else {
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;
                            songListens.put(song.getName(), countElement);
                        }
                    }
                }
            }
        }

        List<String> top5Songs = new ArrayList<>();
        int z = -1;
        int count3 = songListens.entrySet().size();
        for (int i = 0; i < TOP5 && i < count3; i++) {
            int max = 0;
            String maxArtist = "";
            int k = 0;
            for (Map.Entry<String, Integer> entry : songListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtist);

                if (((entry.getValue() > max) || (entry.getValue() == max && result < 0))
                        && !top5Songs.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxArtist = entry.getKey();
                }
            }
            top5Songs.add(maxArtist);
        }

        for (String song : top5Songs) {
            jsonObject.put(song, songListens.get(song));
        }
        return jsonObject;


    }

    /**
     * tops of the songs listened by the user
     */
    public List<String> getTopAllSongsListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> songListens = new HashMap<>();
        for (User user : Library.getDatabase().getUsers()) {
            if (!user.getSongListen().isEmpty()) {
                for (Song song : user.getListenedSongs()) {
                    if (song.getArtist().equals(this.username)) {
                        if (songListens.containsKey(song.getName())) {
                            int count3 = songListens.get(song.getName());
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;

                            songListens.put(song.getName(), countElement + count3);
                        } else {
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;
                            songListens.put(song.getName(), countElement);
                        }
                    }
                }
            }
        }

        List<String> top5Songs = new ArrayList<>();
        int z = -1;
        int count3 = songListens.entrySet().size();
        for (int i = 0; i < count3; i++) {
            int max = 0;
            String maxArtist = "";
            int k = 0;
            for (Map.Entry<String, Integer> entry : songListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtist);

                if (((entry.getValue() > max) || (entry.getValue() == max && result < 0))
                        && !top5Songs.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxArtist = entry.getKey();
                }
            }
            top5Songs.add(maxArtist);
        }

        return top5Songs;

    }

    /**
     * tops of the fans listened by the user
     */
    public ArrayNode getTopFansListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode jsonObject = objectMapper.createArrayNode();
        Map<String, Integer> fanListens = new HashMap<>();
        for (User user : Library.getDatabase().getUsers()) {
            if (!user.getSongListen().isEmpty()) {
                for (Song song : user.getListenedSongs()) {
                    if (song.getArtist().equals(this.username)) {
                        if (fanListens.containsKey(user.getUsername())) {
                            int count3 = fanListens.get(user.getUsername());
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;
                            fanListens.put(user.getUsername(), countElement + count3);
                        } else {
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;
                            fanListens.put(user.getUsername(), countElement);
                        }
                    }
                }
            }
        }


        List<String> top5Fans = new ArrayList<>();
        List<String> topFans = new ArrayList<>();

        int z = -1;
        int count3 = fanListens.entrySet().size();
        for (int i = 0; i < count3; i++) {
            int max = 0;
            String maxArtist = "";
            int k = 0;
            for (Map.Entry<String, Integer>
                    entry : fanListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtist);

                if (((entry.getValue() > max)
                        || (entry.getValue() == max && result < 0))
                        && !topFans.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxArtist = entry.getKey();
                }
            }
            topFans.add(maxArtist);
        }

        for (int i = 0; i < TOP5 && i < topFans.size(); i++) {
            top5Fans.add(topFans.get(i));
        }


        for (String fan : top5Fans) {
            jsonObject.add(fan);
        }
        return jsonObject;


    }


    /**
     * tops of the fans listened by the user
     */
    public List<String> getTopAllFansStringListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode jsonObject = objectMapper.createArrayNode();
        Map<String, Integer> fanListens = new HashMap<>();
        for (User user : Library.getDatabase().getUsers()) {
            if (!user.getSongListen().isEmpty()) {
                for (Song song : user.getListenedSongs()) {
                    if (song.getArtist().equals(this.username)) {
                        if (fanListens.containsKey(user.getUsername())) {
                            int count3 = fanListens.get(user.getUsername());
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;
                            fanListens.put(user.getUsername(), countElement + count3);
                        } else {
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;
                            fanListens.put(user.getUsername(), countElement);
                        }
                    }
                }
            }
        }


        List<String> topFans = new ArrayList<>();

        int z = -1;
        int count3 = fanListens.entrySet().size();
        for (int i = 0; i < count3 && i < TOP5; i++) {
            int max = 0;
            String maxArtist = "";
            int k = 0;
            for (Map.Entry<String, Integer>
                    entry : fanListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtist);

                if (((entry.getValue() > max)
                        || (entry.getValue() == max && result < 0))
                        && !topFans.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxArtist = entry.getKey();
                }
            }
            topFans.add(maxArtist);
        }

        return topFans;

    }

    /**
     * listeners of the user
     */
    public int getListeners() {
        ObjectMapper objectMapper = new ObjectMapper();
        int count3 = 0;
        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> fanListens = new HashMap<>();
        for (User user : Library.getDatabase().getUsers()) {
            if (!user.getSongListen().isEmpty()) {
                for (Song song : user.getListenedSongs()) {
                    if (song.getArtist().equals(this.username)) {
                        if (!fanListens.containsKey(user.getUsername())) {
                            count3++;
                            int countSong = user.getSongListenCount(song);
                            int countElement = countSong;
                            fanListens.put(user.getUsername(), countElement);
                        }
                    }
                }
            }
        }


        return count3;


    }

    /**
     * check if the name of the event
     * exists
     *
     * @param name the name of the
     *             supposed event
     * @return true or false
     */
    public boolean checkIfEventExists(final String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check if the name of the merch exists
     * already
     *
     * @param name the name of the supposed
     *             merch
     * @return true or false
     */
    public boolean checkIfMerchExists(final String name) {
        for (Merch merch : merchList) {
            if (merch.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Artist(final String username) {
        this.username = username;
        this.userDatabase = new Database();
    }

    /**
     * add album to the list
     *
     * @param album the name of the album
     */
    public void addAlbum(final Album album) {

        albums.add(album);
    }

    /**
     * the sum of the likes of the songs of an artist
     * by considering all the albums
     *
     * @return that sum
     */
    public Integer sumofLikeAlbumsUsers() {
        Integer sum = 0;
        for (Album album : this.getAlbums()) {
            sum += album.sumofLikesongAlbums();
        }
        return sum;
    }

    /**
     * returns the top5 artists by number of likes by alum
     *
     * @return the json node
     */
    public JsonNode top5Artist() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = new ObjectMapper().createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        ArrayNode outputs = objectMapper.createArrayNode();
        List<String> top5Artist = user.getUserDatabase().getTop5Artists();
        for (String top5 : top5Artist) {
            outputs.add(top5);
        }
        jsonObject.set("result", outputs);

        return jsonObject;

    }
    @Setter
    @Getter
    private static Comparator<Artist> variableComparator = (o1, o2) -> Double.compare(o2.
     getSongRevenue2() + o2.getMerchRevenue(), o1.getSongRevenue2() + o1.getMerchRevenue());

}

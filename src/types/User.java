package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.Database;
import json.Filters.Filters;
import json.Filters.SongFilters;
import json.Filters.AlbumFilter;
import json.Filters.ArtistFilter;
import json.Filters.PlaylistFilters;

import json.JsonConvertToCategories;
import lombok.Getter;
import lombok.Setter;
import status.playerLoad;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * In functia de user, am incercat sa implementez functie care sa ne foloseasca pentru fiecare
 * user, incercam sa implementez "un singleton" pentru fiecare user si sa interfereze
 * cu ceilalti useri
 * la database, de aceea am implementat un userDatabase.(vedeti functia Public User(String owner)).
 * Am implementat un PrefferdedSongs pentru user pentru a putea acesa melodiile preferate de user.
 * Si addPrefferedSongs si RemovedPrefered songs pentru a le adauga, respectiv elimina.
 */

@Getter
@Setter
public class User {
    private static final Integer THREE = 3;
    private static final Integer TIMESTAMPLIMIT = 30;
    private static final Double PREMIUMNUMBER = 1000000.00;
    private static final String ONLINE = "online";
    private static final Integer TOP5 = 5;
    private Boolean statusshuffle = false;
    private List<Playlist> recommendedPlaylists = new ArrayList<>();
    private List<Song> recommendedSongs = new ArrayList<>();
    private List<String> recommended = new ArrayList<>();
    private Integer tempPrice = 0;
    private String searchType = "";
    private int repeat;
    private int loadedEpisode;
    private List<SongListenCount> songListen = new ArrayList<>();

    private List<SongListenCount> premiumSongListen = new ArrayList<>();
    private List<SongListenCount> adSongListen = new ArrayList<>();
    private List<SongListenCount> previousAdSongListen = new ArrayList<>();
    private int prevAdTimestamp = -1;
    private List<String> merchList = new ArrayList<>();


    private List<EpisodeListenCount> episodeListen = new ArrayList<>();

    private Song listenSongs;
    private Episode listenEpisode;
    private List<Artist> artistsNotification = new ArrayList<>();
    private List<String> notification = new ArrayList<>();
    private List<Host> hostsNotification = new ArrayList<>();

    private String accesedUser = "";

    private playerLoad prevPlayerLoad = null;
    /**
     * -- GETTER --
     * gets the index of the song
     *
     * @return the current index of our loaded song
     * within our album/playlis
     */
    private int songIndex;
    private int songindexrandom;
    private String pageStatus = "HOME";
    private String username;
    private int age;
    private String city;
    /**
     * -- GETTER --
     * get the online status of a user
     */
    private String onlineStatus = ONLINE;
    private final Database userDatabase;
    private String accesedUser2;
    private final List<Song> prefferedSongs = new ArrayList<>();
    private final List<Playlist> prefferedPlaylists = new ArrayList<>();
    private int prevtime;
    private List<Song> listenedSongs = new ArrayList<>();
    private List<Song> premiumListenedSongs = new ArrayList<>();
    private List<Song> adListenedSongs = new ArrayList<>();
    private List<Song> previousAdListenedSongs = new ArrayList<>();
    private List<Episode> listenedEpisodes = new ArrayList<>();

    public User() {
        this.userDatabase = new Database();
    }

    public User(final String username) {
        this.username = username;
        this.onlineStatus = ONLINE;
        this.userDatabase = new Database();
    }

    /**
     * increases the time a song which is acoompanied by an ad is listened by
     *
     * @param song
     */
    public void listenToAdSong(final Song song) {

        boolean adSongFound = false;

        for (SongListenCount adSongListenCount : adSongListen) {

            if (adSongListenCount.getSong().equals(song)
                    && !adSongListenCount.getSong().getName().equals("Ad Break")) {
                int currentCount = adSongListenCount.getCount();
                adSongListenCount.setCount(currentCount + 1);
                adSongFound = true;
                break;
            }
        }

        if (!adSongFound && !song.getName().equals("Ad Break")) {
            adSongListen.add(new SongListenCount(song, 1));
            adListenedSongs.add(song);

        }


    }

    /**
     * increases the time a song which is listened by a premium user
     *
     * @param song
     */
    public void listenToPremiumSong(final Song song) {
        song.setCount4(song.getCount4() + 1);

        boolean premiumSongFound = false;
        for (SongListenCount premiumSongListenCount : premiumSongListen) {

            if (premiumSongListenCount.getSong().equals(song)
                    && !premiumSongListenCount.getSong().getName().equals("Ad Break")) {
                int currentCount = premiumSongListenCount.getCount();
                premiumSongListenCount.setCount(currentCount + 1);
                premiumSongFound = true;
                break;
            }
        }

        if (!premiumSongFound && !song.getName().equals("Ad Break")) {
            premiumSongListen.add(new SongListenCount(song, 1));
            premiumListenedSongs.add(song);

        }


    }

    /**
     * gives money to the artists for premium user
     */
    public void givePremiumMoneyToSongs() {

        int total = 0;
        for (SongListenCount premiumSongListenCount : premiumSongListen) {
            int currentCount = premiumSongListenCount.getCount();
            if (!premiumSongListenCount.getSong().getName().equals("Ad Break")) {
                total += currentCount;
            }

        }
        if (total == 0) {
            return;
        }
        for (SongListenCount premiumSongListenCount : premiumSongListen) {
            int currentCount = premiumSongListenCount.getCount();
            Song song = premiumSongListenCount.getSong();
            double value = (PREMIUMNUMBER * currentCount) / total;
            String element = song.getName() + "#" + song.getArtist();
            if (Library.getDatabase().getSongArtistMap().containsKey(element)) {
                Library.getDatabase().getSongArtistMap().put(element,
                        Library.getDatabase().getSongArtistMap().get(element) + value);
            } else {
                Library.getDatabase().getSongArtistMap().put(element, value);
            }
        }
        for (SongListenCount premiumSongListenCount : premiumSongListen) {


            int currentCount = premiumSongListenCount.getCount();
            Song song = premiumSongListenCount.getSong();
            Artist artist = Library.getDatabase().getArtistInstance(song.getArtist());
            artist.setPremiumCount(artist.getPremiumCount() + currentCount);
            song.setPremiumCount(currentCount);
            double value = (PREMIUMNUMBER * currentCount) / total;
            double songValue = song.getSongRevenue();
            double songRevenue = (value + songValue);
        }


        for (Song song : Library.getDatabase().getSongs()) {
            if (!song.getName().equals("Ad Break")) {
                double value = (PREMIUMNUMBER * song.getPremiumCount()) / total;
                song.setSongRevenue(song.getSongRevenue() + value);
                song.setPremiumCount(0);
            }
        }

        for (Artist artist : Library.getDatabase().getArtists()) {
            double value = (PREMIUMNUMBER * artist.getPremiumCount()) / total;
            artist.setSongRevenue2(artist.getSongRevenue2() + value);
            artist.setPremiumCount(0);
        }

    }

    /**
     * gives money to the artists for premium user
     */
    public void giveAdMoneyToSongs(final int adPrice) {
        Library.getDatabase().setTotale(adPrice + Library.getDatabase().getTotale());

        int total = 0;
        for (SongListenCount adSongListenCount : adSongListen) {
            int currentCount = adSongListenCount.getCount();

            if (adSongListenCount.getSong().getName().equals("Ad Break")) {
                continue;
            }
            total += currentCount;

        }

        if (total == 0) {
            return;
        }
        for (SongListenCount adSongListenCount : adSongListen) {
            int currentCount = adSongListenCount.getCount();
            Song song = adSongListenCount.getSong();
            double value = ((double) adPrice * currentCount) / total;
            String element = song.getName() + "#" + song.getArtist();
            if (Library.getDatabase().getSongArtistMap().containsKey(element)) {
                Library.getDatabase().getSongArtistMap().put(element,
                        Library.getDatabase().getSongArtistMap().get(element) + value);
            } else {
                Library.getDatabase().getSongArtistMap().put(element, value);
            }
        }
        for (SongListenCount adSongListenCount : adSongListen) {

            int currentCount = adSongListenCount.getCount();
            Song song = adSongListenCount.getSong();
            Artist artist = Library.getDatabase().getArtistInstance(song.getArtist());
            artist.setAdCount(artist.getAdCount() + currentCount);
            song.setAdCount(song.getAdCount() + currentCount);

        }

        for (Song song : Library.getDatabase().getSongs()) {
            if (!song.getName().equals("Ad Break")) {
                double value = ((double) adPrice * song.getAdCount()) / total;
                song.setSongRevenue(song.getSongRevenue() + value);
                song.setAdCount(0);
            }
        }
        for (Artist artist : Library.getDatabase().getArtists()) {
            double adPrice2 = (double) adPrice;
            double value = (adPrice2 * artist.getAdCount()) / total;
            artist.setSongRevenue2(artist.getSongRevenue2() + value);
            artist.setAdCount(0);
        }

    }

    /**
     * change premium status
     */
    public void changePremiumStatus() {
        if (getUserDatabase().getPremiumStatus()) {
            givePremiumMoneyToSongs();
            premiumSongListen.clear();
            premiumListenedSongs.clear();
            getUserDatabase().setPremiumStatus(false);
        } else {

            getUserDatabase().setPremiumStatus(true);

        }
    }

    /**
     * increases the time a song is listened by
     *
     * @param song
     */
    public void listenToSong(final Song song) {


        if (!getUserDatabase().getPremiumStatus()) {
            listenToAdSong(song);
        } else {
            listenToPremiumSong(song);
        }
        boolean songFound = false;
        for (SongListenCount songListenCount : songListen) {

            if (songListenCount.getSong().equals(song)) {
                int currentCount = songListenCount.getCount();
                songListenCount.setCount(currentCount + 1);
                songFound = true;
                break;
            }
        }

        if (!songFound) {
            songListen.add(new SongListenCount(song, 1));
            listenedSongs.add(song);

        }


    }

    /**
     * search songlisten by song
     */
    public int getSongListenCount(final Song song) {
        for (SongListenCount songListenCount : songListen) {
            if (songListenCount.getSong().equals(song)) {
                return songListenCount.getCount();
            }
        }
        return -1;
    }

    /**
     * search songlisten by song
     */
    public int getEpisodeListenCount(final Episode episode) {
        for (EpisodeListenCount episodeListenCount : episodeListen) {
            if (episodeListenCount.getEpisode().equals(episode)) {
                return episodeListenCount.getCount();
            }
        }
        return -1;
    }

    /**
     * tops of the artists listened by the user
     */
    public List<String> getTopArtistsListened() {

        Map<String, Integer> artistListens = new HashMap<>();
        for (Artist artist : Library.getDatabase().getArtists()) {
            for (Song song : listenedSongs) {
                if (song.getArtist().equals(artist.getUsername())) {
                    if (artistListens.containsKey(artist.getUsername())) {
                        int count = artistListens.get(artist.getUsername());
                        int countSong = getSongListenCount(song);
                        int countElement = countSong;
                        artistListens.put(artist.getUsername(), count + countElement);
                    } else {
                        int countSong = getSongListenCount(song);
                        int countElement = countSong;
                        artistListens.put(artist.getUsername(), countElement);
                    }
                }
            }
        }

        List<String> topArtists = new ArrayList<>();
        int count = artistListens.entrySet().size();
        for (int i = 0; i < count; i++) {
            double max = -1.0;
            String maxArtistString = "";
            for (Map.Entry<String, Integer> entry : artistListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtistString);
                Artist artist = Library.getDatabase().getArtistInstance(maxArtistString);

                double sum = artist.getMerchRevenue() + artist.getSongRevenue2();
                if ((sum > max || (sum == max && result < 0))
                        && !topArtists.contains(entry.getKey())) {
                    max = sum;
                    maxArtistString = entry.getKey();
                }
            }


            topArtists.add(maxArtistString);
        }
        return topArtists;


    }

    /**
     * tops 5 of the artists listened by the user
     */
    public JsonNode getTop5ArtistsListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> artistListens = new HashMap<>();

        for (Artist artist : Library.getDatabase().getArtists()) {
            for (Song song : listenedSongs) {
                if (song.getArtist().equals(artist.getUsername())) {
                    if (artistListens.containsKey(artist.getUsername())) {
                        int count = artistListens.get(artist.getUsername());
                        int countSong = getSongListenCount(song);
                        int countElement = countSong;
                        artistListens.put(artist.getUsername(), count + countElement);
                    } else {
                        int countSong = getSongListenCount(song);
                        int countElement = countSong;
                        artistListens.put(artist.getUsername(), countElement);
                    }
                }
            }
        }

        List<String> top5Artists = new ArrayList<>();
        int count = artistListens.entrySet().size();
        for (int i = 0; i < TOP5 && i < count; i++) {
            int max = 0;
            String maxArtist = "";

            for (Map.Entry<String, Integer> entry : artistListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtist);
                if ((entry.getValue() > max || (entry.getValue() == max && result < 0))
                        && !top5Artists.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxArtist = entry.getKey();
                }
            }
            top5Artists.add(maxArtist);
        }

        for (String artist : top5Artists) {
            jsonObject.put(artist, artistListens.get(artist));
        }
        return jsonObject;


    }

    /**
     * tops of the albums listened by the user
     */
    public JsonNode getTopAlbumsListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> albumListens = new HashMap<>();

        for (Album album : Library.getDatabase().getAlbums()) {
            for (Song song : listenedSongs) {
                String element = album.getName();
                if (song.getAlbum().equals(album.getName())
                        && song.getArtist().equals(album.getArtist())) {
                    if (albumListens.containsKey(element)) {
                        int count = albumListens.get(element);
                        int countSong = getSongListenCount(song);
                        int countElement = countSong;

                        albumListens.put(element,
                                count + countElement);
                    } else {
                        int countSong = getSongListenCount(song);
                        int countElement = countSong;
                        albumListens.put(element, countElement);
                    }
                }
            }

        }

        List<String> top5Album = albumListens.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(TOP5)
                .map(Map.Entry::getKey)
                .toList();

        for (String album : top5Album) {
            jsonObject.put(album, albumListens.get(album));
        }
        return jsonObject;


    }

    /**
     * buy premium Json
     */
    public JsonNode buyPremiumJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }
        if (userDatabase.getPremiumStatus()) {
            jsonObject.put("message", jsonElement.getUsername()
                    + " is already a premium user.");
            return jsonObject;
        }
        changePremiumStatus();
        jsonObject.put("message", jsonElement.getUsername()
                + " bought the subscription successfully.");


        return jsonObject;


    }

    /**
     * buy merch Json
     */

    public JsonNode buyMerchJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());

        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }
        if (!user.getUserDatabase().getPageStatus().equals("ARTIST")) {
            jsonObject.put("message", "Cannot buy merch from this page.");
            return jsonObject;
        }
        boolean ok = false;
        for (Artist artist : Library.getDatabase().getArtists()) {

            for (Merch merch : artist.getMerchList()) {
                if (merch.getName().equals(jsonElement.getName())) {
                    ok = true;
                    artist.setMerchRevenue(artist.getMerchRevenue() + merch.getPrice());
                    jsonObject.put("message", jsonElement.getUsername()
                            + " has added new merch successfully.");
                    user.getMerchList().add(merch.getName());
                    return jsonObject;
                }

            }

        }
        jsonObject.put("message", "The merch " + jsonElement.getName()
                + " doesn't exist.");
        return jsonObject;
    }

    /**
     * see Merch
     */
    public JsonNode seeMerchJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (String merchName : user.getMerchList()) {
            arrayNode.add(merchName);
        }
        jsonObject.put("result", arrayNode);
        return jsonObject;
    }

    /**
     * subscribe Json
     */
    public JsonNode subscribeJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }
        if (Library.getDatabase().getArtistElement().isEmpty()
                && Library.getDatabase().getHostElement().isEmpty()) {
            jsonObject.put("message", "To subscribe you need to be on the "
                    + "page of an artist or host.");
            return jsonObject;
        }
        if (Library.getDatabase().checkIfArtistValid(Library.getDatabase().getArtistElement())) {
            Artist artist = Library.getDatabase().getArtistInstance(Library.
                    getDatabase().getArtistElement());
            boolean alreadyExist = false;
            for (Artist artist1 : user.getArtistsNotification()) {
                if (artist1.getUsername().equals(artist.getUsername())) {
                    alreadyExist = true;
                    break;
                }
            }
            if (!alreadyExist) {
                user.getArtistsNotification().add(artist);
                jsonObject.put("message", jsonElement.getUsername()
                        + " subscribed to " + artist.getUsername() + " successfully.");
            } else {
                user.getArtistsNotification().remove(artist);
                jsonObject.put("message", jsonElement.getUsername()
                        + " unsubscribed from " + artist.getUsername() + " successfully.");
            }
        }
        if (Library.getDatabase().checkIfHostValid(Library.getDatabase().getHostElement())) {
            Host host = Library.getDatabase().getHostInstance(Library.
                    getDatabase().getHostElement());
            boolean alreadyExist = false;
            for (Host host1 : user.getHostsNotification()) {
                if (host1.getUsername().equals(host.getUsername())) {
                    alreadyExist = true;
                    break;
                }
            }
            if (!alreadyExist) {
                user.getHostsNotification().add(host);

                jsonObject.put("message", jsonElement.getUsername()
                        + " subscribed to " + host.getUsername() + " successfully.");
            } else {
                user.getHostsNotification().remove(host);
                jsonObject.put("message", jsonElement.getUsername()
                        + " unsubscribed from  " + host.getUsername() + " successfully.");
            }
        }


        return jsonObject;


    }

    /**
     * notifications Json
     */
    public JsonNode notificationsJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (String newNotification : user.getNotification()) {
            String[] notificationsWord = newNotification.split("  ");
            ObjectNode smallJsonObject = objectMapper.createObjectNode();

            smallJsonObject.put("name", "New " + notificationsWord[0]);
            smallJsonObject.put("description", "New " + notificationsWord[0]
                    + " from " + notificationsWord[notificationsWord.length - 1] + ".");
            arrayNode.add(smallJsonObject);
        }
        jsonObject.set("notifications", arrayNode);
        user.getNotification().clear();


        return jsonObject;


    }

    /**
     * ad break Json
     */
    public JsonNode adBreakJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {

            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        if (!user.getUserDatabase().getLoadStatus()) {

            jsonObject.put("message", jsonElement.getUsername()
                    + " is not playing any music.");
            return jsonObject;
        }
        jsonObject.put("message", "Ad inserted successfully.");
        prevAdTimestamp = jsonElement.getPrice();
        return jsonObject;


    }

    /**
     * cancel premium Json
     */
    public JsonNode cancelPremiumJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }
        if (!userDatabase.getPremiumStatus()) {
            jsonObject.put("message", jsonElement.getUsername()
                    + " is not a premium user.");
            return jsonObject;
        }
        changePremiumStatus();

        jsonObject.put("message", jsonElement.getUsername()
                + " cancelled the subscription successfully.");


        return jsonObject;


    }

    /**
     * tops of the genre listened by the user
     */
    public JsonNode getTopGenreListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> genreListens = new HashMap<>();
        Artist artist1 = new Artist();

        for (Song song : listenedSongs) {

            if (genreListens.containsKey(song.getGenre())) {
                int count = genreListens.get(song.getGenre());
                int countSong = getSongListenCount(song);
                int countElement = countSong;
                genreListens.put(song.getGenre(), count + countElement);
            } else {
                int countSong = getSongListenCount(song);
                int countElement = countSong;
                genreListens.put(song.getGenre(), countElement);
            }

        }


        List<String> top5Genre = new ArrayList<>();
        int z = -1;
        int count = genreListens.entrySet().size();
        for (int i = 0; i < TOP5 && i < count; i++) {
            int max = 0;
            String maxArtist = "";
            int k = 0;
            for (Map.Entry<String, Integer> entry : genreListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtist);
                if ((entry.getValue() > max || (entry.getValue() == max && result < 0))
                        && !top5Genre.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxArtist = entry.getKey();
                }
            }
            top5Genre.add(maxArtist);
        }

        for (String genre : top5Genre) {
            jsonObject.put(genre, genreListens.get(genre));
        }
        return jsonObject;


    }

    /**
     * tops of the songs listened by the user
     */
    public JsonNode getTopSongsListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> songsListens = new HashMap<>();

        for (Song song : listenedSongs) {
            String element = song.getName() + "#" + song.getArtist();

            if (songsListens.containsKey(element)) {
                int count = songsListens.get(element);
                int countSong = getSongListenCount(song);
                int countElement = countSong;
                songsListens.put(element, count + countElement);
            } else {
                int countSong = getSongListenCount(song);
                int countElement = countSong;
                songsListens.put(element, countElement);
            }

        }

        List<String> top5Songs = new ArrayList<>();
        int z = -1;
        int count = songsListens.entrySet().size();
        for (int i = 0; i < TOP5 && i < count; i++) {
            int max = 0;
            String maxArtist = "";
            int k = 0;
            for (Map.Entry<String, Integer> entry : songsListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtist);
                if ((entry.getValue() > max || (entry.getValue() == max && result < 0))
                        && !top5Songs.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxArtist = entry.getKey();
                }
            }
            top5Songs.add(maxArtist);
        }

        for (String song : top5Songs) {
            Integer index = song.indexOf("#");
            String songName = song.substring(0, index);
            jsonObject.put(songName, songsListens.get(song));
        }
        return jsonObject;


    }

    /**
     * increases the time an episode is listened by
     * @param episode
     */
    public void listenToEpisode(final Episode episode) {
        List<Episode> episodeList = new ArrayList<>();
        for (EpisodeListenCount episodeListenCount : episodeListen) {
            episodeList.add(episodeListenCount.getEpisode());
        }
        if (episodeList.contains(episode)) {
            int index = episodeList.indexOf(episode);
            int count = episodeListen.get(index).getCount();
            episodeListen.get(index).setCount(count + 1);
        } else {

            episodeListen.add(new EpisodeListenCount(episode, 1));
            listenedEpisodes.add(episode);
        }

    }

    private HashMap<String, Integer> conditionSongAddGenre(final Song song,
               final HashMap<String, Integer> genreMap) {
        if (genreMap.containsKey(song.getGenre())) {
            int count = genreMap.get(song.getGenre());
            genreMap.put(song.getGenre(), count + 1);
        } else {
            genreMap.put(song.getGenre(), 1);
        }
        return genreMap;

    }

    private Playlist createRandomPlaylist(final String owner,
          final List<Song> songList, final HashMap<String, Integer> genreMap) {
        Playlist playlist = new Playlist();
        playlist.setName(owner + "'s recommendations");
        playlist.setOwner(owner);
        playlist.setVisibility("private");
        playlist.setFollowers(0);
        List<Map.Entry<String, Integer>> sortedGenreHashmap = new ArrayList<>(genreMap.entrySet());
        sortedGenreHashmap.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        int i = 0, limit2 = TOP5;
        for (Map.Entry<String, Integer> entry : sortedGenreHashmap.stream().
                limit(THREE).toList()) {
            String genre = entry.getKey();
            if (i != 0) {
                limit2 -= (THREE - i);
            }
            List<Song> songListGenre = Library.getDatabase().getSongs().stream()
                    .filter(song -> song.getGenre().equals(genre)).limit(limit2).toList();
            if (songListGenre.isEmpty()) {
                continue;
            }
            int index = (int) (Math.random() * songListGenre.size());
            Song song = songListGenre.get(index);
            if (songList.contains(song)) {
                continue;
            }
            songList.add(song);
            playlist.getSongList().add(song.getName());
            playlist.getAlbumList().add(song.getAlbum());
            playlist.getGenreList().add(song.getGenre());
        }

        return playlist;
    }

    /**
     * update my recommandations Json for random plalist
     */
    public JsonNode updateMyRecommandationsRandomPlaylistJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {
                jsonObject.put("message", jsonElement.getUsername() + " is not a normal user.");
            }
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }

        String songName = user.getUserDatabase().getStatus().getName();
        String songAlbum = user.getUserDatabase().getStatus().getAlbum();
        int index = Library.searchSongByNameAndAlbum(songName, songAlbum);
        if (index == -1) {
            jsonObject.put("message", "No new recommendations were found");
            return jsonObject;
        }

        List<Song> songList = new ArrayList<>();
        HashMap<String, Integer> genreMap = new HashMap<>();
        for (Song song2 : user.getPrefferedSongs()) {
            if (songList.isEmpty() || !songList.contains(song2)) {
                songList.add(song2);
                genreMap = conditionSongAddGenre(song2, genreMap);
            }
        }

        List<Playlist> playlistsUser = Library.getDatabase().getPlaylistList().stream()
                .filter(playlist -> playlist.getOwner().equals(user.getUsername())).toList();
        for (Playlist playlist : playlistsUser) {
            for (int i = 0; i < playlist.getSongList().size(); i++) {
                int index3 = Library.searchSongByNameAndAlbum(playlist.getSongList().get(i),
                        playlist.getAlbumList().get(i));
                Song song3 = Library.getDatabase().getSongs().get(index3);
                if (songList.isEmpty() || !songList.contains(song3)) {
                    songList.add(song3);
                    genreMap = conditionSongAddGenre(song3, genreMap);
                }
            }
        }

        for (Playlist playlist : user.getPrefferedPlaylists()) {
            for (int i = 0; i < playlist.getSongList().size(); i++) {
                int index3 = Library.searchSongByNameAndAlbum(playlist.getSongList().get(i),
                        playlist.getAlbumList().get(i));
                Song song3 = Library.getDatabase().getSongs().get(index3);
                if (songList.isEmpty() || !songList.contains(song3)) {
                    songList.add(song3);
                    genreMap = conditionSongAddGenre(song3, genreMap);
                }
            }
        }


        if (songList.isEmpty()) {
            jsonObject.put("message", "No new recommendations were found");
        } else {
            Playlist playlist = createRandomPlaylist(user.getUsername(), songList, genreMap);
            user.getRecommendedPlaylists().add(playlist);
            user.getRecommended().add(playlist.getName());
            jsonObject.put("message", "The recommendations for user " + jsonElement.getUsername()
                    + " have been updated successfully.");
        }

        return jsonObject;
    }

    /**
     * update my recommandations Json for random plalist
     */
    public JsonNode loadRecommendationJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (!user.getOnlineStatus().equals("online")) {

            jsonObject.put("message", jsonElement.getUsername()
                    + " is offline.");
            return jsonObject;
        }
        if (user.getRecommended().isEmpty()) {
            jsonObject.put("message", "No recommendations available.");
            return jsonObject;
        }

        String lastRecommendation = user.getRecommended().
                get(user.getRecommended().size() - 1);
        List<String> songNames = user.getRecommendedSongs().stream()
                .map(Song::getName).toList();
        List<String> playlistNames = user.getRecommendedPlaylists().stream()
                .map(Playlist::getName).toList();
        if (songNames.contains(lastRecommendation)) {
            Song song = user.getRecommendedSongs().get(user.
                    getRecommendedSongs().size() - 1);
            playerLoad playerLoad = new playerLoad();
            playerLoad.setAlbum("");
            playerLoad.setRepeat("No Repeat");
            playerLoad.setPaused(false);
            playerLoad.setName(song.getName());
            playerLoad.setAlbum(song.getAlbum());
            playerLoad.setGenre(song.getGenre());

            playerLoad.setType("song");
            playerLoad.setRemainedTime(song.getDuration());
            if (!song.getName().equals("Ad Break")) {
                user.listenToSong(song);
            }
            user.getUserDatabase().setStatus(playerLoad);
        } else if (playlistNames.contains(lastRecommendation)) {
            playerLoad playerLoad = new playerLoad();

            Playlist playlist = user.getUserDatabase().
                    selectPlaylistByName(user.getUserDatabase().
                            getSelectElement());

            playerLoad.setRepeat("No Repeat");

            playerLoad.setType("playlist");
            user.getUserDatabase().setLoadedPlaylist(playlist);
            user.setSongIndex(0);
            playlist.setSongList();

            user.setStatusshuffle(false);

            String searchSong = playlist.getSongList().get(playlist.getnewSongIndex(user));
            String searchAlbum = playlist.getAlbumList().get(playlist.getnewSongIndex(user));
            int index = Library.searchSongByNameAndAlbum(searchSong, searchAlbum);
            playerLoad.setAlbum(Library.getDatabase().getSongs().get(index).getAlbum());
            Song song2 = Library.getDatabase().getSongs().get(index);
            if (!song2.getName().equals("Ad Break")) {
                user.listenToSong(song2);
            }
            playerLoad.setGenre(Library.getDatabase().getSongs().get(index).getGenre());
            playerLoad.setName(Library.getDatabase().getSongs().get(index).getName());
            playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                    get(index).getDuration());
            playerLoad.setPaused(false);
            playerLoad.setRemainedTime(song2.getDuration());
            user.getUserDatabase().setStatus(playerLoad);

        } else {
            jsonObject.put("message", "No recommendations available.");
            return jsonObject;
        }
        jsonObject.put("message", "Playback loaded successfully.");


        return jsonObject;
    }


    /**
     * update my recommandations Json for random song
     */
    public JsonNode updateMyRecommandationsRandomSongJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {
                jsonObject.put("message", jsonElement.getUsername() + " is not a normal user.");
            }
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }

        int index = Library.searchSongByNameAndAlbum(user.getUserDatabase().getStatus().getName(),
                user.getUserDatabase().getStatus().getAlbum());
        Song song = Library.getDatabase().getSongs().get(index);
        int timestamp = song.getDuration() - user.getUserDatabase().
                getStatus().getRemainedTime();


        if (!user.getUserDatabase().getLoadStatus() || timestamp < TIMESTAMPLIMIT
                || song.getName().isEmpty()) {
            jsonObject.put("message", "No new recommendations were found");
            return jsonObject;
        }

        List<Song> genreSongList = Library.getDatabase().getSongs().stream()
                .filter(song1 -> song1.getGenre().equals(song.getGenre())).toList();
        Random random = new Random(timestamp);
        int randomNumber = random.nextInt(genreSongList.size());
        user.getRecommendedSongs().add(genreSongList.get(randomNumber));
        user.getRecommended().add(genreSongList.get(randomNumber).getName());
        jsonObject.put("message", "The recommendations for user " + jsonElement.getUsername()
                + " have been updated successfully.");
        return jsonObject;
    }

    /**
     * update my recommandations Json
     */
    public JsonNode updateMyRecommandationsFansPlaylistJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {
                jsonObject.put("message", jsonElement.getUsername() + " is not a normal user.");
            }
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }

        String songName = user.getUserDatabase().getStatus().getName();
        String songAlbum = user.getUserDatabase().getStatus().getAlbum();
        int index = Library.searchSongByNameAndAlbum(songName, songAlbum);
        Song song = Library.getDatabase().getSongs().get(index);

        Artist artist = Library.getDatabase().getArtistInstance(song.getArtist());
        List<String> songList = new ArrayList<>();
        for (String userString : artist.getTopAllFansStringListened()) {
            User user1 = Library.getDatabase().getUserInstance(userString);
            for (String stringSong : user1.getTop5SongsUser()) {
                if (songList.isEmpty() || !songList.contains(stringSong)) {
                    songList.add(stringSong);
                }
            }

        }

        if (songList.isEmpty()) {
            jsonObject.put("message", "No new recommendations were found");
        } else {
            Playlist playlist = new Playlist();
            playlist.setOwner(Library.getDatabase().getJson().getUsername());
            playlist.setVisibility("private");
            String name = artist.getUsername() + " Fan Club recommendations";
            playlist.setName(name);
            playlist.setFollowers(0);
            playlist.setSongs(songList);
            user.getRecommendedPlaylists().add(playlist);
            user.getRecommended().add(playlist.getName());
            jsonObject.put("message", "The recommendations for user " + jsonElement.getUsername()
                    + " have been updated successfully.");
        }

        return jsonObject;
    }

    /**
     * goes to previous page Json
     */
    public JsonNode previousPageJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {
                jsonObject.put("message", jsonElement.getUsername() + " is not a normal user.");
            }
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }

        if (user.getUserDatabase().getPageStatusStack().size() <= 1) {
            jsonObject.put("message", "There are no pages left to go back.");
            return jsonObject;
        }
        jsonObject.put("message", "The user " + user.getUsername()
                + " has navigated successfully to the previous page.");
        String page = user.getUserDatabase().getPageStatusStack().pop();
        user.getUserDatabase().getPageStatusNextStack().push(page);
        String page2 = user.getUserDatabase().getPageStatusStack().peek();
        user.getUserDatabase().setPageStatus(page2);
        return jsonObject;
    }

    /**
     * goes to next page Json
     */
    public JsonNode nextPageJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (!Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {
                jsonObject.put("message", jsonElement.getUsername() + " is not a normal user.");
            }
            jsonObject.put("message", "The username " + jsonElement.getUsername()
                    + " doesn't exist.");
            return jsonObject;
        }

        if (user.getUserDatabase().getPageStatusNextStack().size() <= 1) {
            jsonObject.put("message", "There are no pages left to go forward.");
            return jsonObject;
        }
        jsonObject.put("message", "The user " + user.getUsername()
                + " has navigated successfully to the next page.");
        String page = user.getUserDatabase().getPageStatusNextStack().pop();
        user.getUserDatabase().getPageStatusStack().push(page);
        String page2 = user.getUserDatabase().getPageStatusStack().peek();
        user.getUserDatabase().setPageStatus(page2);
        return jsonObject;
    }

    /**
     * tops of the episodes listened by the user
     */
    public JsonNode getTopEpisodesListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> episodesListens = new HashMap<>();

        for (Episode episode : listenedEpisodes) {
            if (episodesListens.containsKey(episode.getName())) {
                int count = episodesListens.get(episode.getName());
                int countEpisode = getEpisodeListenCount(episode);
                int countElement = countEpisode;
                episodesListens.put(episode.getName(), count + countElement);
            } else {
                int countEpisode = getEpisodeListenCount(episode);
                int countElement = countEpisode;
                episodesListens.put(episode.getName(), countElement);
            }

        }

        List<String> top5Episodes = new ArrayList<>();
        int z = -1;
        int count = episodesListens.entrySet().size();
        for (int i = 0; i < TOP5 && i < count; i++) {
            int max = 0;
            String maxHost = "";
            int k = 0;
            for (Map.Entry<String, Integer> entry : episodesListens.entrySet()) {
                int result = entry.getKey().compareTo(maxHost);
                if ((entry.getValue() > max || (entry.getValue() == max && result < 0))
                        && !top5Episodes.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxHost = entry.getKey();
                }
            }
            top5Episodes.add(maxHost);
        }

        for (String episode : top5Episodes) {
            jsonObject.put(episode, episodesListens.get(episode));
        }
        return jsonObject;


    }

    /**
     * in top5songs for the current user we implement the principle with the
     * origin in which likes were given by the user,
     * if several elements have maximum values
     */
    public List<String> getTop5SongsUser() {

        List<String> top5 = new ArrayList<>();
        int i = 0;
        int maxPrev = -1;
        int maxi = -1, maxiIndex = -1;
        while (i < TOP5 && i < getPrefferedSongs().size()) {
            maxi = -1;
            maxiIndex = -1;
            int j = 0;
            for (Song element3 : getPrefferedSongs()) {
                if (maxPrev == -1) {
                    if (element3.getUsersLiked().size() > maxi) {
                        maxi = element3.getUsersLiked().size();
                        maxiIndex = j;
                    }
                } else {
                    if (element3.getUsersLiked().size() > maxi
                            && element3.getUsersLiked().size()
                            <= maxPrev && !top5.contains(element3.getName())) {
                        maxi = element3.getUsersLiked().size();
                        maxiIndex = j;
                    }
                }
                j++;
            }
            top5.add(Library.getDatabase().getSongs().
                    get(maxiIndex).getName());
            maxPrev = maxi;

            i++;
        }

        return top5;
    }

    /**
     * sets the index for the song list
     *
     * @param songIndex2 song index
     */
    public void setSongIndex(final int songIndex2) {
        this.songIndex = songIndex2;
    }

    /**
     * added prefered songs by user
     *
     * @param name of the song
     */
    public void addPrefferedSong(final Song name) {
        if (prefferedSongs.contains(name)) {
            return;
        }
        prefferedSongs.add(name);
    }

    /**
     * sets the online status of the user
     * if it online, it sets offline and vice-versa
     * it changes the online status
     */
    public void setOnlineStatus() {

        if (this.getOnlineStatus().equals("online")) {
            this.onlineStatus = "offline";
        } else {
            this.onlineStatus = "online";
        }
    }

    /**
     * here we are trying to implement a followJson function,
     * which changes the state of the playlist if we follow, unfollow and error
     */
    public JsonNode followJson() {


        ObjectNode jsonObject = new ObjectMapper().createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.
                getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (user.getUserDatabase().getSelectElement() == null
                || !user.getUserDatabase().getSelectStatus()) {
            jsonObject.put("message",
                    "Please select a source before following "
                            + "or unfollowing.");
            return jsonObject;
        }

        if (user.getUserDatabase().selectPlaylistByName(user.
                getUserDatabase().getSelectElement()) == null) {
            jsonObject.put("message", "The selected source"
                    + " is not a playlist.");
            return jsonObject;
        }

        Playlist playlist = user.getUserDatabase().
                selectPlaylistByName(user.getUserDatabase().
                        getSelectElement());
        assert playlist != null;
        if (playlist.getOwner().equals(Library.getDatabase().
                getJson().getUsername())) {
            jsonObject.put("message", "You cannot"
                    + " follow or unfollow your own playlist.");
            return jsonObject;
        } else {

            if (!playlist.searchUsersWhoFollowed(Library.
                    getDatabase().getJson().getUsername())) {

                jsonObject.put("message", "Playlist "
                        + "followed successfully.");
                playlist.getUsersFollow().add(jsonElement.getUsername());
                user.getPrefferedPlaylists().add(playlist);

                playlist.setFollowers(playlist.getFollowers() + 1);

            } else {

                jsonObject.put("message", "Playlist unfollowed"
                        + " successfully.");
                playlist.getUsersFollow().removeIf(user2
                        -> user2.equals(jsonElement.getUsername()));
                user.getPrefferedPlaylists().removeIf(playlist2 ->
                        playlist2.getName().equals(playlist.getName()));
                playlist.setFollowers(playlist.getFollowers() - 1);
            }

        }
        return jsonObject;
    }

    /**
     * this class if we try to implement,if the size of the
     * playlist is smaller than playlistid-1 (we take them from 0),
     * then we give an error if it is not found in our function after
     * name, can be empty or invalid and error again, if the item
     * number in the selected item is* bigger than selected.size()
     * gives an error again, after that* we set the state
     * @return we set the state of the element, whether it is
     * public or private (depending on by the preceding
     * state, or if it was not previously initialized
     * the state (I give it private)
     */
    public JsonNode visibilityJson() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());

        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        List<String> selected = user.getUserDatabase().getSelected();
        List<Playlist> playlistUser = user.getUserDatabase().
                selectPlaylistByOwner(jsonElement.getUsername());
        if (playlistUser.size() <= (jsonElement.getPlaylistId() - 1)) {
            user.getUserDatabase().setSelectstatus(false);
            jsonObject.put("message",
                    "The specified playlist ID is too high.");
            return jsonObject;
        }
        Playlist playlistFilter = playlistUser.get(jsonElement.
                getPlaylistId() - 1);

        if (user.getUserDatabase().selectPlaylistByName(playlistFilter.
                getName()) == null) {
            user.getUserDatabase().setSelectstatus(false);
            jsonObject.put("message",
                    "The specified playlist ID is too high.");
            return jsonObject;
        }
        if (jsonElement.getItemNumber() > selected.size()) {
            user.getUserDatabase().setSelectstatus(false);
            jsonObject.put("message", "The selected ID is too high.");
            return jsonObject;
        } else {
            Playlist playlistselected = user.getUserDatabase().
                    selectPlaylistByName(playlistFilter.getName());
            if (playlistselected.getVisibility() == null) {
                jsonObject.put("message",
                        "Visibility status updated successfully "
                                + "to private.");
                playlistselected.setVisibility("private");
            } else {
                if (playlistselected.getVisibility().equals("private")) {
                    jsonObject.put("message",
                            "Visibility status updated successfully "
                                    + "to public.");
                    playlistselected.setVisibility("public");

                } else {
                    jsonObject.put("message", "Visibility status"
                            + " updated successfully to private.");
                    playlistselected.setVisibility("private");
                }
            }
        }
        return jsonObject;
    }

    /**
     * shows the songs liked by the user
     *
     * @return returns the songs liked by the user
     */
    public static JsonNode showsongliked() {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        List<Song> selected = new ArrayList<>(user.getPrefferedSongs());
        ArrayNode outputs = objectMapper.createArrayNode();
        if (user.getOnlineStatus().equals("online")) {

            for (Song elements : selected) {
                outputs.add(elements.getName());
            }

            jsonObject.set("result", outputs);
            user.getUserDatabase().setLoadStatus(false);
            user.getUserDatabase().setSelectstatus(false);
        } else {
            jsonObject.put("message", "User " + user.getUsername() + " is not logged in.");
        }
        return jsonObject;
    }

    private void conditionPlayerLoadNull(final playerLoad playerLoad) {
        if (playerLoad.getType() != null) {
            if (playerLoad.getType().equals("song")) {
                playerLoad.setName("");
                playerLoad.setAlbum("");
                playerLoad.setOwner("");
                playerLoad.setPaused(true);
            }
        }
    }

    /**
     * for the search function we try to
     * we implement a search, and share
     * in cases we use here
     * and the criteria classes on which
     * we built them, and we are making a function
     * selected upon which we add the elements
     * depending on what we have and will give them
     * we use later to select
     * Search is set to true, at status
     * operations we set it to false.
     * The element that was loaded is
     * empty, because I did a search
     * we are trying to search the json according to the filter
     * have selectedSongs for songs
     * which is a List<Song>
     * and selected which is a List<String>
     * for others
     *
     * @return json
     * @throws IOException exception for objectMAPPER
     */
    public JsonNode searchJsonByFilters() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        String filterString = objectMapper.writeValueAsString(jsonElement.getFilters());
        List<String> selected = new ArrayList<>();
        List<Song> selectedSong;
        List<Album> selectedAlbum;

        ArrayNode outputs = objectMapper.createArrayNode();
        user.getUserDatabase().setType(jsonElement.getType());
        if (user.getOnlineStatus().equals("online")) {
            switch (jsonElement.getType()) {
                case "song" -> {
                    SongFilters criteriaSong = objectMapper.
                            readValue(filterString, SongFilters.class);
                    selectedSong = criteriaSong.selectSongs(criteriaSong);
                    if (!user.getUserDatabase().getSelectedSong().isEmpty()) {
                        user.getUserDatabase().
                                addPreviousSelectedSong(user.getUserDatabase().getSelectedSong());
                    }
                    user.getUserDatabase().getSelectedSong().clear();
                    user.getUserDatabase().addSelectedSong(selectedSong);
                    user.getUserDatabase().setSearchType("song");
                    jsonObject.put("message",
                            "Search returned " + selectedSong.size() + " results");

                    for (Song elements : selectedSong) {
                        outputs.add(elements.getName());
                    }
                    jsonObject.set("results", outputs);

                    if (user.getUserDatabase().getLoadStatus() == null) {
                        user.getUserDatabase().setLoadStatus(false);
                    }
                    if (user.getUserDatabase().getStatus() != null) {
                        playerLoad playerLoad = user.getUserDatabase().getStatus();
                        conditionPlayerLoadNull(playerLoad);
                    }
                }
                case "podcast" -> {
                    Filters criteria = objectMapper.
                            readValue(filterString, Filters.class);
                    selected = criteria.select(criteria);
                    user.getUserDatabase().setSearchType("podcast");

                }
                case "playlist" -> {
                    PlaylistFilters
                            criteria = objectMapper.
                            readValue(filterString, PlaylistFilters.class);
                    selected = criteria.selectPlaylist(criteria, Library.getDatabase().
                            getJson().getUsername());
                    user.getUserDatabase().setSearchType("playlist");

                }
                case "artist" -> {
                    ArtistFilter criteria = objectMapper.
                            readValue(filterString, ArtistFilter.class);
                    selected = criteria.selectArtist(criteria);
                    user.getUserDatabase().setSearchType("artist");

                }
                case "host" -> {
                    ArtistFilter criteria = objectMapper.
                            readValue(filterString, ArtistFilter.class);
                    selected = criteria.selectHost(criteria);
                    user.getUserDatabase().setSearchType("host");

                }
                case "album" -> {
                    AlbumFilter criteriaAlbum = objectMapper.
                            readValue(filterString, AlbumFilter.class);
                    selectedAlbum = criteriaAlbum.select(criteriaAlbum);
                    if (!user.getUserDatabase().getSelectedAlbumSinger().isEmpty()) {
                        user.getUserDatabase().addPreviousSelectedAlbum(user.
                                getUserDatabase().getSelectedAlbumSinger());
                    }
                    user.getUserDatabase().getSelectedAlbumSinger().clear();
                    user.getUserDatabase().addSelectedAlbum(selectedAlbum);
                    user.getUserDatabase().setSearchType("album");
                    jsonObject.put("message",
                            "Search returned " + selectedAlbum.size() + " results");
                    for (Album elements : selectedAlbum) {
                        outputs.add(elements.getName());
                    }
                    jsonObject.set("results", outputs);

                    if (user.getUserDatabase().getLoadStatus() == null) {
                        user.getUserDatabase().setLoadStatus(false);
                    }
                    if (user.getUserDatabase().getStatus() != null) {
                        playerLoad playerLoad = user.getUserDatabase().getStatus();
                        conditionPlayerLoadNull(playerLoad);
                    }
                }
                default -> {
                    break;
                }
            }
            if (!jsonElement.getType().equals("song")
                    && !jsonElement.getType().equals("album")) {
                if (!user.getUserDatabase().getSelected().isEmpty()) {
                    user.getUserDatabase().
                            addPreviousSelected(user.getUserDatabase().getSelected());
                }
                user.getUserDatabase().addSelected(selected);
                jsonObject.put("message",
                        "Search returned " + selected.size() + " results");

                for (String elements : selected) {
                    outputs.add(elements);
                }
                jsonObject.set("results", outputs);
                if (user.getUserDatabase().getLoadStatus() == null) {
                    user.getUserDatabase().setLoadStatus(false);
                }
            }

            user.getUserDatabase().setAccesedUser("", user);
            user.getUserDatabase().setSearchstatus(true);
            user.getUserDatabase().setShufflestatus(false);
            user.getUserDatabase().setLoadedElement("");
            user.getUserDatabase().setSelectstatus(false);
            user.getUserDatabase().setLikestatus(false);

        } else {
            jsonObject.put("message",
                    jsonElement.getUsername() + " is offline.");
            jsonObject.put("results", outputs);
        }
        return jsonObject;
    }
    private ObjectNode selectIdHigh(final User user,
             final List<String> selected, final ObjectNode jsonObject) {
        user.getUserDatabase().setSelectstatus(false);
        if (selected.isEmpty()) {
            user.getUserDatabase().
                    addSelected(user.getUserDatabase().getPreviousSelected());
            user.getUserDatabase().addPreviousSelected(new ArrayList<>());
        }
        jsonObject.put("message", "The selected ID is too high.");
        return jsonObject;
    }
    private ObjectNode selectIdHighSong(final User user,
            final List<Song> selected, final ObjectNode jsonObject) {
        user.getUserDatabase().setSelectstatus(false);
        if (selected.isEmpty()) {
            user.getUserDatabase().addSelectedSong(user.
                    getUserDatabase().getPreviousSelectedSong());
            user.getUserDatabase().addPreviousSelectedSong(new ArrayList<>());
        }
        jsonObject.put("message", "The selected ID is too high.");
        return jsonObject;
    }
    private ObjectNode selectIdHighAlbum(final User user,
               final List<Album> selected, final ObjectNode jsonObject) {
        user.getUserDatabase().setSelectstatus(false);
        if (selected.isEmpty()) {
            user.getUserDatabase().addSelectedAlbum(user.
                    getUserDatabase().getPreviousSelectedAlbumSinger());
            user.getUserDatabase().addPreviousSelectedAlbum(new ArrayList<>());
        }
        jsonObject.put("message", "The selected ID is too high.");
        return jsonObject;
    }
    /**
     * we implement for select and select the element
     * as an index function, and we try to search for it in
     * the elements found in the searchclass
     * search status is set to false as it was
     * made the selection for search, and when selecting true
     * we have a different select for songs
     * because for songs we need the album
     * in order to search the song later
     * we try to search the json by filters
     * have selected which is a List<String>
     * and selectedSong which is a List<Song>
     * for songs as we need the album
     * @return json
     */
    public JsonNode selectJsonByFilters() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();

        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        List<Song> selectedSong = new ArrayList<>();
        List<Album> selectedAlbum = new ArrayList<>();
        List<String> selected = new ArrayList<>();

        if (user.getUserDatabase().getSearchType().equals("song")) {
            selectedSong = new ArrayList<>(user.getUserDatabase().getSelectedSong());

        } else if (user.getUserDatabase().getSearchType().equals("album")) {
            selectedAlbum = new ArrayList<>(user.getUserDatabase().getSelectedAlbumSinger());

        } else {
            selected = new ArrayList<>(user.getUserDatabase().getSelected());

        }
        if (user.getUserDatabase().getSearchstatus() == null
        || !user.getUserDatabase().getSearchstatus()) {
            jsonObject.put("message",
                    "Please conduct a search before making a selection.");
            return jsonObject;
        }

        boolean state = !user.getUserDatabase().getSearchType().equals("song")
                && !user.getUserDatabase().getSearchType().equals("album");
        if (jsonElement.getItemNumber() > selected.size()
                && state) {
            return selectIdHigh(user, selected, jsonObject);

        } else if (jsonElement.getItemNumber() > selectedSong.size()
                && user.getUserDatabase().getSearchType().equals("song")) {
            return selectIdHighSong(user, selectedSong, jsonObject);
        } else if (jsonElement.getItemNumber() > selectedAlbum.size()
                && user.getUserDatabase().getSearchType().equals("album")) {
            return selectIdHighAlbum(user, selectedAlbum, jsonObject);
        } else {
            user.getUserDatabase().setSelectstatus(true);
            if (user.getUserDatabase().getSearchType().equals("song")) {
                jsonObject.put("message", "Successfully selected "
                        + selectedSong.get(jsonElement.getItemNumber() - 1).getName() + ".");
                user.getUserDatabase().setElement(selectedSong.
                        get(jsonElement.getItemNumber() - 1).getName());
                user.getUserDatabase().setSelectElementAlbum(selectedSong.
                        get(jsonElement.getItemNumber() - 1).getAlbum());

                user.getUserDatabase().setSelectElementGenre(selectedSong.
                        get(jsonElement.getItemNumber() - 1).getGenre());

                user.getUserDatabase().setAccesedUser(selectedSong.get(jsonElement.
                        getItemNumber() - 1).getArtist(), user);
                user.getUserDatabase().setSearchstatus(false);

                user.getUserDatabase().setLoadStatus(false);
                user.getUserDatabase().setSelectstatus(true);
                user.getUserDatabase().setLikestatus(false);
            } else if (user.getUserDatabase().getSearchType().equals("album")) {
                jsonObject.put("message", "Successfully selected "
                        + selectedAlbum.get(jsonElement.getItemNumber() - 1).getName() + ".");
                user.getUserDatabase().setSelectAlbum2(selectedAlbum.
                        get(jsonElement.getItemNumber() - 1).getName());
                user.getUserDatabase().setSelectElementOwner(selectedAlbum.
                        get(jsonElement.getItemNumber() - 1).getArtist());

                user.getUserDatabase().setAccesedUser(selectedAlbum.get(jsonElement.
                        getItemNumber() - 1).getArtist(), user);
                user.getUserDatabase().setSearchstatus(false);

                user.getUserDatabase().setLoadStatus(false);
                user.getUserDatabase().setSelectstatus(true);
                user.getUserDatabase().setLikestatus(false);
            } else {
                if (user.getUserDatabase().getSearchType().equals("artist")) {
                    Library.getDatabase().setArtistElement(selected.
                            get(jsonElement.getItemNumber() - 1));

                } else if (user.getUserDatabase().getSearchType().equals("host")) {
                    Library.getDatabase().setHostElement(selected.
                            get(jsonElement.getItemNumber() - 1));
                }
                jsonObject.put("message",
                        "Successfully selected " + selected.
                                get(jsonElement.getItemNumber() - 1) + ".");
                String stringuser = selected.get(jsonElement.getItemNumber() - 1);
                user.getUserDatabase().setElement(selected.
                        get(jsonElement.getItemNumber() - 1));

                Podcast podcast = Library.podcastByName(stringuser);
                if (podcast != null) {
                    if (Library.getDatabase().checkIfHostValid(podcast.getOwner())) {
                        user.getUserDatabase().setAccesedUser(podcast.getOwner(), user);
                    }
                }
                user.getUserDatabase().setSearchstatus(false);

                user.getUserDatabase().setLoadStatus(false);
                user.getUserDatabase().setSelectstatus(true);
                user.getUserDatabase().setLikestatus(false);
                boolean state2 = user.getUserDatabase().getSearchType().equals("album");
                if (Library.getDatabase().checkIfAlbumExists(user.
                        getUserDatabase().getElement()) && state2) {
                    Album album = Library.searchAlbumByName(user.
                            getUserDatabase().getElement());
                    user.getUserDatabase().setAccesedUser(album.getArtist(), user);
                    user.getUserDatabase().setSelectstatus(true);

                    jsonObject.put("message", "Successfully selected "
                            + user.getUserDatabase().getElement() + ".");
                    return jsonObject;
                } else if (Library.getDatabase().checkIfArtistValid(user.
                        getUserDatabase().getElement())) {
                    user.getUserDatabase().setAccesedUser(user.getUserDatabase().
                            getElement(), user);
                    user.getUserDatabase().setPageStatus("ARTIST");
                    user.getUserDatabase().getPageStatusStack().push("ARTIST");
                    jsonObject.put("message", "Successfully selected "
                            + user.getUserDatabase().getElement() + "'s page.");
                    return jsonObject;
                } else if (Library.getDatabase().checkIfHostValid(user.
                        getUserDatabase().getElement())) {
                    user.getUserDatabase().setAccesedUser(user.getUserDatabase().
                            getElement(), user);
                    user.getUserDatabase().setPageStatus("HOST");
                    user.getUserDatabase().getPageStatusStack().push("HOST");


                    jsonObject.put("message", "Successfully selected "
                            + user.getUserDatabase().getElement() + "'s page.");
                    return jsonObject;
                }
            }
        }
        user.getUserDatabase().setSearchstatus(false);
        user.getUserDatabase().setLoadStatus(false);
        user.getUserDatabase().setSelectstatus(true);
        user.getUserDatabase().setLikestatus(false);

        return jsonObject;
    }
}


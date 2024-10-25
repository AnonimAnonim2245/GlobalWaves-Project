package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Library is our mother class on which we have all the operations that
 * we access in common
 * for all users, that's also why we take input for songs, podcasts,
 * playlists(PlaylistList)
 * that's why we take the json values. Here we also convert users to
 * hasmap to make it easier
 * this only once in our function
 * of the user's name (we make a singleton per user).
 * We have a final function that converts input from the test into classes to use
 * to filter information and for
 * to process the data.
 * We have the instance function, in which we make a lazy instance of
 * the singleton so that we can
 * take the data only once, we do with singleton
 * so that we can access the information previously added by me.
 * We have the addPlaylistList function in which we add playlists,
 * and removeAllPlaylists in
 * which we delete respectively
 * I have a function that searches for the song by name, this somehow makes
 * it easier when we need it
 * enter the name
 * songs , and we want to modify or read information
 * Search Song by name returns the name of the song index, which we use
 * to process the information in it.
 * GetUserInstance checks if we did the instantiation once again, and not again
 * makes and returns the usermap
 * which represents the hashmap of our user. And if he checks that he was not notified
 * and does not contain it, we make one.
 * we implemented for stage 2 also for artists, hosts, which are the same as users
 * a hashmap, which verifies if the certain name was
 * instantiated oncewe implemented design pattern flyweight,
 * and we also implemented functions of
 * checking if the artist, the host, the user exists, and functions to
 * delete them all
 */
public final class Library {
    private static final Double HUNDRED = 100.00;
    @Getter
    @Setter
    private Integer totale = 0;
    @Getter
    private JsonConvertToCategories json = new JsonConvertToCategories();
    private Map<String, User> userMap = new HashMap<>();

    private Map<String, Artist> artistMap = new HashMap<>();
    private Map<String, Host> hostMap = new HashMap<>();
    @Getter
    private final List<JsonConvertToCategories> jsonList = new ArrayList<>();
    @Getter
    private ArrayList<Song> songs;
    @Getter
    private List<Playlist> playlistList = new ArrayList<>();
    @Getter
    private List<String> accesedUsers = new ArrayList<>();
    @Getter
    private List<Album> albums = new ArrayList<>();
    @Getter
    private ArrayList<Podcast> podcasts;
    @Getter
    @Setter
    private String hostElement = "";
    @Getter
    @Setter
    private String artistElement = "";
    @Getter
    @Setter
    private Map<String, Double> songArtistMap = new HashMap<>();
    @Getter
    @Setter
    private Boolean barrier = false;
    @Getter
    @Setter
    private String pageStatus = "HOME";

    @Getter
    private ArrayList<User> users;
    @Getter
    private ArrayList<Host> hosts = new ArrayList<>();
    @Getter
    private ArrayList<Artist> artists = new ArrayList<>();


    private static Library instance = null;
    private static int count;

    /**
     * add an album
     *
     * @param album the album
     */
    public void addAlbum(final Album album) {
        albums.add(album);
    }

    /**
     * add a user
     *
     * @param user the user
     */
    public void addUser(final User user) {
        Library.getDatabase().getUsers().add(user);
    }

    /**
     * add a host
     *
     * @param host
     */
    public void addHost(final Host host) {
        hosts.add(host);
    }

    /**
     * add an artist
     *
     * @param artist
     */
    public void addArtist(final Artist artist) {
        artists.add(artist);
    }


    /**
     * singleton process for getDatabase()
     *
     * @return instance
     * implements lazy singleton design pattern
     */
    public static Library getDatabase() {
        if (instance == null) {
            instance = new Library();
        }
        count++;
        return instance;

    }

    /**
     * add a podcast
     *
     * @param podcast the current podcast
     */
    public void addPodcast(final Podcast podcast) {
        podcasts.add(podcast);
    }

    /**
     * removes an element from a playlist
     */
    public void addSongs(final ArrayList<Song> songsCopy) {
        this.songs.addAll(songsCopy);
    }

    /**
     * removes all the playlists
     */
    public void removeAllPlaylists() {
        this.playlistList.removeAll(this.getPlaylistList());
    }

    /**
     * set the json
     */
    public void setJson(final JsonConvertToCategories json) {
        this.json = json;
    }


    /**
     * check if artist was instantiated
     *
     * @param username username artist
     * @return true or false
     */
    public boolean checkifArtistInstance(final String username) {
        return (artistMap.containsKey(username));

    }

    /**
     * check if user was instantiated
     *
     * @param username username normal user
     * @return true or false
     */

    public boolean checkifUserInstance(final String username) {
        return (userMap.containsKey(username));

    }

    /**
     * added artist instance which verifies the key only once
     *
     * @param username username
     * @return the hashmap with username
     * implements the flyweight design pattern
     */
    public Artist getArtistInstance(final String username) {
        if (!artistMap.containsKey(username)) {
            artistMap.put(username, new Artist(username));
        }
        return artistMap.get(username);
    }

    /**
     * added host instance which verifies the key only once
     *
     * @param username username
     * @return the hashmap with username
     * implements the flyweight design pattern
     */
    public Host getHostInstance(final String username) {
        if (!hostMap.containsKey(username)) {
            hostMap.put(username, new Host(username));
        }
        return hostMap.get(username);
    }

    /**
     * checks if the user is valid
     *
     * @param username the username
     * @return true or false
     */
    public boolean checkIfNormalUserValid(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * removes all the playlists
     */

    public void removeAllArtists() {
        this.artists.clear();
    }

    /**
     * removes all the albums
     */
    public void removeAllAlbums() {
        this.albums.clear();
    }

    /**
     * removes all the hosts and all their announcements
     */
    public void removeAllHosts() {
        for (Host host : hosts) {
            host.getAnnouncements().clear();

        }
        Library.getDatabase().getHosts().clear();
    }

    /**
     * checks if the artist is valid and
     * is on the artists list
     *
     * @param username the current artist
     * @return
     */
    public boolean checkIfArtistValid(final String username) {
        for (Artist artist : artists) {
            if (artist.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * return a certain artist by name
     *
     * @param username the name to be searched
     *                 in the artist list
     * @return artist class or null,
     * if wasn't found
     */
    public Artist returnArtist(final String username) {
        for (Artist artist : artists) {
            if (artist.getUsername().equals(username)) {
                return artist;
            }
        }
        return null;
    }

    /**
     * checks if the album exists
     *
     * @param name the name of the album
     *             we check if it exists
     * @return true or false
     */
    public boolean checkIfAlbumExists(final String name) {

        for (Artist artist : Library.getDatabase().getArtists()) {
            for (Album album : Library.getDatabase().
                    getArtistInstance(artist.getUsername()).getAlbums()) {
                if (album.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * checks if the album of a certain artist exists
     *
     * @param name the name of the album
     * @param user the name of the artist
     * @return true or false
     */
    public boolean checkIfAlbumUserExists(final String name,
                                          final String user) {

        for (Artist artist : Library.getDatabase().getArtists()) {
            if (artist.getUsername().equals(user)) {
                for (Album album : Library.getDatabase().
                        getArtistInstance(artist.getUsername()).getAlbums()) {
                    if (album.getName().equals(name)) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    /**
     * checks if the podcast exists
     *
     * @param name the name of podcast
     *             we search
     * @return true or false
     */
    public boolean checkIfPodcastExists(final String name) {

        for (Podcast podcast : Library.getDatabase().getPodcasts()) {
            if (podcast.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * checks if the host exists or valid
     *
     * @param username the name of our
     *                 supposed host
     * @return true or false
     */
    public boolean checkIfHostValid(final String username) {
        for (Host host : hosts) {
            if (host.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }


    /**
     * converts Lists to Map with a string paramet
     */
    public void convertListToMap() {
        Map<String, User> stringUserHashMap = new HashMap<>();
        for (User user : users) {
            stringUserHashMap.put(user.getUsername(), user);
        }
        this.userMap = stringUserHashMap;
    }

    /**
     * adds playlist
     */
    public void addPlaylistList(final Playlist playlistList2) {
        if (this.playlistList.isEmpty()) {
            List<Playlist> list = new ArrayList<>();
            list.add(playlistList2);
            this.playlistList = list;
            return;
        }

        this.playlistList.add(playlistList2);
    }

    /**
     * added jsonlist
     */
    public void addJsonList(final List<JsonConvertToCategories> jsonList2) {
        if (this.jsonList.equals(jsonList2)) {
            return;
        }
        this.jsonList.removeAll(this.getJsonList());
        this.jsonList.addAll(jsonList2);

    }

    /**
     * added user instance which verifies the key only once
     *
     * @param username username
     * @return the hashmap with username
     * implements the flyweight design pattern
     */

    public User getUserInstance(final String username) {
        if (!userMap.containsKey(username)) {
            userMap.put(username, new User(username));
        }
        return userMap.get(username);
    }


    /**
     * gets podcast by name
     *
     * @param name name of the podcast
     * @return podcast with that name
     */
    public static Podcast podcastByName(final String name) {
        List<Podcast> podcasts = Library.getDatabase().getPodcasts();
        for (Podcast podcast : podcasts) {
            if (podcast.getName().startsWith(name)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * searches for an album by its name
     *
     * @param name
     * @return
     */
    public static Album searchAlbumByName(final String name) {
        int i = 0;
        for (Album album : Library.getDatabase().getAlbums()) {
            if (album.getName().equals(name)) {
                return album;
            }
            i++;
        }
        return null;
    }

    /**
     * deteletes all the traces of a certain user
     * such as songs, preffered songs(by others), if they are
     * preffered, album
     *
     * @param artist the artist we want to its
     *               songs
     */
    public static void deleteAllUserTrace(final Artist artist) {
        for (Album album : artist.getAlbums()) {
            for (Song songAlbum : album.getSongs()) {
                Library.getDatabase().getSongs().removeIf(song ->
                        song.getName().equals(songAlbum.getName()));
                for (User user : Library.getDatabase().getUsers()) {
                    user.getPrefferedSongs().removeIf(song2 ->
                            song2.equals(songAlbum));
                }
            }

            Library.getDatabase().getAlbums().removeIf(album1 ->
                    album1.getName().equals(album.getName()));
        }
    }

    /**
     * searches song by name
     *
     * @param name the name of song
     * @return returns the index of the song
     */
    public static int searchSongByName(final String name) {
        int i = 0;
        for (Song song : Library.getDatabase().getSongs()) {
            if (song.getName().equals(name)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * searches song by name and album
     *
     * @param songName  the name of song
     * @param albumName the name of album
     * @return returns the index of the song
     */
    public static int searchSongByNameAndAlbum(final String songName, final String albumName) {
        int i = 0;
        for (Song song : Library.getDatabase().getSongs()) {
            if (song.getName().equals(songName) && songName.equals("Ad Break")) {

                return i;
            }
            if (song.getName().equals(songName) && song.getAlbum().equals(albumName)) {

                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * our library function
     */
    public Library() {
    }

    /**
     * set Song function
     *
     * @param songs the list of songs
     */
    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    /**
     * set Podcast function
     *
     * @param podcasts the list of podcast
     */
    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    /**
     * SET USERS
     *
     * @param users the list of users
     */
    public void setUsers(final ArrayList<User> users) {
        this.users = users;
    }

    /**
     * Add user function which handles the case that the
     *
     * @return json
     */
    public static JsonNode addUserJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        switch (jsonElement.getType()) {
            case "artist" -> {
                if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {
                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " is already taken.");
                    return jsonObject;
                } else if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())
                        || Library.getDatabase().checkIfNormalUserValid(jsonElement.
                        getUsername())) {
                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " is already taken.");
                    return jsonObject;
                } else {

                    for (int i = 0; i < Library.getDatabase().getArtistInstance(jsonElement.
                            getUsername()).getAlbums().size(); i++) {
                        Library.getDatabase().getArtistInstance(jsonElement.getUsername()).
                                getAlbums().get(i).getSongs().clear();
                    }
                    Library.getDatabase().getArtistInstance(jsonElement.getUsername()).
                            getAlbums().clear();
                    Library.getDatabase().addArtist(Library.
                            getDatabase().getArtistInstance(jsonElement.getUsername()));

                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " has been added successfully.");

                }
            }
            case "user" -> {
                if (Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " is already taken.");
                    return jsonObject;
                } else if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())
                        || Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {
                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " is already taken.");
                    return jsonObject;
                } else {
                    Library.getDatabase().addUser(Library.
                            getDatabase().getUserInstance(jsonElement.getUsername()));
                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " has been added successfully.");

                }
            }
            case "host" -> {
                if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {
                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " is already taken.");
                    return jsonObject;
                } else if (Library.getDatabase().checkIfNormalUserValid(jsonElement.
                        getUsername())
                        || Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {
                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " is already taken.");
                    return jsonObject;
                } else {

                    Library.getDatabase().addHost(Library.
                            getDatabase().getHostInstance(jsonElement.getUsername()));
                    jsonObject.put("message", "The username "
                            + jsonElement.getUsername() + " has been added successfully.");

                }
            }
            default -> {
                break;
            }
        }

        return jsonObject;


    }

    /**
     * stats about the User, Artist or Hosts about the listeners
     *
     * @return jsonnode
     */
    public static JsonNode wrappedJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (Library.getDatabase().
                checkIfNormalUserValid(jsonElement.getUsername())) {
            User user = Library.getDatabase().
                    getUserInstance(jsonElement.getUsername());
            boolean value1 = !user.getTopArtistsListened().isEmpty();
            boolean value2 = !user.getTopGenreListened().isEmpty();
            boolean value3 = !user.getTopAlbumsListened().isEmpty();
            boolean value4 = !user.getTopSongsListened().isEmpty();
            boolean value5 = !user.getTopEpisodesListened().isEmpty();
            if (value1 || value2 || value3 || value4 || value5) {
                ObjectNode middleJsonObject = objectMapper.createObjectNode();
                middleJsonObject.put("topArtists", user.getTop5ArtistsListened());
                middleJsonObject.put("topGenres", user.getTopGenreListened());
                middleJsonObject.put("topAlbums", user.getTopAlbumsListened());
                middleJsonObject.put("topSongs", user.getTopSongsListened());
                middleJsonObject.put("topEpisodes", user.getTopEpisodesListened());
                jsonObject.put("result", middleJsonObject);
            } else {
                jsonObject.put("message", "No data to show for user "
                        + jsonElement.getUsername() + ".");
            }
        } else if (Library.getDatabase().
                checkIfArtistValid(jsonElement.getUsername())) {
            Artist artist = Library.getDatabase().
                    getArtistInstance(jsonElement.getUsername());
            boolean value1 = !artist.getTopAlbumsListened().isEmpty();
            boolean value2 = !artist.getTopSongsListened().isEmpty();
            boolean value3 = !artist.getTopFansListened().isEmpty();
            boolean value4 = artist.getListeners() != 0;
            if (value1 || value2 || value3 || value4) {
                ObjectNode middleJsonObject = objectMapper.createObjectNode();

                middleJsonObject.put("topAlbums", artist.getTopAlbumsListened());
                middleJsonObject.put("topSongs", artist.getTopSongsListened());
                middleJsonObject.put("topFans", artist.getTopFansListened());
                middleJsonObject.put("listeners", artist.getListeners());
                jsonObject.put("result", middleJsonObject);
            } else {
                jsonObject.put("message", "No data to show for artist "
                        + jsonElement.getUsername() + ".");
            }

        } else if (Library.getDatabase().
                checkIfHostValid(jsonElement.getUsername())) {
            Host host = Library.getDatabase().
                    getHostInstance(jsonElement.getUsername());
            boolean value1 = !host.getTopEpisodesListened().isEmpty();
            boolean value2 = host.getListeners() != 0;
            if (value1 || value2) {
                ObjectNode middleJsonObject = objectMapper.createObjectNode();

                middleJsonObject.put("topEpisodes", host.getTopEpisodesListened());
                middleJsonObject.put("listeners", host.getListeners());
                jsonObject.put("result", middleJsonObject);
            } else {
                jsonObject.put("message", "No data to show for host "
                        + jsonElement.getUsername() + ".");
            }
        }
        return jsonObject;
    }

    /**
     * the json for endprogram
     * @return endprogram
     */
    public static JsonNode endProgramJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", "endProgram");
        jsonObject.put("result", putsmallJsonObject());
        return jsonObject;
    }

    /**
     * tops of the artists listened by the user
     */
    public static List<Artist> getTopArtistsListened() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> artistListens = new HashMap<>();
        Artist artist1 = new Artist();
        for (Artist artist : Library.getDatabase().getArtists()) {
            for (User user : Library.getDatabase().getUsers()) {
                if (Library.getDatabase().checkIfNormalUserValid(user.getUsername())) {
                    for (Song song : user.getListenedSongs()) {
                        if (song.getArtist().equals(artist.getUsername())) {
                            if (artistListens.containsKey(artist.getUsername())) {
                                int count3 = artistListens.get(artist.getUsername());
                                int countSong = user.getSongListenCount(song);
                                int countElement = countSong;
                                artist1 = artist;
                                artistListens.put(artist.getUsername(), count3 + countElement);
                            } else {
                                int countSong = user.getSongListenCount(song);
                                int countElement = countSong;
                                artistListens.put(artist.getUsername(), countElement);
                            }
                        }
                    }
                }

            }

            if (artist.getMerchRevenue() != 0.0) {
                int count2 = 0;
                if (artistListens.containsKey(artist.getUsername())) {
                    count2 = artistListens.get(artist.getUsername());
                } else {
                    count2 = 0;
                }
                artistListens.put(artist.getUsername(), count2);
            }
        }

        List<Artist> topArtists = new ArrayList<>();
        List<String> topArtistString = new ArrayList<>();
        int z = -1;
        int count2 = artistListens.entrySet().size();
        for (int i = 0; i < count2; i++) {
            double max = -1.0;
            String maxArtistString = "";
            int k = 0;
            for (Map.Entry<String, Integer> entry : artistListens.entrySet()) {
                int result = entry.getKey().compareTo(maxArtistString);
                Artist artist = Library.getDatabase().getArtistInstance(maxArtistString);
                boolean contains = false;

                double sum = artist.getMerchRevenue() + artist.getSongRevenue2();
                if ((sum > max || (sum == max && result < 0))
                        && !topArtistString.contains(entry.getKey())) {
                    max = sum;
                    maxArtistString = entry.getKey();
                }
            }

            Artist artist = Library.getDatabase().getArtistInstance(maxArtistString);
            topArtists.add(artist);
            topArtistString.add(maxArtistString);
        }
        return topArtists;


    }

    /**
     * STATS ABOUT ARTIST
     * FOR THE END PROGRAM
     * @return
     */
    public static JsonNode putsmallJsonObject() {
        ObjectMapper objectMapper2 = new ObjectMapper();
        ObjectNode jsonObject = objectMapper2.createObjectNode();
        ArrayNode bigJsonObject = objectMapper2.createArrayNode();
        int i = 1;
        double total2 = 0;

        HashMap<String, Double> songListens = new HashMap<>();
        int maxi = -1;
        for (Song song : Library.getDatabase().getSongs()) {
            Artist artist = Library.getDatabase().getArtistInstance(song.getArtist());
            if (songListens.containsKey(song.getName() + "#" + artist.getUsername())) {
                double revenue = songListens.get(song.getName() + "#" + artist.getUsername());
                songListens.put(song.getName() + "#" + artist.getUsername(),
                        revenue + song.getSongRevenue());
            } else {
                songListens.put(song.getName() + "#" + artist.getUsername(), song.getSongRevenue());
            }
            Double revenue = songListens.get(song.getName() + "#" + artist.getUsername());
            String currentString = song.getName() + "#" + artist.getUsername();
            Double totalRounded1 = Math.round(artist.getMaxi()
                    * HUNDRED) / HUNDRED;
            Double totalRounded2 = Math.round(songListens.get(currentString)
                    * HUNDRED) / HUNDRED;

            boolean value = (totalRounded2.equals(totalRounded1)
                    && artist.getMostProfitableSong2().compareTo(song.getName()) > 0);
            if (revenue > artist.getMaxi() || value) {
                artist.setMaxi(revenue);
                artist.setMostProfitableSong2(song.getName());
            }
        }
        for (Artist artist : Library.getDatabase().getArtists()) {
            artist.setMaxi(-1.0);
        }

        for (Artist artist : getTopArtistsListened()) {

            double total = 0;
            double max = 0;
            int maxIndex = -1;
            int index = 0;
            String maxSong = "";


            for (String stringsong : artist.getTopAllSongsListened()) {

                Song song = Library.getDatabase().getSongs().get(searchSongByName(stringsong));

                if (song.getArtist().equals(artist.getUsername())) {
                    total += song.getSongRevenue();
                    boolean value = (max == song.getSongRevenue()
                            && maxSong.compareTo(song.getName()) > 0);
                    if (max < song.getSongRevenue() || value) {
                        max = song.getSongRevenue();
                        maxSong = song.getName();

                        maxIndex = index;
                    }
                }
                index += 1;
            }

            for (Album album : artist.getAlbums()) {
                for (Song song : album.getSongs()) {
                    if (song.getArtist().equals(artist.getUsername())) {
                        total2 += song.getSongRevenue();
                    }
                }
            }
            Double totalRounded = Math.round(total * HUNDRED) / HUNDRED;
            if (maxIndex != -1) {
                artist.setMostProfitableSong(maxSong);
            }


        }
        List<Artist> artistsList = getTopArtistsListened();
        artistsList.sort(Artist.getVariableComparator());
        for (Artist artist : artistsList) {
            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode smallJsonObject = objectMapper.createObjectNode();

            smallJsonObject.put("merchRevenue", artist.getMerchRevenue());
            ArrayNode smallJsonObject2 = objectMapper.createArrayNode();
            double total = 0;
            for (Song song : Library.getDatabase().getSongs()) {
                if (song.getArtist().equals(artist.getUsername())) {
                    smallJsonObject2.add(song.getName() + " " + song.getSongRevenue());
                    total += song.getSongRevenue();

                }
            }
            double artistrevenue = Math.round(artist.getSongRevenue2()
                    * HUNDRED) / HUNDRED;
            smallJsonObject.put("songRevenue", artistrevenue);
            smallJsonObject.put("ranking", i);
            artist.setSongRevenue2(0.0);
            if (artist.getMostProfitableSong().isEmpty()) {
                smallJsonObject.put("mostProfitableSong", "N/A");
            } else {
                String maxSong = artist.getMostProfitableSong();
                smallJsonObject.put("mostProfitableSong", artist.getMostProfitableSong2());
            }
            jsonObject.put(artist.getUsername(), smallJsonObject);

            bigJsonObject.add(smallJsonObject);
            i += 1;
        }

        return jsonObject;
    }

    private static JsonNode checkIfNormalUserForDeleteOk(final JsonConvertToCategories
                                                                 jsonElement) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        if (!Library.getDatabase().checkifUserInstance(jsonElement.getUsername())
                && !Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {

            if (!Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " is not an artist.");
                return jsonObject;
            }
        }

        boolean status = false;
        for (User user : Library.getDatabase().getUsers()) {

            if (user.getUserDatabase().getAccesedUser() == null) {
                continue;
            }
            if (user.getUserDatabase().getAccesedUser().
                    equals(jsonElement.getUsername())) {
                status = true;
                break;

            }
        }
        if (status) {
            jsonObject.put("message",
                    jsonElement.getUsername() + " can't be deleted.");
            return jsonObject;
        }
        if (Library.getDatabase().checkifArtistInstance(jsonElement.getUsername())) {

            Artist artist4 = Library.getDatabase().
                    returnArtist(jsonElement.getUsername());
            if (artist4 != null) {

                for (Playlist playlist : Library.getDatabase().getPlaylistList()) {

                    for (Album album : Library.getDatabase().
                            returnArtist(jsonElement.getUsername()).getAlbums()) {

                        for (Song song : album.getSongs()) {

                            if (playlist.getSongList().contains(song.getName())) {

                                jsonObject.put("message",
                                        jsonElement.getUsername() + " can't be deleted.");
                                return jsonObject;
                            }
                        }

                    }
                }
            }
        }
        if (Library.getDatabase().checkifUserInstance(jsonElement.getUsername())) {
            User user = Library.getDatabase().
                    getUserInstance(jsonElement.getUsername());
            if (user != null) {
                for (User user3 : Library.getDatabase().getUsers()) {
                    if (user3.getUserDatabase().getLoadedPlaylist() != null) {
                        String loadPlaylist = user3.
                                getUserDatabase().getLoadedElement();
                        Playlist playlistLoad = user.
                                getUserDatabase().selectPlaylistByName(loadPlaylist);
                        if (playlistLoad != null) {
                            for (Playlist playlist
                                    : Library.getDatabase().getPlaylistList()) {
                                if (playlistLoad.getName().
                                        equals(playlist.getName()) && playlist.getOwner().
                                        equals(jsonElement.getUsername())) {
                                    jsonObject.put("message",
                                            jsonElement.getUsername() + " can't be deleted.");
                                    return jsonObject;
                                }
                            }
                        }

                    }
                }
            }

        }
        if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {
            Host host = Library.getDatabase().getHostInstance(jsonElement.getUsername());
            if (host != null) {
                for (User user3 : Library.getDatabase().getUsers()) {
                    if (user3.getUserDatabase().getLoadedPodcast() != null) {
                        String loadPodcast = user3.getUserDatabase().getLoadedElement();
                        Podcast podcastLoad = user3.
                                getUserDatabase().selectPodcastByName(loadPodcast);
                        if (podcastLoad != null) {
                            for (Podcast podcast : Library.getDatabase().getPodcasts()) {
                                if (podcastLoad.getName().
                                        equals(podcast.getName()) && podcast.getOwner().
                                        equals(jsonElement.getUsername())) {
                                    jsonObject.put("message",
                                            jsonElement.getUsername() + " can't be deleted.");
                                    return jsonObject;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;

    }

    /**
     * we delete the user from the database
     * , and we check if the user is an artist, host or normal user
     * , and we check if the user is loaded in has a loaded song or podcast
     * ,and we check if it is in a playlist
     * then we delete all the traces of it, albums, songs, podcasts,
     * playlists, likesongs, followings etc.
     * if it isn't we delete the user
     */
    public static JsonNode deleteUserJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        JsonNode jsonObject2 = objectMapper.createObjectNode();
        jsonObject2 = checkIfNormalUserForDeleteOk(jsonElement);

        if (jsonObject2 != null) {
            jsonObject.put("message",
                    jsonElement.getUsername() + " can't be deleted.");
            return jsonObject;
        }

        if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {

            Artist artist = Library.getDatabase().returnArtist(jsonElement.getUsername());
            Library.deleteAllUserTrace(artist);
            Library.getDatabase().getArtists().removeIf(artist2 ->
                    artist2.getUsername().equals(jsonElement.getUsername()));

        } else if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {

            Library.getDatabase().getHosts().removeIf(host ->
                    host.getUsername().equals(jsonElement.getUsername()));
        } else if (Library.getDatabase().checkIfNormalUserValid(jsonElement.
                getUsername())) {
            for (Playlist playlist : Library.getDatabase().getPlaylistList()) {
                if (playlist.getUsersFollow().contains(jsonElement.getUsername())) {
                    playlist.setFollowers(playlist.getFollowers() - 1);
                    playlist.getUsersFollow().removeIf(user2
                            -> user2.equals(jsonElement.getUsername()));
                }
            }
            Iterator<Playlist> iterator = Library.getDatabase().
                    getPlaylistList().iterator();
            while (iterator.hasNext()) {
                Playlist playlist2 = iterator.next();
                if (playlist2.getOwner().equals(jsonElement.getUsername())) {
                    iterator.remove();
                }
            }
            Library.getDatabase().getUsers().removeIf(user ->
                    user.getUsername().equals(jsonElement.getUsername()));
        }
        jsonObject.put("message", jsonElement.getUsername()
                + " was successfully deleted.");

        return jsonObject;
    }

    /**
     * returns the online status of a normal user
     * user can be online or offline
     *
     * @return json
     */
    public static JsonNode onlineJson() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
            User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
            user.setOnlineStatus();
            jsonObject.put("message",
                    jsonElement.getUsername() + " has changed status successfully.");
        } else {
            if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {
                jsonObject.put("message", jsonElement.getUsername() + " is not a normal user.");
            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
            }
        }


        return jsonObject;
    }

    /**
     * shows all users, artists, hosts
     *
     * @return json
     */
    public static JsonNode showAllUsers() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        boolean first = true;
        ArrayNode outputs = objectMapper.createArrayNode();

        for (User user : Library.getDatabase().getUsers()) {
            outputs.add(user.getUsername());
        }
        for (Artist artist : Library.getDatabase().getArtists()) {
            outputs.add(artist.getUsername());
        }
        for (Host host : Library.getDatabase().getHosts()) {
            outputs.add(host.getUsername());
        }
        jsonObject.put("result", outputs);

        return jsonObject;
    }

    /**
     * show all online normal users
     *
     * @return json
     */
    public static JsonNode showAllOnlineUsers() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        boolean first = true;
        ArrayNode outputs = objectMapper.createArrayNode();

        for (User user : Library.getDatabase().getUsers()) {
            if (user.getOnlineStatus().equals("online")) {
                outputs.add(user.getUsername());
            }
        }
        jsonObject.put("result", outputs);

        return jsonObject;
    }


}

package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * in this class we have function related to the page
 * and its commands
 */
public class Page {

    private static final Integer TOP5NUMBER = 5;
    /**
     * in this function we print the episode te
     * @param podcast the current podcast
     * @return the message returned
     */
    private String printEpisodes(final Podcast podcast) {
        String message = ":\n\t[";
        boolean value = false;
        for (Episode episode : podcast.getEpisodes()) {
            if (!value) {
                message += episode.getName() + " - " + episode.getDescription();
                value = true;
            } else {
                message += ", " + episode.getName() + " - " + episode.getDescription();
            }
        }
        message += "]\n";
        return message;
    }

    /**
     * we set the next page(it changes the page)
     * @return the json format(status)
     */
    public JsonNode nextPage() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());

        jsonObject.put("user", jsonElement.getUsername());

        jsonObject.put("timestamp", jsonElement.getTimestamp());
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        if (user.getOnlineStatus().equals("offline")) {
            jsonObject.put("message",
                    jsonElement.getUsername() + " is offline.");
            return jsonObject;
        }

        if (jsonElement.getNextPage().equals("LikedContent")) {
            Library.getDatabase().setPageStatus("LIKECONTENT");
            user.getUserDatabase().setPageStatus("LIKECONTENT");
            user.getUserDatabase().getPageStatusStack().push("LIKECONTENT");
        } else if (jsonElement.getNextPage().equals("Home")) {
            user.getUserDatabase().setAccesedUser("", user);
            Library.getDatabase().setPageStatus("HOME");
            user.getUserDatabase().setPageStatus("HOME");
            user.getUserDatabase().getPageStatusStack().push("HOME");
        } else if (jsonElement.getNextPage().equals("Artist")) {
            Library.getDatabase().setPageStatus("ARTIST");
            user.getUserDatabase().setPageStatus("ARTIST");
            user.getUserDatabase().getPageStatusStack().push("ARTIST");
        } else if (jsonElement.getNextPage().equals("Host")) {
            Library.getDatabase().setPageStatus("HOST");
            user.getUserDatabase().setPageStatus("HOST");
            user.getUserDatabase().getPageStatusStack().push("HOST");
        } else {
            jsonObject.put("message", "The page "
                    + jsonElement.getNextPage() + " doesn't exist.");
            return jsonObject;
        }
        user.getUserDatabase().getPageStatusNextStack().clear();
        String msg = "";
        msg += jsonElement.getUsername() + " accessed "
                + jsonElement.getNextPage() + " successfully.";
        jsonObject.put("message", msg);

        return jsonObject;
    }
    private String messageHostJson() {
        String message = "Podcasts:\n\t[";
        boolean value = false;

        Host host = Library.getDatabase().
                getHostInstance(Library.getDatabase().getHostElement());
        for (Podcast podcast : host.getPodcasts()) {
            if (!value) {
                message += podcast.getName() + printEpisodes(podcast);
                value = true;
            } else {
                message += ", " + podcast.getName() + printEpisodes(podcast);
            }
        }
        message += "]\n\nAnnouncements:\n\t[";
        value = false;
        for (Announcement announcement : host.getAnnouncements()) {
            if (!value) {
                message += announcement.getName() + ":\n\t"
                        + announcement.getDescription() + "\n";
                value = true;
            } else {
                message += ", " + announcement.getName() + "\n\t"
                        + announcement.getDescription() + "\n";
            }
        }
        message += "]";
        return message;
    }
    private String messageHomeJson(final User user,
                                   final JsonConvertToCategories jsonElement) {
        Comparator<Song> descendingComparator = (o1, o2)
                -> Integer.compare(o2.getUsersLiked().size(), o1.getUsersLiked().size());

        List<Song> sortedList = new ArrayList<>(user.getPrefferedSongs());
        Collections.sort(sortedList, descendingComparator);
        Boolean value = false;
        String message = "Liked songs:\n\t[";

        int i = 0;
        for (Song song : sortedList) {
            if (!value) {
                message += song.getName();
                value = true;
            } else {
                message += ", " + song.getName();
            }
            i++;
            if (i == TOP5NUMBER) {
                break;
            }
        }
        message += "]\n\nFollowed playlists:\n\t[";
        value = false;
        for (Playlist playlist : Library.getDatabase().getPlaylistList()) {
            if (playlist.getUsersFollow().contains(jsonElement.getUsername())) {
                if (!value) {
                    message += playlist.getName();
                    value = true;
                } else {
                    message += ", " + playlist.getName();
                }
            }
        }
        message += "]\n\nSong recommendations:\n\t[";
        value = false;
        for (Song song : user.getRecommendedSongs()) {
            int songIndex = Library.searchSongByNameAndAlbum(song.getName(),
                    song.getAlbum());
            if (!value) {
                message += song.getName();
                value = true;
            } else {
                message += ", " + song.getName();
            }
        }
        message += "]\n\nPlaylists recommendations:\n\t[";
        value = false;
        for (Playlist playlist : user.getRecommendedPlaylists()) {
            if (!value) {
                message += playlist.getName();
                value = true;
            } else {
                message += ", " + playlist.getName();
            }
        }
        message += "]";
        return message;
    }
    private String messageArtistJson() {
        String message = "Albums:\n\t[";
        boolean value = false;
        Artist artist = Library.getDatabase().
                getArtistInstance(Library.getDatabase().getArtistElement());

        for (Album album : artist.getAlbums()) {
            if (!value) {
                message += album.getName();
                value = true;
            } else {
                message += ", " + album.getName();
            }
        }
        message += "]\n\nMerch:\n\t[";
        value = false;
        for (Merch merch : artist.getMerchList()) {
            if (!value) {
                message += merch.getName() + " - "
                        + merch.getPrice() + ":\n\t"
                        + merch.getDescription();
                value = true;
            } else {
                message += ", " + merch.getName()
                        + " - " + merch.getPrice() + ":\n\t"
                        + merch.getDescription();
            }
        }
        message += "]";
        message += "\n\nEvents:\n\t[";
        value = false;
        for (Event event : artist.getEvents()) {
            if (!value) {
                message += event.getName() + " - "
                        + event.getDate() + ":\n\t" + event.getDescription();
                value = true;
            } else {
                message += ", " + event.getName() + " - "
                        + event.getDate() + ":\n\t" + event.getDescription();
            }
        }
        message += "]";
        return message;
    }

    private String messageLikeContent(final User user,
                                      final JsonConvertToCategories jsonElement) {
        String message = "Liked songs:\n\t[";
        boolean value = false;
        for (Song song : user.getPrefferedSongs()) {
            int songIndex = Library.searchSongByNameAndAlbum(song.getName(),
                    song.getAlbum());
            if (!value) {
                message += song.getName() + " - " + Library.getDatabase().getSongs().
                        get(songIndex).getArtist();
                value = true;
            } else {
                message += ", " + song.getName() + " - "
                        + Library.getDatabase().getSongs().get(songIndex).getArtist();
            }
        }
        message += "]\n\nFollowed playlists:\n\t[";
        value = false;
        for (Playlist playlist : Library.getDatabase().getPlaylistList()) {
            if (playlist.getUsersFollow().contains(jsonElement.getUsername())) {
                if (!value) {
                    message += playlist.getName() + " - " + playlist.getOwner();
                    value = true;
                } else {
                    message += ", " + playlist.getName() + " - " + playlist.getOwner();
                }
            }
        }
        message += "]";
        return message;
    }

    private String messageRecommendationsContent(final User user,
                                      final JsonConvertToCategories jsonElement) {
        String message = "Liked songs:\n\t[";
        boolean value = false;
        for (Song song : user.getPrefferedSongs()) {
            int songIndex = Library.searchSongByNameAndAlbum(song.getName(),
                    song.getAlbum());
            if (!value) {
                message += song.getName();
                value = true;
            } else {
                message += ", " + song.getName();
            }
        }
        message += "]\n\nFollowed playlists:\n\t[";
        value = false;
        for (Playlist playlist : Library.getDatabase().getPlaylistList()) {
            if (playlist.getUsersFollow().contains(jsonElement.getUsername())) {
                if (!value) {
                    message += playlist.getName();
                    value = true;
                } else {
                    message += ", " + playlist.getName();
                }
            }
        }
        message += "]\n\nSong recommendations:\n\t[";
        value = false;
        for (Song song : user.getRecommendedSongs()) {
            int songIndex = Library.searchSongByNameAndAlbum(song.getName(),
                    song.getAlbum());
            if (!value) {
                message += song.getName();
                value = true;
            } else {
                message += ", " + song.getName();
            }
        }
        message += "]\n\nPlaylists recommendations:\n\t[";
        value = false;
        for (Playlist playlist : user.getRecommendedPlaylists()) {
            if (!value) {
                message += playlist.getName();
                value = true;
            } else {
                message += ", " + playlist.getName();
            }
        }
        message += "]";
        return message;
    }


    /**
     *
     * @return the page status of the json
     * and the command it should do
     */
    public JsonNode pageJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("user", jsonElement.getUsername());

        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("timestamp", jsonElement.getTimestamp());


        User user = Library.getDatabase().
                getUserInstance(jsonElement.getUsername());
        if (user.getOnlineStatus().equals("offline")) {
            jsonObject.put("message",
                    jsonElement.getUsername() + " is offline.");
            return jsonObject;
        }
        if (Objects.equals(user.getUserDatabase().getPageStatus(), "HOST")) {
            String messageHost = messageHostJson();
            jsonObject.put("message", messageHost);

        } else if (user.getUserDatabase().getPageStatus().equals("HOME")) {
            String messageHome = messageHomeJson(user, jsonElement);
            jsonObject.put("message", messageHome);

        } else if (user.getUserDatabase().getPageStatus().equals("ARTIST")) {
            String messageArtist = messageArtistJson();
            jsonObject.put("message", messageArtist);

        } else if (user.getUserDatabase().getPageStatus().equals("LIKECONTENT")) {
            String messageLikeContent = messageLikeContent(user, jsonElement);
            jsonObject.put("message", messageLikeContent);
        } else if (user.getUserDatabase().getPageStatus().equals("RECOMMENDATIONS")) {
            String messageRecommendations = messageRecommendationsContent(user, jsonElement);
            jsonObject.put("message", messageRecommendations);
        } else {
            jsonObject.put("message", "The page "
                    + jsonElement.getNextPage() + " doesn't exist.");
            return jsonObject;
        }


        return jsonObject;
    }
}

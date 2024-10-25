package command;

import com.fasterxml.jackson.databind.JsonNode;
import json.JsonConvertToCategories;
import status.playerActions;
import status.playerLoad;
import status.playerStatus;
import types.Artist;
import types.Host;
import types.Library;
import types.Playlist;
import types.User;
import types.Page;
import types.Song;
import types.Merch;
import types.Event;
import types.Album;
import types.Podcast;
import types.Announcement;

import java.io.IOException;

/**
 * we return the json for each command
 * we have a command runner for each command
 */
public final class CommandRunner {
    private CommandRunner() {
        throw new UnsupportedOperationException("This is a utility class"
                + " and cannot be instantiated");
    }

    /**
     * search command
     *
     * @param user our current user
     * @return json
     * @throws IOException for seachJsonByFilters
     */
    public static JsonNode searchCommand(final User user) throws IOException {
        Library.getDatabase().setBarrier(true);
        playerStatus status = new playerStatus();
        Library.getDatabase().setBarrier(false);
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        if (user.getUserDatabase().getLoadStatus()) {

            status.moveTimeStamp(user, user.getPrevtime());
        }

        return user.searchJsonByFilters();
    }

    /**
     * select command
     *
     * @return json
     */

    public static JsonNode selectCommand(final User user) {

        return user.selectJsonByFilters();
    }

    /**
     * load command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode loadCommand(final User user) {
        playerLoad status = new playerLoad();
        JsonNode js = status.loadJson(user.getPrevtime());
        return js;
    }

    /**
     * status command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode statusCommand(final User user) {
        playerStatus status = new playerStatus(user.getUserDatabase().getStatus());
        JsonNode js = status.statusJson(user.getPrevtime(), 0);
        return js;
    }

    /**
     * playpause command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode playPauseCommand(final User user) {
        playerLoad playerLoad = user.getUserDatabase().getStatus();
        if (!playerLoad.getPaused()) {
            playerStatus status = new playerStatus(user.getUserDatabase().getStatus());
            JsonNode js = status.statusJson(user.getPrevtime(), 1);
            playerLoad.setPaused(true);
            user.getUserDatabase().setStatus(playerLoad);

            return js;

        }

        playerActions pause = new playerActions();
        playerLoad.setPaused(false);

        JsonNode js = pause.pauseJson(playerLoad.getPaused());
        user.getUserDatabase().setStatus(playerLoad);

        return js;

    }

    /**
     * create playlist command
     *
     * @param user our current user
     * @return json
     * @throws IOException for createPlaylist
     */
    public static JsonNode createPlaylistCommand(final User user)
            throws IOException {
        Playlist create = new Playlist();
        JsonNode js = create.createPlaylist();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * addremove command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode addRemoveInPlaylistCommand(final User user) {
        Playlist addremove = new Playlist();
        JsonNode js = addremove.addremovePlaylist();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * like command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode likeCommand(final User user) {
        if (user.getUserDatabase().getLoadStatus()) {
            playerStatus status = new playerStatus();
            status.moveTimeStamp(user, user.getPrevtime());
        }
        Song like = new Song();
        JsonNode js = like.likejson();
        return js;
    }

    /**
     * show playlists command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode showPlaylistsCommand(final User user) {
        Playlist showPlaylist = new Playlist();
        JsonNode js = showPlaylist.showPlaylist();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * show preferred songs command for user(that is why
     * it is user)
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode showPreferredSongsCommand(final User user) {
        JsonNode js = user.showsongliked();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * repeat command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode repeatCommand(final User user) {
        if (user.getUserDatabase().getLoadStatus()) {
            playerStatus status = new playerStatus(user.getUserDatabase().getStatus());
            JsonNode js = status.statusJson(user.getPrevtime(), 0);

        }
        playerActions repeat = new playerActions();
        JsonNode js = repeat.repeatJson();
        return js;
    }

    /**
     * shuffle command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode shuffleCommand(final User user) {
        if (user.getUserDatabase().getLoadStatus()) {
            playerStatus status = new playerStatus(user.getUserDatabase().getStatus());
            status.statusJson(user.getPrevtime(), 0);
            user.setPrevtime(Library.getDatabase().getJson().getTimestamp());
        }

        playerActions shuffle = new playerActions();
        JsonNode js = shuffle.shuffleJson(user.getPrevtime());
        if (!user.getUserDatabase().getShufflestatus()) {
            user.setPrevtime(Library.getDatabase().getJson().getTimestamp());
        }
        return js;
    }

    /**
     * forward command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode forwardCommand(final User user) {
        if (user.getUserDatabase().getLoadStatus()) {
            playerStatus status = new playerStatus();
            status.moveTimeStamp(user, user.getPrevtime());
            user.setPrevtime(Library.getDatabase().getJson().getTimestamp());
        }

        playerActions forward = new playerActions();
        JsonNode js = forward.forwardJson();
        return js;
    }

    /**
     * backward command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode backwardCommand(final User user) {
        playerActions back = new playerActions();
        JsonNode js = back.backwardJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * prev command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode prevCommand(final User user) {
        if (user.getUserDatabase().getLoadStatus()) {
            playerStatus status = new playerStatus();
            status.moveTimeStamp(user, user.getPrevtime());
            user.setPrevtime(Library.getDatabase().getJson().getTimestamp());
        }

        playerActions prev = new playerActions();
        JsonNode js = prev.prevJson();

        return js;


    }

    /**
     * next command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode nextCommand(final User user) {
        if (user.getUserDatabase().getLoadStatus()) {
            playerStatus status = new playerStatus();
            status.moveTimeStamp(user, user.getPrevtime());
            user.setPrevtime(Library.getDatabase().getJson().getTimestamp());
        }
        playerActions next = new playerActions();
        JsonNode js = next.nextJson();

        return js;
    }

    /**
     * follow command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode followCommand(final User user) {
        JsonNode js = user.followJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * switch visibility command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode switchVisibilityCommand(final User user) {
        if (user.getUserDatabase().getLoadStatus()) {
            playerStatus status = new playerStatus(user.getUserDatabase().getStatus());
            status.statusJson(user.getPrevtime(), 0);
            user.setPrevtime(Library.getDatabase().getJson().getTimestamp());
        }
        JsonNode js = user.visibilityJson();
        return js;
    }

    /**
     * top 5 playlists command
     *
     * @return json
     */
    public static JsonNode getTop5PlaylistsCommand() {
        Playlist top5 = new Playlist();
        JsonNode js = top5.top5Playlists();
        return js;
    }

    /**
     * top 5 songs command
     *
     * @return json
     */
    public static JsonNode getTop5SongsCommand() {
        Song top5 = new Song();
        JsonNode js = top5.top5Songs();
        return js;
    }

    /**
     * remove previous data
     * delete all the previous data from users
     */
    public static void removePreviousData() {
        for (Song song : Library.getDatabase().getSongs()) {
            song.getUsersLiked().clear();
            song.setSongRevenue(0.0);
            song.setNumberOfListens(0);
            song.setSongRevenue(0.0);
        }
        for (User user : Library.getDatabase().getUsers()) {

            if (user.getUserDatabase().getLoadedPlaylist() != null) {
                Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
                playlist.removeAllSongList();
            }
            user.getPrefferedPlaylists().clear();
            user.getPrefferedSongs().clear();
            user.getListenedSongs().clear();
            user.getUserDatabase().setSelectstatus(false);
            user.getUserDatabase().setLoadStatus(false);
            user.getUserDatabase().setLikestatus(false);
            user.getUserDatabase().setPause(false);
            user.getUserDatabase().setPremiumStatus(false);

        }

        for (Artist artist : Library.getDatabase().getArtists()) {
            artist.getAlbums().clear();
            artist.getMerchList().clear();
            artist.getEvents().clear();
            artist.setSongRevenue2(0.0);
            artist.setMostProfitableSong2("");
            artist.setMerchRevenue(0.0);
            artist.setMostProfitableSong("");
            artist.setCount(0);
            artist.setPremiumCount(0);
            artist.setAdCount(0);
        }

        for (Host host : Library.getDatabase().getHosts()) {
            host.removeAllPodcasts();
        }
        Library.getDatabase().setPageStatus("HOME");
        for (Playlist playlist : Library.getDatabase().getPlaylistList()) {
            playlist.getUsersFollow().clear();
        }
        Library.getDatabase().getSongArtistMap().clear();
        Library.getDatabase().setTotale(0);
        Library.getDatabase().removeAllPlaylists();
        Library.getDatabase().removeAllAlbums();
        Library.getDatabase().removeAllArtists();
        Library.getDatabase().removeAllHosts();
    }

    /**
     * change timestamp for some users
     * at the end of the command
     *
     * @param user our current user
     */
    public static void syncAtFinal(final User user) {
        if (user.getOnlineStatus().equals("online")
                && user.getUserDatabase().getLoadStatus()) {
            playerStatus status = new playerStatus();
            status.moveTimeStamp(user, user.getPrevtime());

        }
    }

    /**
     * switch connection status command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode switchConnectionStatusCommand(final
                                                         User user) {
        if (user.getUserDatabase().getLoadStatus() && user.getOnlineStatus().equals("online")) {
            playerStatus status = new playerStatus();
            status.moveTimeStamp(user, user.getPrevtime());
        }
        JsonNode js = Library.onlineJson();
        return js;
    }

    /**
     * get online users command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode getOnlineUsersCommand(final
                                                 User user) {
        JsonNode js = Library.showAllOnlineUsers();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * get online friends command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode addUserCommand(final User
                                                  user) {

        JsonNode js = Library.addUserJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * add album command
     *
     * @param user our current user
     * @return json
     * @throws IOException for addAlbumJson
     */
    public static JsonNode addAlbumCommand(final
                                           User user) throws IOException {
        Album addalbum = new Album();
        JsonNode js = addalbum.addAlbumJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * show albums command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode showAlbumsCommand(final User user) {
        Album showalbum = new Album();
        JsonNode js = showalbum.showAlbumJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * show artists command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode printCurrentPageCommand(final User user) {
        Page page = new Page();
        JsonNode js = page.pageJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * add artist command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode addMerchCommand(final User user) {
        Merch merch = new Merch();
        JsonNode js = merch.merchJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * add event command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode addEventCommand(final User user) {
        Event event = new Event();
        JsonNode js = event.eventJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * delete user command
     *
     * @return json
     */
    public static JsonNode deleteUserCommand() {
        for (User userElement : Library.getDatabase().getUsers()) {

            if (Library.getDatabase().checkifUserInstance(userElement.getUsername())) {

                if (userElement.getUserDatabase().getLoadStatus()) {

                    Library.getDatabase().setBarrier(true);
                    playerStatus status = new playerStatus();
                    Library.getDatabase().setBarrier(false);
                    status.moveTimeStamp(userElement, userElement.getPrevtime());
                    userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
                }
                userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());

            }
        }

        JsonNode js = Library.deleteUserJson();
        return js;
    }

    /**
     * get all users command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode getAllUsersCommand(final User user) {
        JsonNode js = Library.showAllUsers();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * add podcast command
     *
     * @param user our current user
     * @return json
     * @throws IOException for addPodcastJson
     */
    public static JsonNode addPodcastCommand(final User user)
            throws IOException {
        Podcast podcast = new Podcast();
        JsonNode js = podcast.addPodcastJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * add artist command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode addAnnouncementCommand(final User user) {
        Announcement announcement = new Announcement();
        JsonNode js = announcement.announcementJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * add artist command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode removeAnnouncementCommand(final User user) {
        Announcement announcement = new Announcement();
        JsonNode js = announcement.removeannouncementJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * show artists command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode showPodcastsCommand(final User user) {
        Podcast podcast = new Podcast();
        JsonNode js = podcast.showPodcastjson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * remove album command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode removeAlbumCommand(final User user) {
        for (User userElement : Library.getDatabase().getUsers()) {
            if (Library.getDatabase().checkifUserInstance(userElement.getUsername())) {

                if (userElement.getUserDatabase().getLoadStatus()) {

                    playerStatus status = new playerStatus();
                    status.moveTimeStamp(userElement, userElement.getPrevtime());
                    userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
                }
                userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());

            }
        }
        Album album = new Album();
        JsonNode js = album.removeAlbumJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * change page command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode changePageCommand(final User user) {
        Page page = new Page();
        JsonNode js = page.nextPage();
        CommandRunner.syncAtFinal(user);
        return js;

    }

    /**
     * remove podcast command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode removePodcastCommand(final User user) {
        Podcast podcast = new Podcast();
        JsonNode js = podcast.removePodcastJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * remove event command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode removeEventCommand(final User user) {
        Event event = new Event();
        JsonNode js = event.removeEventJson();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * top 5 albums command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode getTop5AlbumsCommand(final User user) {
        Album album = new Album();
        JsonNode js = album.top5Album();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * top 5 artists command
     *
     * @param user our current user
     * @return json
     */
    public static JsonNode getTop5ArtistsCommand(final User user) {
        Artist artist = new Artist();
        JsonNode js = artist.top5Artist();
        CommandRunner.syncAtFinal(user);
        return js;
    }

    /**
     * wrapped command which returns
     * stats about listens for each user
     */
    public static JsonNode wrappedCommand(final User user) {
        for (User userElement : Library.getDatabase().getUsers()) {

            if (Library.getDatabase().checkifUserInstance(userElement.getUsername())) {

                if (userElement.getUserDatabase().getLoadStatus()) {

                    Library.getDatabase().setBarrier(true);
                    playerStatus status = new playerStatus();
                    Library.getDatabase().setBarrier(false);
                    status.moveTimeStamp(userElement, userElement.getPrevtime());
                    userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
                }
                userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());

            }
        }

        JsonNode js = Library.wrappedJson();

        return js;
    }

    /**
     * monetization stats about each artist
     */
    public static JsonNode endProgramCommand() {

        for (User userElement : Library.getDatabase().getUsers()) {

            if (Library.getDatabase().checkifUserInstance(userElement.getUsername())) {
                if (userElement.getUserDatabase().getLoadStatus()) {

                    Library.getDatabase().setBarrier(true);
                    playerStatus status = new playerStatus();
                    Library.getDatabase().setBarrier(false);
                    status.moveTimeStamp(userElement, userElement.getPrevtime());
                    userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
                }
                userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
                if (userElement.getUserDatabase().getPremiumStatus()) {
                    userElement.givePremiumMoneyToSongs();

                    userElement.getUserDatabase().setPremiumStatus(false);
                }
            }
        }
        JsonNode js = Library.endProgramJson();
        return js;
    }

    /**
     * buy premium command
     */
    public static JsonNode buyPremiumCommand(final User user) {
        for (User userElement : Library.getDatabase().getUsers()) {

            if (Library.getDatabase().checkifUserInstance(userElement.getUsername())) {
                if (userElement.getUserDatabase().getLoadStatus()) {

                    Library.getDatabase().setBarrier(true);
                    playerStatus status = new playerStatus();
                    Library.getDatabase().setBarrier(false);
                    status.moveTimeStamp(userElement, userElement.getPrevtime());
                    userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
                }
                userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
            }
        }
        JsonNode js = user.buyPremiumJson();
        return js;
    }

    /**
     * cancel premium command
     */

    public static JsonNode cancelPremiumCommand(final User user) {
        for (User userElement : Library.getDatabase().getUsers()) {

            if (Library.getDatabase().checkifUserInstance(userElement.getUsername())) {
                if (userElement.getUserDatabase().getLoadStatus()) {

                    Library.getDatabase().setBarrier(true);
                    playerStatus status = new playerStatus();
                    Library.getDatabase().setBarrier(false);
                    status.moveTimeStamp(userElement, userElement.getPrevtime());
                    userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
                }
                userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
            }
        }
        JsonNode js = user.cancelPremiumJson();
        return js;
    }

    /**
     * cancel premium command
     */

    public static JsonNode adBreakCommand(final User user) {
        for (User userElement : Library.getDatabase().getUsers()) {

            if (Library.getDatabase().checkifUserInstance(userElement.getUsername())) {
                if (userElement.getUserDatabase().getLoadStatus()) {

                    Library.getDatabase().setBarrier(true);
                    playerStatus status = new playerStatus();
                    Library.getDatabase().setBarrier(false);
                    status.moveTimeStamp(userElement, userElement.getPrevtime());
                    userElement.setPrevtime(Library.getDatabase().getJson().getTimestamp());
                }
            }
        }
        JsonNode js = user.adBreakJson();
        return js;
    }

    /**
     * subscribe command which returns
     * subscribe to a paritcalar artist
     */
    public static JsonNode subscribeCommand(final User user) {

        JsonNode js = user.subscribeJson();
        CommandRunner.syncAtFinal(user);

        return js;
    }

    /**
     * notifications command which returns
     * notifications about artists and hosts about a user
     */
    public static JsonNode notificationCommand(final User user) {

        JsonNode js = user.notificationsJson();
        CommandRunner.syncAtFinal(user);

        return js;
    }

    /**
     * command which returns
     * what the user has bought recently from an artist
     */
    public static JsonNode buyMerchCommand(final User user) {

        JsonNode js = user.buyMerchJson();
        CommandRunner.syncAtFinal(user);

        return js;
    }

    /**
     * command which returns
     * what the all the things an artist bought from an artist
     */
    public static JsonNode seeMerchCommand(final User user) {

        JsonNode js = user.seeMerchJson();
        CommandRunner.syncAtFinal(user);

        return js;
    }

    /**
     * updateRecommandation command which returns
     * an update recommandation for a user
     */
    public static JsonNode updateRecommandationCommand(final User user) {
        String recommendationType = Library.getDatabase().
                getJson().getRecommendationType();
        JsonNode js = null;
        CommandRunner.syncAtFinal(user);

        if (recommendationType.equals("fans_playlist")) {
            js = user.updateMyRecommandationsFansPlaylistJson();
        }
        if (recommendationType.equals("random_song")) {
            js = user.updateMyRecommandationsRandomSongJson();
        }
        if (recommendationType.equals("random_playlist")) {
            js = user.updateMyRecommandationsRandomPlaylistJson();
        }

        return js;
    }

    /**
     * loadedRecommandation command which returns
     * the loaded recommandation for a user
     */
    public static JsonNode loadRecommendationsCommand(final User user) {
        CommandRunner.syncAtFinal(user);
        JsonNode js = user.loadRecommendationJson();

        return js;
    }

    /**
     * previous Page command which returns
     * the previous page the user accessed
     */
    public static JsonNode previousPageCommand(final User user) {

        JsonNode js = user.previousPageJson();
        CommandRunner.syncAtFinal(user);

        return js;
    }

    /**
     * previous Page command which returns
     * the previous page the user accessed
     */
    public static JsonNode nextPageCommand(final User user) {

        JsonNode js = user.nextPageJson();
        CommandRunner.syncAtFinal(user);

        return js;
    }


}

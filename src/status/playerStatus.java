package status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;
import lombok.Getter;
import lombok.Setter;
import types.*;
import visitor.NegativeTimeStampVisitor;

import java.util.List;
import java.util.Objects;

/**
 * aici in functia status am incercat sa implementez pentru doua situatia: prima cu statusjson
 * in care am nevoie sa public
 * il folosesc pentru status si pentru pause, de aceea avem conditia pause_or_not,
 * a doua cand e orice alta
 * functia si am nevoie doar sa modific sincronizez melodia, playlistul, podcastul
 * cu timestampul
 * si in fiecare daca diferenta dintre melodia si timestamp si element este negativ,
 * il parcurg pana imi da pozitiv si am gasit melodia curent, si avem cazuri pentru
 * random si normal*
 */
@Getter
@Setter
public final class playerStatus extends playerLoad {
    private final static int ADTIME = 10;
    public playerStatus(final playerLoad playerLoad) {
        super(playerLoad);
        if (playerLoad == null) {
            return;
        }
        this.setRepeat(playerLoad.getRepeat());
        this.setShuffle(playerLoad.getShuffle());
        this.setGenre(playerLoad.getGenre());
        this.setPaused(playerLoad.getPaused());
        this.setName(playerLoad.getName());
        this.setAlbum(playerLoad.getAlbum());
        this.setGenre(playerLoad.getGenre());
        this.setRemainedTime(playerLoad.getRemainedTime());
        this.setType(playerLoad.getType());


    }

    public playerStatus(final User user) {
    }

    public playerStatus(final int e) {

    }

    public playerStatus() {

    }

    /**
     * element negativ for song, in cazul in care valorile noastre sunt negative, luam pe cazuri,
     * ca sa simplificam procesul
     *
     * @param playerLoad load
     * @param user       our current user
     */
    public void elementNegativForSong(final playerLoad playerLoad, final User user) {
        int newI = playerLoad.getRemainedTime();
        playerLoad.setRemainedTime(0);

        List<String> songNames = Library.getDatabase().getSongs().stream()
                .filter(song -> "Ad Break".equals(song.getName()))
                .map(Song::getName)
                .toList();

        List<String> songNames2 = Library.getDatabase().getSongs().stream()
                .filter(song -> "Arrival".equals(song.getAlbum()))
                .map(Song::getName)
                .toList();

        Song song = Library.getDatabase().getSongs().get(Library.
                searchSongByNameAndAlbum(playerLoad.getName(), playerLoad.getAlbum()));
        assert song != null;

        while (user.getRepeat() > 0 && (newI) <= 0) {
            if (user.getRepeat() == 1) {
                user.setRepeat(0);
            }

            this.setRemainedTime(song.getDuration() + newI);
            playerLoad.setRemainedTime(song.getDuration() + newI);
            if (!song.getName().equals("Ad Break")) {
                user.listenToSong(song);
            }
            newI += song.getDuration();

        }
        if (user.getRepeat() > 0 || newI > 0) {
            if (user.getRepeat() == 1) {
                user.setRepeat(0);
            }
            this.setRemainedTime(newI);
            playerLoad.setRemainedTime(newI);
            if (this.getRemainedTime() < 0 && user.getRepeat() == 1) {
                this.setRemainedTime(0);
                playerLoad.setRemainedTime(0);
                this.setPaused(true);
                playerLoad.setPaused(true);
                user.getUserDatabase().setLoadStatus(false);

                user.getUserDatabase().setSelectstatus(false);
                this.setName("");
                playerLoad.setName("");
                this.setAlbum("");
                this.setGenre("");
                playerLoad.setAlbum("");
                this.setGenre("");
                playerLoad.setGenre("");
                user.getUserDatabase().
                        setAccesedUser("", user);

            }

        } else {


            if (newI <= 0) {


                this.setRemainedTime(0);

                this.setPaused(true);
                playerLoad.setPaused(true);
                user.getUserDatabase().setLoadStatus(false);

                user.getUserDatabase().setSelectstatus(false);
                this.setName("");
                playerLoad.setName("");
                this.setAlbum("");
                playerLoad.setAlbum("");
                this.setGenre("");
                playerLoad.setGenre("");
                user.getUserDatabase().
                        setAccesedUser("", user);

            }


        }
        if (user.getRepeat() == 0) {
            playerLoad.setRepeat("No Repeat");
        } else if (user.getRepeat() == 1) {
            playerLoad.setRepeat("Repeat Once");
        } else {
            playerLoad.setRepeat("Repeat Infinite");
        }
    }

    /**
     * if the set remaining time of the podcast is negative
     *
     * @param newI       set remaining time of the load
     * @param playerLoad load
     * @param user       user
     */
    public void elementPodcastNegativ(final int newI, final
    playerLoad playerLoad, final User user) {
        Podcast podcast = Library.
                podcastByName(user.getUserDatabase().getLoadedElement());
        playerLoad.setRemainedTime(0);

        assert podcast != null;


        user.getUserDatabase().setSelectstatus(false);


        while (playerLoad.getRemainedTime() <= 0 && (user.getLoadedEpisode()
                < podcast.getEpisodes().size() - 1)) {
            originalLength(newI, playerLoad, podcast, user);
        }
        if (playerLoad.getRemainedTime() <= 0) {
            isTheLoadedEpisodeTheLast(playerLoad, user, podcast);
        }
        if (user.getRepeat() == 0) {
            this.setRepeat("No Repeat");
        } else if (user.getRepeat() == 1) {
            this.setRepeat("Repeat Once");
        } else {
            this.setRepeat("Repeat Infinite");
        }
    }

    /**
     * checks if the loaded episode is the last in order to return the apropiate result
     *
     * @param playerLoad load
     * @param user       user
     * @param podcast    podcast
     */

    public static void isTheLoadedEpisodeTheLast(final playerLoad playerLoad, final User user,
                                                 final Podcast podcast) {
        if (user.getLoadedEpisode()
                == podcast.getEpisodes().size() - 1) {
            int original = podcast.getEpisodes().get(user.getLoadedEpisode()).getDuration();
            podcast.getEpisodes().get(user.getLoadedEpisode()).setRemainingduration(original);
            if (user.getRepeat() > 0) {
                if (user.getRepeat() == 1) {
                    user.setRepeat(0);

                }
                user.setLoadedEpisode(0);
                Episode episode = podcast.getEpisodes().get(0);
                user.listenToEpisode(episode);
                playerLoad.setRemainedTime(podcast.getEpisodes().get(0).getDuration());
                playerLoad.setAlbum("");
                playerLoad.setGenre("");

            } else {
                playerLoad.setPaused(true);
                user.getUserDatabase().setSelectstatus(false);
                user.getUserDatabase().setLoadStatus(false);
                playerLoad.setAlbum("");
                playerLoad.setName("");
                playerLoad.setGenre("");
                user.getUserDatabase().setAccesedUser("", user);

            }
        }
    }

    /**
     * calculates the original length
     *
     * @param newI       the remaining time of the load
     * @param playerLoad load
     * @param podcast    our podcast
     */
    public static void originalLength(final int newI, final playerLoad playerLoad,
                                      final Podcast podcast, final User user) {
        int original = podcast.getEpisodes().get(user.getLoadedEpisode()).getDuration();
        podcast.getEpisodes().get(user.getLoadedEpisode()).setRemainingduration(original);
        user.setLoadedEpisode(user.getLoadedEpisode() + 1);
        Episode episode = podcast.getEpisodes().get(user.getLoadedEpisode());
        user.listenToEpisode(episode);
        playerLoad.setName(podcast.getEpisodes().get(user.getLoadedEpisode()).getName());
        playerLoad.setAlbum("");
        playerLoad.setGenre("");
        List<Episode> listEpisode = podcast.getEpisodes();
        int newEpisodeDuration = listEpisode.get(user.getLoadedEpisode()).getDuration();
        playerLoad.setRemainedTime(newEpisodeDuration + newI);
        int loadedEpisode = user.getLoadedEpisode();
        podcast.getEpisodes().get(loadedEpisode).setRemainingduration(playerLoad.getRemainedTime());

    }

    /**
     * checks if the element is negative, and skips to others tracks(or repeat) or ends the playlist
     *
     * @param playerLoad load
     * @param user       load
     */
    public void ifElementNegativ(final playerLoad playerLoad, final User user) {
        int newI = playerLoad.getRemainedTime();
        if (playerLoad.getRemainedTime() <= 0) {
            if (Objects.equals(playerLoad.getType(), "song")) {
                elementNegativForSong(playerLoad, user);
            } else if (Objects.equals(playerLoad.getType(), "podcast")) {
                elementPodcastNegativ(newI, playerLoad, user);
            } else if (Objects.equals(playerLoad.getType(), "playlist")) {

                Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
                assert playlist != null;
                NegativeTimeStampVisitor visitor = new NegativeTimeStampVisitor();
                playerLoad playerLoad3 = playlist.
                        acceptNegativeRemaininingTimeForStatusJson(visitor, playerLoad, user, newI);

                if (playerLoad3 != null) {
                    setStatus(playerLoad3);
                }

            } else if (Objects.equals(playerLoad.getType(), "album")) {
                Album album = user.getUserDatabase().getLoadedAlbum();
                assert album != null;
                NegativeTimeStampVisitor visitor = new NegativeTimeStampVisitor();
                playerLoad playerLoad3 = album.
                        acceptNegativeRemaininingTimeForStatusJson(visitor, playerLoad, user, newI);

                if (playerLoad3 != null) {
                    setStatus(playerLoad3);
                }
            }
        }


    }

    /**
     * checks if the element is negative, and skips to others tracks or ends the playlist
     * for no repeat
     *
     * @param playlist the current playlist
     * @param user     the current user
     * @return true or false
     */
    public static boolean isTheEndofPlaylistForNoRepeat2(final Playlist playlist, final User user) {
        return user.getSongIndex() >= playlist.getSongList().size()
                && user.getRepeat() == 0;
    }

    /**
     * checks if the element is negative,
     * and skips to others tracks or ends the playlist
     * for repeat all
     * if is the end of the song and it still negative
     * it goes to the beginning of the playlist
     *
     * @param playerLoad
     * @param playlist
     * @param user
     * @return
     */
    public static int endOfPlaylistForRepeatAll(final playerLoad playerLoad, final
    Playlist playlist, final User user) {
        int newI = playerLoad.getRemainedTime();
        if (user.getRepeat() == 1
                && (user.getSongIndex() == (playlist.getSongList().size() - 1)) && newI <= 0) {
            user.setSongIndex(0);
            int index2 = getIndex(playlist, user);
            Song song = Library.getDatabase().getSongs().get(index2);
            playerLoad.setName(Library.getDatabase().getSongs().get(index2).getName());
            playerLoad.setAlbum(Library.getDatabase().getSongs().get(index2).getAlbum());
            playerLoad.setGenre(Library.getDatabase().getSongs().get(index2).getGenre());
            if (!song.getName().equals("Ad Break")) {
                user.listenToSong(song);
            }
            playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                    get(index2).getDuration() + newI);
            newI += Library.getDatabase().getSongs().get(index2).getDuration();


        }
        return newI;
    }

    /**
     * gets the index of the song for playlist
     * whether is random or not
     *
     * @param playlist the current playlist
     * @param user     the current user
     * @return the index of the song
     */
    public static int getIndex(final Playlist playlist, final User user) {
        int index;
        if (!user.getStatusshuffle()) {
            user.setSongindexrandom(user.getSongIndex());
            String searchSong = playlist.getSongList().get(user.getSongIndex());
            String searchAlbum = playlist.getAlbumList().get(user.getSongIndex());
            index = Library.searchSongByNameAndAlbum(searchSong, searchAlbum);

        } else {
            user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
            String searchSong = playlist.getSongList().get(playlist.
                    getnewRandomSongIndex(user));
            String searchAlbum = playlist.getAlbumList().get(playlist.
                    getnewRandomSongIndex(user));
            index = Library.searchSongByNameAndAlbum(searchSong, searchAlbum);
        }
        return index;
    }

    /**
     * gets the index of the song for album
     * whether is random or not
     *
     * @param album the current album
     * @param user  the current user
     * @return the index of the song
     */
    public static int getIndexAlbum(final Album album, final User user) {
        int index;
        if (!album.getStatusshuffle()) {
            user.setSongindexrandom(user.getSongIndex());
            index = Library.searchSongByNameAndAlbum(album.getSongs().
                    get(user.getSongIndex()).getName(), album.getName());
        } else {
            user.setSongindexrandom(album.getnewRandomSongIndex(user));
            index = Library.searchSongByNameAndAlbum(album.getSongs().
                    get(album.getnewRandomSongIndex(user)).getName(), album.getName());
        }
        return index;
    }

    private ObjectNode returnjsonObject() {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.createObjectNode();

    }
    private void updateStatsStatus(final User user, final playerLoad playerLoad,
     final JsonConvertToCategories jsonElement, final int prevTimestamp) {
        int i, newI;
        if (Objects.equals(super.getType(), "podcast")) {
            Podcast podcast = Library.podcastByName(user.getUserDatabase().
                    getLoadedElement());
            assert podcast != null;
            i = podcast.getEpisodes().get(user.getLoadedEpisode()).
                    getRemainingduration();

            newI = i - (jsonElement.getTimestamp() - prevTimestamp);
            podcast.getEpisodes().get(user.getLoadedEpisode()).
                    setRemainingduration(newI);
        } else {

            i = playerLoad.getRemainedTime();
            newI = i - (jsonElement.getTimestamp() - prevTimestamp);
        }
        playerLoad.setRemainedTime(newI);
        this.setRemainedTime(newI);

        if (user.getPrevAdTimestamp() != -1 && playerLoad.getRemainedTime() <= 0) {
            newI = playerLoad.getRemainedTime();
            Song song2 = null;
            int price = -1;
            for (Song songlist : Library.getDatabase().getSongs()) {
                if (songlist.getName().equals("Ad Break")) {
                    song2 = songlist;
                    this.setRemainedTime(song2.getDuration() + newI);
                    playerLoad.setRemainedTime(song2.getDuration() + newI);
                    newI += song2.getDuration();
                    price = user.getPrevAdTimestamp();
                    user.setPrevAdTimestamp(-1);
                    break;
                }
            }
            if (newI <= 0) {
                if (price != -1) {
                    user.giveAdMoneyToSongs(price);
                    user.getAdSongListen().remove("Ad Break");
                    user.getAdListenedSongs().remove("Ad Break");
                    user.getAdSongListen().clear();
                    user.getAdListenedSongs().clear();

                }

            } else {

                user.setPrevPlayerLoad(playerLoad);
                user.setTempPrice(price);
                this.setName(song2.getName());

                playerLoad.setName(song2.getName());
                this.setAlbum("");
                playerLoad.setAlbum("");
                this.setGenre(song2.getGenre());
                playerLoad.setGenre(song2.getGenre());
                this.setType("song");
                playerLoad.setType("song");
                super.setType("song");
                user.setRepeat(0);
            }

        } else if (playerLoad.getName().equals("Ad Break")
                && playerLoad.getRemainedTime() <= 0) {
            user.giveAdMoneyToSongs(user.getTempPrice());
            user.getAdSongListen().clear();
            user.getAdListenedSongs().clear();
            user.setTempPrice(0);
            int copyRemainingTime = playerLoad.getRemainedTime();
            playerLoad.setRepeat(user.getPrevPlayerLoad().getRepeat());
            playerLoad.setRemainedTime(copyRemainingTime);
            playerLoad.setPaused(user.getPrevPlayerLoad().getPaused());
            playerLoad.setName(user.getPrevPlayerLoad().getName());
            playerLoad.setAlbum(user.getPrevPlayerLoad().getAlbum());
            playerLoad.setGenre(user.getPrevPlayerLoad().getGenre());
            playerLoad.setType(user.getPrevPlayerLoad().getType());
            playerLoad.setShuffle(user.getPrevPlayerLoad().getShuffle());
            super.setType(user.getPrevPlayerLoad().getType());


        }
        if (this.getRemainedTime() <= 0) {
            negativeRemaininingTimeForStatusJson(playerLoad, user);
        }
    }

    /**
     * used when we have function other than status,load,pause etc.
     * we do not want the json parameter
     *
     * @param prevTimestamp previous timestamp
     */
    public void moveTimeStamp(final User user2, final int prevTimestamp) {
        ObjectNode jsonObject = returnjsonObject();
        ObjectNode smallJsonObject = returnjsonObject();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        if (user2 != user) {
            user = user2;
        }

        playerLoad playerLoad = user.getUserDatabase().getStatus();
        if (playerLoad == null || playerLoad.getName() == null) {
            return;
        }
        if (Objects.equals(playerLoad.getName(), "")
                && Objects.equals(user.getUserDatabase().getLoadedElement(), "")) {
            return;
        }

        if (!playerLoad.getPaused()) {

            if (Objects.equals(playerLoad.getType(), "album")) {

                String albumName = playerLoad.getAlbum();
                String ownerName = playerLoad.getOwner();
                Album album2 = user.getUserDatabase().
                        selectAlbumByNameAndOwner(albumName, ownerName);

                if (album2 == null) {
                    user.getUserDatabase().setStatus(null);
                    return;
                }

            }
            if (Objects.equals(playerLoad.getType(), "song")) {
                int index = Library.searchSongByNameAndAlbum(playerLoad.
                        getName(), playerLoad.getAlbum());
                if (index == -1) {
                    user.getUserDatabase().setStatus(null);
                    return;
                }
            }
            int i, newI;
            if (Objects.equals(playerLoad.getType(), "podcast")) {
                Podcast podcast = Library.
                        podcastByName(user.getUserDatabase().getLoadedElement());
                assert podcast != null;
                i = podcast.getEpisodes().
                        get(user.getLoadedEpisode()).getRemainingduration();
                newI = i - (jsonElement.getTimestamp() - prevTimestamp);
                playerLoad.setRemainedTime(newI);
                podcast.getEpisodes().
                        get(user.getLoadedEpisode()).setRemainingduration(newI);
                this.setName(playerLoad.getName());
                this.setAlbum("");
                this.setGenre("");

            } else {

                i = playerLoad.getRemainedTime();
                newI = i - (jsonElement.getTimestamp() - prevTimestamp);
                playerLoad.setRemainedTime(newI);

            }
            playerLoad.setRemainedTime(i - (jsonElement.getTimestamp() - prevTimestamp));

            if (user.getPrevAdTimestamp() != -1 && playerLoad.getRemainedTime() <= 0) {


                newI = playerLoad.getRemainedTime();
                Song song2 = null;
                int price = -1;

                for (Song songlist : Library.getDatabase().getSongs()) {
                    if (songlist.getName().equals("Ad Break")) {
                        song2 = songlist;
                        this.setRemainedTime(song2.getDuration() + newI);
                        playerLoad.setRemainedTime(song2.getDuration() + newI);
                        newI += ADTIME;
                        price = user.getPrevAdTimestamp();
                        user.setPrevAdTimestamp(-1);
                        break;
                    }
                }
                if (newI <= 0) {

                    if (price != -1) {
                        user.giveAdMoneyToSongs(price);
                        user.getAdSongListen().remove("Ad Break");
                        user.getAdListenedSongs().remove("Ad Break");
                        user.getAdSongListen().clear();
                        user.getAdListenedSongs().clear();
                        playerLoad.setRemainedTime(newI);

                    }

                } else {
                    if (price != -1) {
                        user.giveAdMoneyToSongs(price);
                        user.getAdSongListen().remove("Ad Break");
                        user.getAdListenedSongs().remove("Ad Break");
                        user.getAdSongListen().clear();
                        user.getAdListenedSongs().clear();

                    }
                    user.setPrevPlayerLoad(playerLoad);
                    user.setTempPrice(price);
                    this.setName(song2.getName());
                    playerLoad.setRemainedTime(newI);
                    playerLoad.setName(song2.getName());
                    this.setAlbum("");
                    playerLoad.setAlbum("");
                    this.setGenre(song2.getGenre());
                    playerLoad.setGenre(song2.getGenre());
                    this.setType("song");
                    playerLoad.setType("song");
                    super.setType("song");
                    user.setRepeat(0);
                }

            } else if (playerLoad.getName().equals("Ad Break")
                    && playerLoad.getRemainedTime() <= 0) {
                user.giveAdMoneyToSongs(user.getTempPrice());

                user.getAdSongListen().clear();
                user.getAdListenedSongs().clear();
                user.setTempPrice(0);
                int copyRemainingTime = playerLoad.getRemainedTime();
                playerLoad.setRepeat(user.getPrevPlayerLoad().getRepeat());
                playerLoad.setRemainedTime(copyRemainingTime);
                playerLoad.setPaused(user.getPrevPlayerLoad().getPaused());
                playerLoad.setName(user.getPrevPlayerLoad().getName());
                playerLoad.setAlbum(user.getPrevPlayerLoad().getAlbum());
                playerLoad.setGenre(user.getPrevPlayerLoad().getGenre());
                playerLoad.setType(user.getPrevPlayerLoad().getType());
                playerLoad.setShuffle(user.getPrevPlayerLoad().getShuffle());
                super.setType(user.getPrevPlayerLoad().getType());


            }

            if (playerLoad.getRemainedTime() <= 0) {
                ifElementNegativ(playerLoad, user);
            }

        } else {
            if (Objects.equals(playerLoad.getType(), "podcast")) {

                Podcast podcast = Library.
                        podcastByName(user.getUserDatabase().getLoadedElement());

                assert podcast != null;
                this.setName(podcast.
                        getEpisodes().get(user.getLoadedEpisode()).getName());

                this.setAlbum("");
                this.setGenre("");
                this.setRemainedTime(podcast.getEpisodes().
                        get(user.getLoadedEpisode()).getDuration());
            }
        }
        smallJsonObject.put("name", this.getName());

        if (Objects.equals(playerLoad.getName(), "")) {
            user.getUserDatabase().setLoadedElement("");
        }

        smallJsonObject.put("remainedTime", this.getRemainedTime());
        smallJsonObject.put("repeat", this.getRepeat());

        if (Objects.equals(playerLoad.getType(), "playlist")) {

            Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
            assert playlist != null;
            smallJsonObject.put("shuffle", user.getStatusshuffle());

        } else if (Objects.equals(playerLoad.getType(), "album")) {

            Album album = user.getUserDatabase().getLoadedAlbum();
            assert album != null;
            smallJsonObject.put("shuffle", album.getStatusshuffle());

        } else {

            smallJsonObject.put("shuffle", false);
        }
        smallJsonObject.put("paused", this.getPaused());
        jsonObject.set("stats", smallJsonObject);

    }

    /**
     * this function is used for returning the statusJson
     * and facing various posibilities with negative etc.
     *
     * @param prevTimestamp previous timestamp
     * @param pauseOrNot    if pause or not
     * @return json
     */
    public JsonNode statusJson(final int prevTimestamp, final int pauseOrNot) {
        ObjectMapper objectMapper = getObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        ObjectNode smallJsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        playerLoad playerLoad = user.getUserDatabase().getStatus();
        if (pauseOrNot != 2) {
            jsonObject.put("command", jsonElement.getCommand());
            jsonObject.put("user", jsonElement.getUsername());
            jsonObject.put("timestamp", jsonElement.getTimestamp());
        }

        int i, newI;
        if (playerLoad != null && !Objects.equals(playerLoad.getName(), "")
                && !Objects.equals(user.getUserDatabase().getLoadedElement(), "")) {
            if (Objects.equals(super.getType(), "podcast")) {
                Podcast podcast = Library.podcastByName(user.
                        getUserDatabase().getLoadedElement());
                assert podcast != null;
                if (user.getRepeat() == 0) {
                    playerLoad.setRepeat("No Repeat");
                } else if (user.getRepeat() == 1) {
                    playerLoad.setRepeat("Repeat Once");
                } else {
                    playerLoad.setRepeat("Repeat Infinite");
                }
                this.setName(podcast.getEpisodes().get(user.
                        getLoadedEpisode()).getName());
                this.setAlbum("");
                this.setGenre("");
                this.setRemainedTime(this.getRemainedTime());

            } else if (Objects.equals(super.getType(), "song")) {
                Song song = Library.getDatabase().getSongs().
                        get(Library.searchSongByNameAndAlbum(playerLoad.getName(),
                                playerLoad.getAlbum()));
                assert song != null;
                if (user.getRepeat() == 0) {
                    this.setRepeat("No Repeat");
                } else if (user.getRepeat() == 1) {
                    this.setRepeat("Repeat Once");
                } else {
                    this.setRepeat("Repeat Infinite");
                }

            } else if (Objects.equals(super.getType(), "playlist")) {
                Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
                assert playlist != null;
                if (user.getRepeat() == 0) {
                    this.setRepeat("No Repeat");
                } else if (user.getRepeat() == 1) {
                    this.setRepeat("Repeat All");
                } else {
                    this.setRepeat("Repeat Current Song");
                }

            } else if (Objects.equals(super.getType(), "album")) {
                Album album = user.getUserDatabase().getLoadedAlbum();
                assert album != null;
                if (user.getRepeat() == 0) {
                    this.setRepeat("No Repeat");
                } else if (user.getRepeat() == 1) {
                    this.setRepeat("Repeat All");
                } else {
                    this.setRepeat("Repeat Current Song");
                }

            }

            if (!super.getPaused() && user.getOnlineStatus().equals("online")) {
                updateStatsStatus(user, playerLoad, jsonElement, prevTimestamp);
            } else {
                if (Objects.equals(super.getType(), "podcast")) {
                    Podcast podcast =
                            Library.podcastByName(user.getUserDatabase().getLoadedElement());
                    assert podcast != null;
                    Episode getLoadedEpisode = podcast.getEpisodes().
                            get(user.getLoadedEpisode());
                    this.setName(podcast.getEpisodes().get(user.getLoadedEpisode()).getName());
                    this.setAlbum("");
                    this.setGenre("");
                    this.setRemainedTime(getLoadedEpisode.getRemainingduration());
                }
            }
        } else {
            if (playerLoad != null) {
                this.setPaused(playerLoad.getPaused());
                this.setName("");
                this.setAlbum("");
                this.setGenre("");
                playerLoad.setAlbum("");
                playerLoad.setName("");
                playerLoad.setGenre("");
                user.getUserDatabase().setAccesedUser("", user);
                user.getUserDatabase().setLoadStatus(false);

                this.setPaused(true);
                playerLoad.setPaused(true);
                this.setRemainedTime(0);
                playerLoad.setRemainedTime(0);
                this.setShuffle(false);
                playerLoad.setShuffle(false);
                this.setRepeat("No Repeat");
                playerLoad.setRepeat(this.getRepeat());
            }
        }
        if (pauseOrNot == 1) {

            this.setPaused(!this.getPaused());
            if (!playerLoad.getName().isEmpty()) {
                jsonObject.put("message",
                        "Playback paused successfully.");
            } else {
                jsonObject.put("message",
                        "Please load a source before attempting to pause or resume playback.");
                return jsonObject;
            }

        } else if (pauseOrNot == 0) {
            if (this.getName() == null) {
                this.setName("");
            }
            if (this.getRemainedTime() == null) {
                this.setRemainedTime(0);
            }
            if (this.getRepeat() == null) {
                this.setRepeat("No Repeat");
            }
            if (this.getPaused() == null) {
                this.setPaused(true);
            }

            smallJsonObject.put("name", this.getName());


            smallJsonObject.put("remainedTime", this.getRemainedTime());
            smallJsonObject.put("repeat", this.getRepeat());

            if (Objects.equals(super.getType(), "playlist")
                    && !this.getName().isEmpty()) {

                Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
                assert playlist != null;

                if (user.getStatusshuffle() != null) {
                    smallJsonObject.put("shuffle", user.getStatusshuffle());
                } else {
                    smallJsonObject.put("shuffle", false);
                }
            } else if (Objects.equals(super.getType(), "album")
                    && !this.getName().isEmpty()) {

                Album album = user.getUserDatabase().getLoadedAlbum();
                assert album != null;

                if (album.getStatusshuffle() != null) {
                    smallJsonObject.put("shuffle", album.getStatusshuffle());
                } else {
                    smallJsonObject.put("shuffle", false);
                }

            } else {

                if (this.getShuffle() != null) {
                    smallJsonObject.put("shuffle", this.getShuffle());
                } else {
                    smallJsonObject.put("shuffle", false);

                }
            }
            smallJsonObject.put("paused", this.getPaused());
            jsonObject.set("stats", smallJsonObject);
        }
        if (playerLoad != null) {
            playerLoad.setPaused(this.getPaused());
            user.getUserDatabase().setStatus(this);
        }


        return jsonObject;
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    private void setStatus(final playerLoad playerLoad2) {
        this.setName(playerLoad2.getName());
        this.setAlbum(playerLoad2.getAlbum());
        this.setGenre(playerLoad2.getGenre());
        this.setRemainedTime(playerLoad2.getRemainedTime());
        this.setShuffle(playerLoad2.getShuffle());
        this.setRepeat(playerLoad2.getRepeat());
        this.setPaused(playerLoad2.getPaused());
    }

    private void negativeRemaininingTimeForStatusJson(final playerLoad
        playerLoad, final User user) {
        int newI = playerLoad.getRemainedTime();
        if (Objects.equals(super.getType(), "song")) {
            this.setRemainedTime(newI);
            List<Song> listsong = Library.getDatabase().getSongs();
            Song song = listsong.get(Library.
                    searchSongByNameAndAlbum(playerLoad.getName(), playerLoad.getAlbum()));
            assert song != null;
            NegativeTimeStampVisitor visitor = new NegativeTimeStampVisitor();
            playerLoad playerLoad2 = song.
                    acceptNegativeRemaininingTimeForStatusJson(visitor, playerLoad, user, newI);

            if (playerLoad2 != null) {
                setStatus(playerLoad2);
            }

        } else if (Objects.equals(super.getType(), "podcast")) {
            Podcast podcast = Library.
                    podcastByName(user.getUserDatabase().getLoadedElement());
            assert podcast != null;
            NegativeTimeStampVisitor visitor = new NegativeTimeStampVisitor();
            playerLoad playerLoad2 = podcast.
                    acceptNegativeRemaininingTimeForStatusJson(visitor, playerLoad, user, newI);

            if (playerLoad2 != null) {
                setStatus(playerLoad2);
            }


        } else if (Objects.equals(super.getType(), "playlist")) {

            Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
            assert playlist != null;
            NegativeTimeStampVisitor visitor = new NegativeTimeStampVisitor();
            playerLoad playerLoad2 = playlist.
                    acceptNegativeRemaininingTimeForStatusJson(visitor, playerLoad, user, newI);

            if (playerLoad2 != null) {
                setStatus(playerLoad2);
            }

        } else if (Objects.equals(super.getType(), "album")) {

            Album album = user.getUserDatabase().getLoadedAlbum();
            NegativeTimeStampVisitor visitor = new NegativeTimeStampVisitor();
            playerLoad playerLoad2 = album.
                    acceptNegativeRemaininingTimeForStatusJson(visitor, playerLoad, user, newI);

            if (playerLoad2 != null) {
                setStatus(playerLoad2);
            }
        }
    }

    /**
     * checks if the CONDITION for NO REPEAT
     * FOR END OF PLAYLIST
     *
     * @param newI the current timestamp difference
     *             if it is negative
     *             and the user.getRepeat() == 0
     * @param user the current user
     */
    public static boolean isTheEndOfPlaylistNoRepeat(final int newI, final boolean whileLoop,
                                                     final User user) {
        return (newI <= 0 || !whileLoop) && user.getRepeat() == 0;
    }

    /**
     * checks if the CONDITION for NO REPEAT
     * FOR END OF PLAYLIST
     *
     * @param newI the current timestamp difference
     *             if it is negative
     *             and the user.getRepeat() == 0
     * @param user the current user
     */
    public static void checkForEndOfPlaylist2NoRepeat(final int newI, final playerLoad playerLoad,
                                                      final User user, final boolean whileLoop) {
        int newI2 = newI;
        if (!whileLoop || newI2 <= 0) {

            if (newI2 <= 0) {
                user.getUserDatabase().setShufflestatus(false);

                user.getUserDatabase().setLoadStatus(false);

                user.getUserDatabase().setSelectstatus(false);
                playerLoad.setRemainedTime(0);
                playerLoad.setPaused(true);
                playerLoad.setName("");
                playerLoad.setAlbum("");
                playerLoad.setGenre("");
                user.getUserDatabase().setAccesedUser("", user);
                playerLoad.setAlbum("");
                playerLoad.setGenre("");
            }
        }
    }


}

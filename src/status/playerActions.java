package status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;
import types.*;

import java.util.Objects;

public class playerActions {
    private static final int MOMENT = 90;

    /**
     * in the pause function we put information about the state of
     * pause, inn main we implement the ca status function
     * to synchronize the timestamp with the current song, and after
     * you give it a pause, and in the search function in the hand and then
     * give it a pause
     * with the current song that corresponds to the current song
     */
    public JsonNode pauseJson(final Boolean b) {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();

        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        playerLoad playerLoad = user.getUserDatabase().getStatus();
        if (Objects.equals(playerLoad.getName(), "") || !user.getUserDatabase().getLoadStatus()) {
            jsonObject.put("message",
                    "Please load a source before attempting to pause or resume playback.");
            return jsonObject;
        }
        if (b) {
            jsonObject.put("message", "Playback paused successfully.");
        } else {
            jsonObject.put("message", "Playback resumed successfully.");
        }
        return jsonObject;
    }

    private String nextPodcastJson(final Podcast podcast, final
    User user, final playerLoad playerLoad) {
        assert podcast != null;

        int size = user.getLoadedEpisode() + 1;
        if (size >= podcast.getEpisodes().size()) {
            if (user.getRepeat() != 0) {
                if (user.getRepeat() == 1) {
                    user.setRepeat(0);
                }
                playerLoad.setPaused(false);
                int durationEpisode = podcast.getEpisodes().
                        get(user.getLoadedEpisode()).getDuration();
                podcast.getEpisodes().get(user.getLoadedEpisode()).
                        setRemainingduration(durationEpisode);
                user.setLoadedEpisode(0);
                podcast.getEpisodes().get(user.getLoadedEpisode()).
                        setRemainingduration(durationEpisode);
            } else {
                playerLoad.setPaused(true);
                user.getUserDatabase().setSelectstatus(false);
                playerLoad.setName("");
                playerLoad.setAlbum("");
            }


        } else {
            playerLoad.setPaused(false);
            int numberEpisode = user.getLoadedEpisode();
            int durationEpisode = podcast.getEpisodes().
                    get(numberEpisode).getDuration();

            podcast.getEpisodes().get(numberEpisode).setRemainingduration(durationEpisode);
            user.setLoadedEpisode(size);
            numberEpisode = user.getLoadedEpisode();
            durationEpisode = podcast.getEpisodes().get(numberEpisode).getDuration();
            podcast.getEpisodes().get(numberEpisode).setRemainingduration(durationEpisode);
        }
        return "Skipped to next track successfully. The current track is "
                + podcast.getEpisodes().get(user.getLoadedEpisode()).getName() + ".";
    }

    private JsonNode nextPlaylistActionJson(final User user, final
    playerLoad playerLoad, ObjectNode jsonObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
        ArrayNode outputs2 = objectMapper.createArrayNode();
        int index;
        for (String song : playlist.getSongList()) {
            outputs2.add(song);
        }

        if (user.getRepeat() != 2) {
            user.setSongIndex(user.getSongIndex() + 1);
        }
        if (user.getRepeat() == 0 && ((user.getSongIndex() >= (playlist.getSongList().size()))
                || playlist.getSongList().isEmpty())) {
            user.getUserDatabase().setShufflestatus(false);

            playerLoad.setRemainedTime(0);
            playerLoad.setPaused(true);
            user.getUserDatabase().setSelectstatus(false);
            playerLoad.setName("");
            playerLoad.setAlbum("");

            jsonObject.put("message", "Please load a source before"
                    + " skipping to the next track.");
            return jsonObject;
        }

        ArrayNode outputs4 = objectMapper.createArrayNode();
        for (String song : playlist.getSongList()) {
            outputs4.add(song);
        }
        playerLoad.setPaused(false);

        if (user.getSongIndex()
                >= (playlist.getSongList().size())
                && user.getRepeat() != 2) {
            if (user.getRepeat() == 1) {
                user.setSongIndex(0);

                if (!user.getStatusshuffle()) {
                    user.setSongindexrandom(user.getSongIndex());
                    index = Library.searchSongByName(playlist.getSongList().
                            get(user.getSongIndex()));
                } else {
                    user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
                    index = Library.searchSongByName(playlist.getSongList().
                            get(playlist.getnewRandomSongIndex(user)));
                }
                playerLoad.setName(Library.getDatabase().getSongs().
                        get(index).getName());
                playerLoad.setAlbum(Library.getDatabase().getSongs().
                        get(index).getAlbum());
                playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                        get(index).getDuration());

            } else if (user.getRepeat() == 0) {
                user.getUserDatabase().setShufflestatus(false);

                playerLoad.setRemainedTime(0);
                playerLoad.setPaused(true);
                user.getUserDatabase().setSelectstatus(false);
                playerLoad.setName("");
                playerLoad.setAlbum("");
                jsonObject.put("message", "Please load a source before "
                        + "skipping to the next track.");
                return jsonObject;
            }
        } else {
            int index2 = 0;
            if (user.getRepeat() == 2) {
                if (!user.getStatusshuffle()) {
                    user.setSongindexrandom(user.getSongIndex());
                    index2 = Library.searchSongByName(playlist.getSongList().
                            get(user.getSongIndex()));
                } else {
                    user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
                    index2 = Library.searchSongByName(playlist.getSongList().
                            get(playlist.getnewRandomSongIndex(user)));

                }
                playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                        get(index2).getDuration());
            } else {
                if (!user.getStatusshuffle()) {
                    user.setSongindexrandom(user.getSongIndex());
                    index = Library.searchSongByName(playlist.getSongList().
                            get(user.getSongIndex()));
                } else {
                    user.setSongindexrandom(playlist.
                            getnewRandomSongIndex(user));
                    index = Library.searchSongByName(playlist.getSongList().
                            get(playlist.getnewRandomSongIndex(user)));

                }
                playerLoad.setName(Library.getDatabase().getSongs().
                        get(index).getName());
                playerLoad.setAlbum(Library.getDatabase().getSongs().
                        get(index).getAlbum());

                playerLoad.setRemainedTime(Library.getDatabase().
                        getSongs().get(index).getDuration());
            }

        }
        jsonObject.put("message", "Skipped to next track successfully. "
                + "The current track is " + playerLoad.getName() + ".");
        return jsonObject;
    }
    /**
     * The next class, we do it to access the next one
     * and before doing that we try
     * to synchronize with the timestamp, and after that the next song,
     * we check if the name of the element is "empty",
     * then it is finished, or if the loaded status is false,
     * or if there is no error, and we proceed as usual,
     * when our value of the value becomes positive
     */
    public JsonNode nextJson() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();

        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        Boolean loadStatus = user.getUserDatabase().getLoadStatus();
        String loadedElement = user.getUserDatabase().getLoadedElement();
        String nameStatus = user.getUserDatabase().
                getStatus().getName();


        if (!loadStatus || loadedElement.isEmpty()
                || nameStatus.isEmpty()) {

            jsonObject.put("message", "Please load a source before skipping "
                    + "to the next track.");
            return jsonObject;
        }
        playerLoad playerLoad = user.getUserDatabase().getStatus();


        int index;
        if (user.getUserDatabase().getStatus().getType().equals("podcast")) {

            Podcast podcast = Library.podcastByName(user.
                    getUserDatabase().getLoadedElement());
            String name = nextPodcastJson(podcast, user, playerLoad);
            jsonObject.put("message", name);

        } else if (user.getUserDatabase().getStatus().getType().equals("playlist")) {
            return nextPlaylistActionJson(user, playerLoad, jsonObject);
        } else if (user.getUserDatabase().getStatus().getType().equals("album")) {

            Album album = user.getUserDatabase().getLoadedAlbum();
            ArrayNode outputs2 = objectMapper.createArrayNode();
            for (Song song : album.getSongs()) {
                outputs2.add(song.getName());
            }
            if (user.getRepeat() != 2) {
                user.setSongIndex(user.getSongIndex() + 1);
            }

            if (user.getRepeat() == 0 && ((user.getSongIndex()
                    >= (album.getSongs().size())) || album.getSongs().isEmpty())) {
                user.getUserDatabase().setShufflestatus(false);

                playerLoad.setRemainedTime(0);
                playerLoad.setPaused(true);
                user.getUserDatabase().setSelectstatus(false);
                playerLoad.setName("");
                playerLoad.setAlbum("");

                jsonObject.put("message", "Please load a source before"
                        + " skipping to the next track.");
                return jsonObject;
            }

            ArrayNode outputs4 = objectMapper.createArrayNode();
            for (Song song : album.getSongs()) {
                outputs4.add(song.getName());
            }
            playerLoad.setPaused(false);

            if (user.getSongIndex() >= (album.getSongs().size())
                    && user.getRepeat() != 2) {
                if (user.getRepeat() == 1) {
                    user.setSongIndex(0);

                    if (!album.getStatusshuffle()) {
                        user.setSongindexrandom(user.getSongIndex());
                        index = Library.searchSongByNameAndAlbum(album.getSongs().
                                get(user.getSongIndex()).getName(), album.getName());
                    } else {
                        user.setSongindexrandom(album.getnewRandomSongIndex(user));
                        index = Library.searchSongByNameAndAlbum(album.getSongs().
                                get(album.getnewRandomSongIndex(user)).getName(), album.getName());
                    }
                    playerLoad.setName(Library.getDatabase().getSongs().
                            get(index).getName());
                    playerLoad.setAlbum(Library.getDatabase().getSongs().
                            get(index).getAlbum());
                    playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                            get(index).getDuration());

                } else if (user.getRepeat() == 0) {
                    user.getUserDatabase().setShufflestatus(false);

                    playerLoad.setRemainedTime(0);
                    playerLoad.setPaused(true);
                    user.getUserDatabase().setSelectstatus(false);
                    playerLoad.setName("");
                    playerLoad.setAlbum("");
                    jsonObject.put("message", "Please load a source before "
                            + "skipping to the next track.");
                    return jsonObject;
                }
            } else {
                int index2 = 0;
                if (user.getRepeat() == 2) {
                    if (!album.getStatusshuffle()) {
                        user.setSongindexrandom(user.getSongIndex());
                        index2 = Library.searchSongByName(album.getSongs().
                                get(user.getSongIndex()).getName());
                    } else {
                        user.setSongindexrandom(album.getnewRandomSongIndex(user));
                        index2 = Library.searchSongByName(album.getSongs().
                                get(album.getnewRandomSongIndex(user)).getName());

                    }
                    playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                            get(index2).getDuration());
                } else {
                    if (!album.getStatusshuffle()) {
                        user.setSongindexrandom(user.getSongIndex());
                        String songName = album.getSongs().get(user.getSongIndex()).getName();
                        String albumName = album.getSongs().get(user.getSongIndex()).getAlbum();
                        index = Library.searchSongByNameAndAlbum(songName, albumName);
                    } else {
                        user.setSongindexrandom(album.
                                getnewRandomSongIndex(user));
                        String songName = album.getSongs().
                                get(album.getnewRandomSongIndex(user)).getName();
                        String albumName = album.getSongs().
                                get(album.getnewRandomSongIndex(user)).getAlbum();
                        index = Library.searchSongByNameAndAlbum(songName, albumName);

                    }
                    playerLoad.setName(Library.getDatabase().getSongs().
                            get(index).getName());
                    playerLoad.setAlbum(Library.getDatabase().getSongs().
                            get(index).getAlbum());

                    playerLoad.setRemainedTime(Library.getDatabase().
                            getSongs().get(index).getDuration());
                }

            }
            jsonObject.put("message", "Skipped to next track successfully. "
                    + "The current track is " + playerLoad.getName() + ".");

        }

        return jsonObject;
    }

    /**
     Within the prev function, we check the same conditions
     * as in prev, but we check if the full duration is the duration of the song
     * if it's we go to the previous song, if
     * It's not we go to the beginning of our song without
     * we give to the previous song
     * returns previous element
     *
     * @return json
     */
    public JsonNode prevJson() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        Boolean loadStatus = user.getUserDatabase().getLoadStatus();
        String loadedElement = user.getUserDatabase().getLoadedElement();
        String nameStatus = user.getUserDatabase().
                getStatus().getName();

        if (!loadStatus || loadedElement.isEmpty() || nameStatus.isEmpty()) {

            jsonObject.put("message",
                    "Please load a source before returning to the previous track.");
            return jsonObject;
        }
        playerLoad playerLoad = user.getUserDatabase().getStatus();
        int index;
        if (user.getUserDatabase().getStatus().getType().equals("podcast")) {

            Podcast podcast = Library.podcastByName(user.getUserDatabase().
                    getLoadedElement());
            assert podcast != null;
            int originalDimension = podcast.getEpisodes().get(user.
                    getLoadedEpisode()).getRemainingduration();
            if (originalDimension == podcast.getEpisodes().get(user.
                    getLoadedEpisode()).getDuration()) {
                int size = user.getLoadedEpisode() - 1;
                if (size <= 0) {
                    int numberEpisode = user.getLoadedEpisode();
                    int durationEpisode = podcast.getEpisodes().
                            get(numberEpisode).getDuration();

                    podcast.getEpisodes().get(numberEpisode).
                            setRemainingduration(durationEpisode);
                    user.setLoadedEpisode(0);
                    podcast.getEpisodes().get(numberEpisode).
                            setRemainingduration(durationEpisode);

                } else {
                    int numberEpisode = user.getLoadedEpisode();
                    int durationEpisode = podcast.getEpisodes().
                            get(numberEpisode).getDuration();

                    podcast.getEpisodes().get(numberEpisode).
                            setRemainingduration(durationEpisode);
                    user.setLoadedEpisode(size);
                    numberEpisode = user.getLoadedEpisode();
                    durationEpisode = podcast.getEpisodes().
                            get(numberEpisode).getDuration();
                    podcast.getEpisodes().get(numberEpisode).
                            setRemainingduration(durationEpisode);
                }
            } else {
                int numberEpisode = user.getLoadedEpisode();
                int durationEpisode = podcast.getEpisodes().
                        get(numberEpisode).getDuration();
                podcast.getEpisodes().get(numberEpisode).
                        setRemainingduration(durationEpisode);
            }

            jsonObject.put("message",
                    "Returned to previous track successfully. The current track is "
                            + podcast.getEpisodes().
                            get(user.getLoadedEpisode()).getName() + ".");


        } else if (user.getUserDatabase().getStatus().getType().equals("playlist")) {
            Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
            assert playlist != null;
            if (!user.getStatusshuffle()) {
                user.setSongindexrandom(user.getSongIndex());
                index = Library.searchSongByName(playlist.getSongList().
                        get(user.getSongIndex()));
            } else {
                user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
                index = Library.searchSongByName(playlist.getSongList().
                        get(playlist.getnewRandomSongIndex(user)));
            }
            int originalDimension = Library.getDatabase().getSongs().
                    get(index).getDuration();
            playerLoad.setPaused(false);

            if (originalDimension == playerLoad.getRemainedTime()) {
                if (user.getSongIndex() - 1 < 0) {
                    user.setSongIndex(0);
                    index = 0;
                } else {
                    user.setSongIndex(user.getSongIndex() - 1);
                    if (!user.getStatusshuffle()) {
                        user.setSongindexrandom(user.getSongIndex());
                        index = Library.searchSongByName(playlist.getSongList().
                                get(user.getSongIndex()));
                    } else {
                        user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
                        index = Library.searchSongByName(playlist.getSongList().
                                get(playlist.getnewRandomSongIndex(user)));
                    }
                }
                if (!user.getStatusshuffle()) {
                    user.setSongindexrandom(user.getSongIndex());
                    index = Library.searchSongByName(playlist.getSongList().
                            get(user.getSongIndex()));
                } else {
                    user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
                    index = Library.searchSongByName(playlist.getSongList().
                            get(playlist.getnewRandomSongIndex(user)));
                }

                playerLoad.setName(Library.getDatabase().getSongs().
                        get(index).getName());
                playerLoad.setAlbum(Library.getDatabase().getSongs().
                        get(index).getAlbum());
                playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                        get(index).getDuration());

            } else {
                playerLoad.setRemainedTime(originalDimension);
            }
            jsonObject.put("message",
                    "Returned to previous track successfully. The current track is "
                            + playerLoad.getName() + ".");

            return jsonObject;
        } else if (user.getUserDatabase().getStatus().getType().equals("album")) {
            Album album = user.getUserDatabase().getLoadedAlbum();
            assert album != null;
            if (!album.getStatusshuffle()) {
                user.setSongindexrandom(user.getSongIndex());
                index = Library.searchSongByName(album.getSongs().
                        get(user.getSongIndex()).getName());
            } else {
                user.setSongindexrandom(album.getnewRandomSongIndex(user));
                index = Library.searchSongByName(album.getSongs().
                        get(album.getnewRandomSongIndex(user)).getName());
            }
            int originalDimension = Library.getDatabase().getSongs().
                    get(index).getDuration();
            playerLoad.setPaused(false);
            if (originalDimension == playerLoad.getRemainedTime()) {
                if (user.getSongIndex() - 1 < 0) {
                    user.setSongIndex(0);
                    index = 0;
                } else {
                    if (user.getRepeat() != 2) {
                        user.setSongIndex(user.getSongIndex() - 1);
                    }

                    if (!album.getStatusshuffle()) {
                        user.setSongindexrandom(user.getSongIndex());
                        index = Library.searchSongByName(album.getSongs().
                                get(user.getSongIndex()).getName());
                    } else {
                        user.setSongindexrandom(album.getnewRandomSongIndex(user));
                        index = Library.searchSongByName(album.getSongs().
                                get(album.getnewRandomSongIndex(user)).getName());
                    }
                }
                if (!album.getStatusshuffle()) {
                    user.setSongindexrandom(user.getSongIndex());
                    index = Library.searchSongByName(album.getSongs().
                            get(user.getSongIndex()).getName());
                } else {
                    user.setSongindexrandom(album.getnewRandomSongIndex(user));
                    index = Library.searchSongByName(album.getSongs().
                            get(album.getnewRandomSongIndex(user)).getName());
                }

                playerLoad.setName(Library.getDatabase().getSongs().
                        get(index).getName());
                playerLoad.setAlbum(Library.getDatabase().getSongs().
                        get(index).getAlbum());
                playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                        get(index).getDuration());

            } else {
                playerLoad.setRemainedTime(originalDimension);
            }
            jsonObject.put("message",
                    "Returned to previous track successfully. The current track is "
                            + playerLoad.getName() + ".");

            return jsonObject;
        }
        return jsonObject;
    }

    /**
     * we implement the souffle function, in case
     * before it was false and negative, we update the new index of the song,
     * with
     * the one for our random list (Song_index),
     * if it is positive, we synchronize the elements
     * with the timestamp, and we apply the cases of
     * the error, and we set the Song_index index
     * @param prevTimestamp previous timestamp
     * @return json
     */
    public JsonNode shuffleJson(final int prevTimestamp) {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        playerLoad playerLoad2 = user.getUserDatabase().getStatus();

        if (Objects.equals(playerLoad2.getName(), "")) {
            jsonObject.put("message",
                    "Please load a source before using the shuffle function.");
            return jsonObject;
        }
        if (!user.getUserDatabase().getStatus().getType().equals("playlist")
                && !user.getUserDatabase().getStatus().getType().equals("album")) {
            jsonObject.put("message",
                    "The loaded source is not a playlist or an album.");
            return jsonObject;
        }

        if (user.getUserDatabase().getStatus().getType().equals("album")) {
            Album album = user.getUserDatabase().getLoadedAlbum();
            if (album.getSeed() == 0 || jsonElement.getSeed() != 0) {
                album.setSeed(jsonElement.getSeed());
            }

            if (!album.getStatusshuffle()) {
                album.makeShuffleList();
                album.showPlaylist();
                user.setSongindexrandom(user.getSongIndex());

                album.setStatusshuffle(true);
                user.setSongIndex(album.searchRandomIndex(user.getSongIndex()));
                jsonObject.put("message", "Shuffle function activated successfully.");

            } else {
                int i, newI;
                playerLoad playerLoad = user.getUserDatabase().getStatus();

                i = playerLoad.getRemainedTime();
                if (!playerLoad.getPaused()) {

                    newI = i - (jsonElement.getTimestamp() - prevTimestamp);
                    int currentIndex = user.getSongIndex();

                    while (newI < 0 && currentIndex < album.getSongList().size() - 1) {
                        if (user.getRepeat() != 2) {
                            currentIndex += 1;
                            user.setSongIndex(currentIndex);
                        }
                        int index;

                        user.setSongindexrandom(album.
                                getnewRandomSongIndex(user));

                        index = Library.searchSongByNameAndAlbum(album.
                                getSongList().get(album.
                                        getnewRandomSongIndex(user)), album.getName());

                        newI += Library.getDatabase().getSongs().get(index).
                                getDuration();
                    }
                    user.setSongindexrandom(album.getnewRandomSongIndex(user));
                    if (currentIndex >= album.getSongs().size() - 1 && newI < 0) {
                        if (user.getRepeat() == 0) {
                            playerLoad.setName("");
                            playerLoad.setAlbum("");
                            playerLoad.setRemainedTime(0);
                            playerLoad.setPaused(true);
                            album.setStatusshuffle(false);
                            jsonObject.put("message",
                                    "Please load a source before using the shuffle function.");
                            return jsonObject;
                        }

                    }
                    user.setSongIndex(user.getSongindexrandom());
                    playerLoad.setName(album.getSongList().get(user.getSongindexrandom()));
                    playerLoad.setAlbum(album.getAlbumList().get(user.getSongindexrandom()));
                    playerLoad.setRemainedTime(newI);
                } else {
                    user.setSongindexrandom(album.getnewRandomSongIndex(user));
                    user.setSongIndex(user.getSongindexrandom());

                }

                album.setStatusshuffle(false);
                jsonObject.put("message", "Shuffle function deactivated successfully.");
                user.getUserDatabase().setShufflestatus(false);

            }
            return jsonObject;

        }

        Playlist playlist = user.getUserDatabase().getLoadedPlaylist();
        if (playlist.getSeed() == 0 || jsonElement.getSeed() != 0) {
            playlist.setSeed(jsonElement.getSeed());
        }

        if (!user.getStatusshuffle()) {
            playlist.makeShuffleList();
            playlist.showPlaylist();
            user.setSongindexrandom(user.getSongIndex());

            user.setStatusshuffle(true);
            user.setSongIndex(playlist.searchRandomIndex(user.getSongIndex()));
            jsonObject.put("message", "Shuffle function activated successfully.");
        } else {
            int i, newI;
            playerLoad playerLoad = user.getUserDatabase().getStatus();

            i = playerLoad.getRemainedTime();
            if (!playerLoad.getPaused()) {
                newI = i - (jsonElement.getTimestamp() - prevTimestamp);
                int currentIndex = user.getSongIndex();
                while (newI < 0 && currentIndex < playlist.getSongList().size() - 1) {
                    if (user.getRepeat() != 2) {
                        currentIndex += 1;
                        user.setSongIndex(currentIndex);
                    }
                    int index;

                    user.setSongindexrandom(playlist.
                            getnewRandomSongIndex(user));

                    index = Library.searchSongByName(playlist.
                            getSongList().get(playlist.
                                    getnewRandomSongIndex(user)));

                    newI += Library.getDatabase().getSongs().get(index).
                            getDuration();
                }
                user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
                if (currentIndex >= playlist.getSongList().size() - 1 && newI < 0) {
                    if (user.getRepeat() == 0) {
                        playerLoad.setName("");
                        playerLoad.setAlbum("");
                        playerLoad.setRemainedTime(0);
                        playerLoad.setPaused(true);
                        user.setStatusshuffle(false);
                        jsonObject.put("message",
                                "Please load a source before using the shuffle function.");
                        return jsonObject;
                    }

                }
                user.setSongIndex(user.getSongindexrandom());
                playerLoad.setName(playlist.getSongList().get(user.getSongindexrandom()));
                playerLoad.setAlbum(playlist.getAlbumList().get(user.getSongindexrandom()));
                playerLoad.setRemainedTime(newI);
            } else {
                user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
                user.setSongIndex(user.getSongindexrandom());

            }

            user.setStatusshuffle(false);
            jsonObject.put("message", "Shuffle function deactivated successfully.");
            user.getUserDatabase().setShufflestatus(false);
        }
        return jsonObject;
    }


    /**
     * we are trying to implement our function, and we are trying
     * let's first see like status or load status and if
     * are not good
     * then it gives error 1, then we try to give
     * we check if the element is loaded and selected
     * empty then we give error 2
     * if we pass these, we go backwards 90 seconds
     * (if the remaining time is short, we go to the
     * previous song)
     *
     * @return json
     */
    public JsonNode backwardJson() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (!user.getUserDatabase().getLikestatus()
                || user.getUserDatabase().getLoadStatus() == null) {

            jsonObject.put("message",
                    "Please load a source before rewounding.");
            return jsonObject;
        }
        if (!user.getUserDatabase().getSelectStatus()
                && user.getUserDatabase().getLoadedElement().isEmpty()) {
            jsonObject.put("message",
                    "Please select a source before rewounding.");
            return jsonObject;
        }
        if (!user.getUserDatabase().getType().
                equals("podcast")) {
            jsonObject.put("message",
                    "The loaded source is not a podcast.");
            return jsonObject;
        }
        Podcast podcast = Library.podcastByName(user.
                getUserDatabase().getLoadedElement());
        assert podcast != null;
        int originalDimension = podcast.getEpisodes().
                get(user.getLoadedEpisode()).getRemainingduration();
        int newDimension = originalDimension + MOMENT;
        if (newDimension > podcast.getEpisodes().get(user.
                getLoadedEpisode()).getDuration()) {
            int beginning = podcast.getEpisodes().get(user.
                    getLoadedEpisode()).getDuration();
            podcast.getEpisodes().get(user.getLoadedEpisode()).
                    setRemainingduration(beginning);
        } else {
            podcast.getEpisodes().get(user.getLoadedEpisode()).
                    setRemainingduration(podcast.getEpisodes().get(user.
                            getLoadedEpisode()).getRemainingduration() + MOMENT);

        }

        jsonObject.put("message", "Rewound successfully.");
        return jsonObject;
    }

    /**
     /**
     * we try to implement our function, and we try
     * Let's first see like status or load status and if
     * are not good
     * then give error 1, after we try to give
     * check if the element loaded and selected are
     * empty then we give error 2
     * If we pass these, we go 90 seconds after
     * (if the remaining time is a little longer, we go to the next song)
     * @return json
     */

    public JsonNode forwardJson() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        playerLoad playerLoad2 = user.getUserDatabase().getStatus();

        if (!user.getUserDatabase().getLikestatus()
                || user.getUserDatabase().getLoadStatus() == null || playerLoad2.getName() == "") {


            jsonObject.put("message",
                    "Please load a source before attempting "
                            + "to forward.");
            return jsonObject;
        }
        if (!user.getUserDatabase().getSelectStatus()
                && user.getUserDatabase().getLoadedElement().
                isEmpty()) {
            jsonObject.put("message",
                    "Please select a source before"
                            + " attempting to forward.");
            return jsonObject;
        }
        if (!user.getUserDatabase().getStatus().getType().
                equals("podcast")) {
            jsonObject.put("message",
                    "The loaded source is not a podcast.");
            return jsonObject;
        }
        Podcast podcast = Library.podcastByName(user.
                getUserDatabase().getLoadedElement());
        assert podcast != null;
        int originalDimension = podcast.getEpisodes().
                get(user.getLoadedEpisode()).getRemainingduration();
        int newDimension = originalDimension - MOMENT;
        if (newDimension < 0) {
            int size = user.getLoadedEpisode() + 1;
            if (size >= podcast.getEpisodes().size()) {
                if (user.getRepeat() != 0) {
                    if (user.getRepeat() == 1) {
                        user.setRepeat(0);
                    }
                    podcast.getEpisodes().get(user.
                                    getLoadedEpisode()).
                            setRemainingduration(podcast.getEpisodes().
                                    get(user.getLoadedEpisode()).
                                    getDuration());
                    user.setLoadedEpisode(0);
                    podcast.getEpisodes().get(user.
                                    getLoadedEpisode()).
                            setRemainingduration(podcast.
                                    getEpisodes().get(user.
                                            getLoadedEpisode()).
                                    getDuration());
                } else {
                    playerLoad playerLoad = user.getUserDatabase().
                            getStatus();
                    playerLoad.setPaused(true);
                    user.getUserDatabase().setSelectstatus(false);
                    playerLoad.setName("");
                    playerLoad.setAlbum("");
                }


            } else {
                podcast.getEpisodes().get(user.getLoadedEpisode()).
                        setRemainingduration(podcast.getEpisodes().
                                get(user.getLoadedEpisode()).
                                getDuration());
                user.setLoadedEpisode(size);
                podcast.getEpisodes().get(user.getLoadedEpisode()).
                        setRemainingduration(podcast.getEpisodes().
                                get(user.getLoadedEpisode()).
                                getDuration());
            }

        } else {
            podcast.getEpisodes().get(user.getLoadedEpisode()).
                    setRemainingduration(podcast.getEpisodes().
                            get(user.getLoadedEpisode()).
                            getRemainingduration() - MOMENT);

        }

        jsonObject.put("message",
                "Skipped forward successfully.");
        return jsonObject;
    }
    /**
     * in this function we set the repeat status according to
     * the current repeat and the element type
     * if it is a song, playlist or podcast or album, and we post
     * the appropriate message.
     * @return json
     */
    public JsonNode repeatJson() {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());
        jsonObject.put("command", jsonElement.
                getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        playerLoad playerLoad = user.getUserDatabase().getStatus();
        if (!user.getUserDatabase().getLikestatus()
                || user.getUserDatabase().getLoadStatus() == null
                || playerLoad.getRemainedTime() == 0) {
            user.getUserDatabase().setLoadedElement("");

            jsonObject.put("message", "Please load a source before setting the repeat status.");
            return jsonObject;
        }
        switch (user.getUserDatabase().getStatus().getType()) {
            case "playlist" -> {

                Playlist playlist = user.getUserDatabase().
                        selectPlaylistByName(user.getUserDatabase().getLoadedElement());
                assert playlist != null;
                user.setRepeat((user.getRepeat() + 1));
                if (user.getRepeat() > 2) {
                    user.setRepeat(0);
                }

                if (user.getRepeat() == 0) {
                    jsonObject.put("message", "Repeat mode changed to no repeat.");
                } else if (user.getRepeat() == 1) {
                    jsonObject.put("message", "Repeat mode changed to repeat all.");
                } else if (user.getRepeat() == 2) {
                    jsonObject.put("message", "Repeat mode changed to repeat current song.");
                }
            }
            case "podcast" -> {
                Podcast podcast = Library.podcastByName(user.getUserDatabase().getLoadedElement());
                assert podcast != null;
                user.setRepeat((user.getRepeat() + 1));
                if (user.getRepeat() > 2) {
                    user.setRepeat(0);
                }
                if (user.getRepeat() == 0) {
                    jsonObject.put("message", "Repeat mode changed to no repeat.");
                } else if (user.getRepeat() == 1) {
                    jsonObject.put("message", "Repeat mode changed to repeat once.");
                } else if (user.getRepeat() == 2) {
                    jsonObject.put("message", "Repeat mode changed to repeat infinite.");
                }
            }
            case "song" -> {
                Song song = Library.getDatabase().getSongs().
                        get(Library.searchSongByName(user.getUserDatabase().getLoadedElement()));
                assert song != null;
                user.setRepeat((user.getRepeat() + 1));
                if (user.getRepeat() > 2) {
                    user.setRepeat(0);
                }
                if (user.getRepeat() == 0) {
                    jsonObject.put("message", "Repeat mode changed to no repeat.");
                } else if (user.getRepeat() == 1) {
                    jsonObject.put("message", "Repeat mode changed to repeat once.");
                } else if (user.getRepeat() == 2) {
                    jsonObject.put("message", "Repeat mode changed to repeat infinite.");
                }
            }
            default -> {
                break;
            }
        }

        return jsonObject;
    }
}

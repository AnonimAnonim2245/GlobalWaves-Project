package visitor;

import status.playerLoad;
import types.Album;
import types.Podcast;
import types.Song;
import types.User;
import types.Library;
import types.Playlist;
import status.playerStatus;

/**
 * visitor class for status json with negative time remaining
 */
public class NegativeTimeStampVisitor implements Visitor {
    /**
     * visit function for end of album with repeat all
     *
     * @param playlist   the playlist and the song which is loaded that needs to be updated
     * @param playerLoad the current load status of the function, we updated the remaining time,
     *                   *             or whether our current
     *                   song/podcast/playlist/album is loaded anymore
     * @param user       user the current user
     * @return the new timestamp also newI
     */
    public int visitEndForRepeatAll(final Playlist playlist,
                                    final playerLoad playerLoad, final User user) {
        int newI = playerLoad.getRemainedTime();
        if (user.getRepeat() == 1 && (user.getSongIndex() == (playlist.getSongList().size() - 1))) {
            user.setSongIndex(0);
            int index2;
            if (Boolean.FALSE.equals(user.getStatusshuffle())) {
                user.setSongindexrandom(user.getSongIndex());
                String selectedSong = playlist.getSongList().
                        get(user.getSongIndex());
                String selectedAlbum = playlist.getAlbumList().
                        get(user.getSongIndex());
                index2 = Library.searchSongByNameAndAlbum(selectedSong,
                        selectedAlbum);
            } else {
                user.setSongindexrandom(playlist.getnewRandomSongIndex(user));
                String selectedSong = playlist.getSongList().
                        get(playlist.getnewRandomSongIndex(user));
                String selectedAlbum = playlist.getAlbumList().
                        get(playlist.getnewSongIndex(user));
                index2 = Library.searchSongByNameAndAlbum(selectedSong,
                        selectedAlbum);
            }
            if (!Library.getDatabase().getSongs().get(index2).getName().equals("Ad Break")) {
                user.listenToSong(Library.getDatabase().getSongs().get(index2));
            }
            playerLoad.setName(Library.getDatabase().
                    getSongs().get(index2).getName());
            playerLoad.setAlbum(Library.getDatabase().
                    getSongs().get(index2).getAlbum());
            playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                    get(index2).getDuration() + newI);

            newI += Library.getDatabase().getSongs().get(index2).getDuration();


        }
        return newI;
    }

    /**
     * a function which update the song until the current song duration(newI) IS NOT negative
     * which a visitor design pattern(if it repeats itself), if the duration is
     * still negative, it unloads
     *
     * @param song       the current song which needs to be updated
     * @param playerLoad the current load status of the function, we updated the remaining time,
     *                   or whether our current song/podcast/playlist/album is loaded anymore
     * @param user       our current user, upon which we have the repeat status
     * @param newI       the timestamp, which has to skip songs in order to become positive
     * @return the current status which we use to update later Status class which is
     * an inheritor of load
     */
    @Override
    public playerLoad visitNegativeRemaininingTimeForStatusJson(final Song song, final playerLoad
            playerLoad, final User user, final Integer newI) {
        Integer newTimestamp = newI;
        playerLoad.setRemainedTime(newI);
        assert song != null;
        while (user.getRepeat() > 0 && newTimestamp <= 0) {
            if (user.getRepeat() == 1) {
                user.setRepeat(0);
            }
            playerLoad.setRemainedTime(song.getDuration() + newTimestamp);
            if (!song.getName().equals("Ad Break")) {
                user.listenToSong(song);
            }
            newTimestamp += song.getDuration();
        }
        if (user.getRepeat() > 0 || newTimestamp > 0) {
            if (user.getRepeat() == 1 && newTimestamp < 0) {
                if (!song.getName().equals("Ad Break")) {
                    user.listenToSong(song);
                }
                user.setRepeat(0);

            }
            playerLoad.setRemainedTime(newTimestamp);
            if (playerLoad.getRemainedTime() <= 0 && user.getRepeat() == 0) {
                playerLoad.setRemainedTime(0);
                playerLoad.setPaused(true);
                user.getUserDatabase().setLoadStatus(false);

                user.getUserDatabase().setSelectstatus(false);
                playerLoad.setName("");
                playerLoad.setAlbum("");
                user.getUserDatabase().setAccesedUser("", user);

            }

        } else {

            if (newTimestamp <= 0) {
                playerLoad.setRemainedTime(0);
                user.getUserDatabase().setAccesedUser("", user);
                user.getUserDatabase().setLoadStatus(false);

                playerLoad.setPaused(true);
                user.getUserDatabase().setAccesedUser("", user);
                user.getUserDatabase().setLoadStatus(false);

                user.getUserDatabase().setSelectstatus(false);
                playerLoad.setName("");
                playerLoad.setAlbum("");
            }


        }
        if (user.getRepeat() == 0) {
            playerLoad.setRepeat("No Repeat");
        } else if (user.getRepeat() == 1) {
            playerLoad.setRepeat("Repeat Once");
        } else {
            playerLoad.setRepeat("Repeat Infinite");
        }
        return playerLoad;
    }

    /**
     * a function which update the album until the current song duration(newI) IS NOT negative
     * * which a visitor design pattern(this for status json function)
     *
     * @param album      the current album
     * @param playerLoad the loaded element
     * @param user       the current user
     * @param newI       the timestamp which can be updated for there are any songs
     * @return the load status after updating the status of the stamp with the timestamp
     */
    @Override
    public playerLoad visitNegativeRemaininingTimeForStatusJson(final Album album, final playerLoad
            playerLoad, final User user, final Integer newI) {
        Integer newTimestamp = newI;
        boolean whileLoop = false;
        NegativeTimeStampVisitor visitor = new NegativeTimeStampVisitor();
        newTimestamp = album.acceptEndForRepeatAll(visitor, playerLoad, user);

        while (newTimestamp <= 0 && user.getSongIndex() < album.getSongs().size() - 1) {
            if (user.getRepeat() != 2) {
                user.setSongIndex(user.getSongIndex() + 1);
            }
            int index = playerStatus.getIndexAlbum(album, user);
            Song song = Library.getDatabase().getSongs().get(index);
            if (!song.getName().equals("Ad Break")) {
                user.listenToSong(song);
            }
            playerLoad.setName(Library.getDatabase().getSongs().get(index).getName());
            playerLoad.setAlbum(Library.getDatabase().getSongs().get(index).getAlbum());
            int getDuration = Library.getDatabase().getSongs().get(index).getDuration();
            playerLoad.setRemainedTime(getDuration + newTimestamp);
            newTimestamp += Library.getDatabase().getSongs().get(index).getDuration();


            newTimestamp = album.acceptEndForRepeatAll(visitor, playerLoad, user);


            whileLoop = true;
        }
        if (playerStatus.isTheEndOfPlaylistNoRepeat(newTimestamp, whileLoop, user)) {


            if (newTimestamp <= 0) {
                playerLoad.setRemainedTime(0);
                playerLoad.setPaused(true);
                user.getUserDatabase().setSelectstatus(false);
                user.getUserDatabase().setLoadStatus(false);

                playerLoad.setRemainedTime(0);
                playerLoad.setPaused(true);
                playerLoad.setName("");
                playerLoad.setAlbum("");
                user.getUserDatabase().setLoadStatus(false);

                user.getUserDatabase().setAccesedUser("", user);
            }

        }

        if (playerStatus.isTheEndofPlaylistForNoRepeat2(album, user)) {
            playerStatus.checkForEndOfPlaylist2NoRepeat(newTimestamp, playerLoad, user, whileLoop);
        } else {
            if (user.getRepeat() == 2) {
                playerLoad.setRemainedTime(newTimestamp);

            }

        }
        if (user.getRepeat() == 0) {
            playerLoad.setRepeat("No Repeat");
        } else if (user.getRepeat() == 1) {
            playerLoad.setRepeat("Repeat All");
        } else {
            playerLoad.setRepeat("Repeat Current Song");
        }
        return playerLoad;
    }

    /**
     * implementing the
     *
     * @param playlist   the current playlist
     * @param playerLoad the load status
     * @param user       the current user
     * @param newI(which used as newTimestamp) the timestamp which we can update
     * @return the new load status
     */
    @Override
    public playerLoad visitNegativeRemaininingTimeForStatusJson(final Playlist playlist, final
    playerLoad playerLoad, final User user, final Integer newI) {
        Integer newTimestamp = newI;
        NegativeTimeStampVisitor visitor = new NegativeTimeStampVisitor();
        newTimestamp = playlist.acceptEndForRepeatAll(visitor, playerLoad, user);
        playerLoad.setName(playerLoad.getName());
        playerLoad.setAlbum(playerLoad.getAlbum());
        playerLoad.setRemainedTime(playerLoad.getRemainedTime());
        boolean whileLoop = false;
        boolean condition = user.getSongIndex() <= playlist.
                getSongList().size() - 1 && user.getRepeat() == 2;
        while (newTimestamp <= 0 && ((user.getSongIndex() < playlist.getSongList().size() - 1)
                || condition)) {
            if (user.getRepeat() != 2) {
                user.setSongIndex(user.getSongIndex() + 1);
            }
            int index = playerStatus.getIndex(playlist, user);
            Song song = Library.getDatabase().getSongs().get(index);
            if (!song.getName().equals("Ad Break")) {
                user.listenToSong(song);
            }
            playerLoad.setName(Library.getDatabase().getSongs().get(index).getName());
            playerLoad.setAlbum(Library.getDatabase().getSongs().get(index).getAlbum());
            int getDuration = Library.getDatabase().getSongs().get(index).getDuration();
            playerLoad.setRemainedTime(getDuration + newTimestamp);
            newTimestamp += Library.getDatabase().getSongs().get(index).getDuration();

            newTimestamp = playerStatus.endOfPlaylistForRepeatAll(playerLoad, playlist, user);

            whileLoop = true;
        }
        if (playerStatus.isTheEndOfPlaylistNoRepeat(newTimestamp, whileLoop, user)) {

            if (newTimestamp <= 0) {

                playerLoad.setRemainedTime(0);
                playerLoad.setPaused(true);
                user.getUserDatabase().setSelectstatus(false);
                user.getUserDatabase().setLoadStatus(false);

                playerLoad.setRemainedTime(0);
                playerLoad.setPaused(true);
                playerLoad.setName("");
                playerLoad.setAlbum("");
                user.getUserDatabase().setLoadStatus(false);

                user.getUserDatabase().setAccesedUser("", user);
            }


        }

        if (playerStatus.isTheEndofPlaylistForNoRepeat2(playlist, user)) {
            playerStatus.checkForEndOfPlaylist2NoRepeat(newTimestamp, playerLoad, user, whileLoop);
        } else {
            if (user.getRepeat() == 2) {
                playerLoad.setRemainedTime(newTimestamp);

            }

        }
        if (user.getRepeat() == 0) {
            playerLoad.setRepeat("No Repeat");
        } else if (user.getRepeat() == 1) {
            playerLoad.setRepeat("Repeat All");
        } else {
            playerLoad.setRepeat("Repeat Current Song");
        }
        return playerLoad;
    }

    /**
     * a function which is implemented for the visitor (implemented for Status json)
     *
     * @param podcast    our current podcast which the function loades
     * @param playerLoad Our current load class which has various functions
     * @param user       the current user which we update its stats
     * @param newI(we use Timestamp) the negative element which has to become
     *                   positive after skipping some songs
     * @return returns the new load status
     */
    @Override
    public playerLoad visitNegativeRemaininingTimeForStatusJson(final Podcast podcast, final
    playerLoad playerLoad, final User user, final Integer newI) {
        Integer newTimestamp = newI;
        user.getUserDatabase().setSelectstatus(false);


        while (playerLoad.getRemainedTime() <= 0 && (user.getLoadedEpisode()
                < podcast.getEpisodes().size() - 1)) {
            playerStatus.originalLength(newTimestamp, playerLoad, podcast, user);


        }

        playerStatus.isTheLoadedEpisodeTheLast(playerLoad, user, podcast);
        return playerLoad;

    }

    /**
     * a function which is implemented when the song it is at end
     * of the playlist
     * , and we have repeat all
     *
     * @param album      album the album with the songs, name etc.
     * @param playerLoad load the negative elemtn which has to
     *                   become positive after skipping some songs
     * @param user       user the current user which we update its stats
     * @return returns the new timestamp
     */
    public int visitEndForRepeatAll(final Album album,
                                    final playerLoad playerLoad, final User user) {
        int newI = playerLoad.getRemainedTime();
        if (user.getRepeat() == 1 && (user.getSongIndex() == (album.getSongs().size() - 1))) {
            user.setSongIndex(0);
            int index2;
            if (!album.getStatusshuffle()) {
                user.setSongindexrandom(user.getSongIndex());
                String selectedSong = album.getSongs().
                        get(user.getSongIndex()).getName();
                String selectedAlbum = album.getSongs().
                        get(user.getSongIndex()).getAlbum();
                index2 = Library.searchSongByNameAndAlbum(selectedSong,
                        selectedAlbum);
            } else {
                user.setSongindexrandom(album.getnewRandomSongIndex(user));
                String selectedSong = album.getSongs().get(album.
                        getnewRandomSongIndex(user)).getName();
                String selectedAlbum = album.getSongs().get(album.
                        getnewRandomSongIndex(user)).getAlbum();
                index2 = Library.searchSongByNameAndAlbum(selectedSong, selectedAlbum);
            }
            if (!Library.getDatabase().getSongs().get(index2).
                    getName().equals("Ad Break")) {
                user.listenToSong(Library.getDatabase().getSongs().get(index2));
            }
            playerLoad.setName(Library.getDatabase().
                    getSongs().get(index2).getName());
            playerLoad.setAlbum(Library.getDatabase().getSongs().get(index2).getAlbum());
            playerLoad.setRemainedTime(Library.getDatabase().getSongs().
                    get(index2).getDuration() + newI);

            newI += Library.getDatabase().getSongs().get(index2).getDuration();


        }
        return newI;
    }


}


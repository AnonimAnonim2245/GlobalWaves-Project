package visitor;

import status.playerLoad;
import types.Album;
import types.Playlist;
import types.Podcast;
import types.Song;
import types.User;

/**
 * visitor interface for Status class
 */
public interface Visitor {
    /**
     * visitor for end of album with repeat all
     * @param album album
     * @param playerLoad load
     * @param user user
     * @return
     */
    int visitEndForRepeatAll(Album album,
                             playerLoad playerLoad, User user);

    /**
     * visitor for end of playlist with repeat all
     * @param playlist playlist
     * @param playerLoad load
     * @param user user
     * @return
     */
    int visitEndForRepeatAll(Playlist playlist,
                             playerLoad playerLoad, User user);

    /**
     * visitor with negative time remaining for status json(playlist)
     * @param playlist
     * @param playerLoad
     * @param user
     * @param newI
     * @return
     */
    playerLoad visitNegativeRemaininingTimeForStatusJson(Playlist playlist,
                      playerLoad playerLoad, User user, Integer newI);

    /**
     * visitor with negative time remaining for status json(album)
     * @param album
     * @param playerLoad
     * @param user
     * @param newI
     * @return
     */
    playerLoad visitNegativeRemaininingTimeForStatusJson(Album album,
                    playerLoad playerLoad, User user, Integer newI);

    /**
     * visitor with negative time remaining for status json(song)
     * @param song
     * @param playerLoad
     * @param user
     * @param newI
     * @return
     */
    playerLoad visitNegativeRemaininingTimeForStatusJson(Song song,
         playerLoad playerLoad, User user, Integer newI);

    /**
     * visitor with negative time remaining for status json(podcast)
     * @param podcast
     * @param playerLoad
     * @param user
     * @param newI
     * @return
     */
    playerLoad visitNegativeRemaininingTimeForStatusJson(Podcast podcast,
                 playerLoad playerLoad, User user, Integer newI);


}

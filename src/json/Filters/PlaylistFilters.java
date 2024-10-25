package json.Filters;

import types.Playlist;
import types.Library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * I implemented a function of filters for the playlist,
 * it's the same as the podcast, but like
 * we also have depending on the visibility in which
 * if it is private, let's check if the owner is the same
 * with the name of the person who selects the playlist
 */
public class PlaylistFilters extends Filters {
    private static final int TOP5 = 5;

    private String name;
    private String owner;

    /**
     * select playlist by these criteries mentioned above
     */
    public List<String> selectPlaylist(final PlaylistFilters criteria,
                                       final String username)
            throws IOException {
        List<String> selectPodcast = new ArrayList<>();
        int number = 0;
        List<Playlist> playlists = Library.getDatabase().getPlaylistList();
        int i = 0;
        while (i < playlists.size()) {
            if (number == TOP5 + 1) {
                break;
            }
            if (criteria.getName() != null) {
                if (!playlists.get(i).getName().
                        startsWith(criteria.getName())) {
                    i++;
                    continue;
                }
            }

            if (criteria.getOwner() != null) {
                if (!playlists.get(i).getOwner().equals(criteria.getOwner())) {
                    i++;
                    continue;
                }

            }
            if (playlists.get(i).getVisibility() != null) {
                if (playlists.get(i).getVisibility().equals("private")
                        && !playlists.get(i).getOwner().equals(username)) {
                    i++;
                    continue;
                }
            }

            number++;
            selectPodcast.add(playlists.get(i).getName());

            i++;
        }
        return selectPodcast;

    }
}

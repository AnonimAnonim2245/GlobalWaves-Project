package json.Filters;

import types.Host;
import types.Library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import types.Artist;

/**
 * class for artist filter
 * it extends filters
 * it has a method that selects the artists
 * only by name
 * it has a method that selects the hosts
 * only by name
 * that match the criteria
 */
public class ArtistFilter extends Filters {
    private static final int TOP5 = 5;

    /**
     * selects by criteria
     *
     * @param criteria criteria
     * @return List<String>
     * @throws IOException exceptie
     */
    public List<String> selectArtist(final ArtistFilter criteria) throws IOException {
        List<String> selectArtist = new ArrayList<>();
        int number = 0;

        for (int i = 0; i < Library.getDatabase().getArtists().size(); i++) {

            if (criteria.getName() != null) {
                if (!Library.getDatabase().getArtists().get(i).
                        getUsername().startsWith(criteria.getName())) {
                    continue;
                }
            }


            number++;
            Artist artist = Library.getDatabase().getArtists().get(i);
            selectArtist.add(artist.getUsername());
            if (number == TOP5) {
                break;
            }
        }
        return selectArtist;

    }
    /**
     * selects by criteria
     *
     * @param criteria criteria
     * @return List<String>
     * @throws IOException exceptie
     */
    public List<String> selectHost(final ArtistFilter criteria) throws IOException {
        List<String> selectArtist = new ArrayList<>();
        int number = 0;

        for (int i = 0; i < Library.getDatabase().getHosts().size(); i++) {

            if (criteria.getName() != null) {
                if (!Library.getDatabase().getHosts().get(i).
                        getUsername().startsWith(criteria.getName())) {
                    continue;
                }
            }


            number++;
            Host host = Library.getDatabase().getHosts().get(i);
            selectArtist.add(host.getUsername());
            if (number == TOP5) {
                break;
            }
        }
        return selectArtist;

    }


}

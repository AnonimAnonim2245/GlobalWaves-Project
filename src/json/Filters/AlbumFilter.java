package json.Filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import main.Main;
import types.Album;
import types.Artist;
import types.Library;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this filter is for the album, I made it according to the name
 * album and for the owner
 * it extends filters
 * it has a method that selects the albums
 * by name, owner and description

 */
@Getter
public class AlbumFilter extends Filters {

    private static final int TOP5 = 5;

    private String description;


    /**
     * selects by criteria
     *
     * @param criteria criteria
     * @return List<String>
     * @throws IOException exceptie
     */
    public List<Album> select(final AlbumFilter criteria) throws IOException {
        List<Album> selectPodcast = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String libraryPath = Main.getLibraryPath();
        int number = 0;
        Library library = objectMapper.readValue(new File(libraryPath),
                Library.class);
        for (Artist artist : Library.getDatabase().getArtists()) {
            for (int i = 0; i < artist.getAlbums().size(); i++) {

                if (criteria.getName() != null) {
                    if (!artist.getAlbums().get(i).
                            getName().startsWith(criteria.getName())) {
                        continue;
                    }
                }

                if (criteria.getOwner() != null) {
                    if (!artist.getAlbums().get(i).
                            getArtist().equals(criteria.getOwner())) {
                        continue;
                    }

                }

                if (criteria.getDescription() != null) {
                    if (!artist.getAlbums().get(i).
                            getDescription().equals(criteria.getDescription())) {
                        continue;
                    }
                }

                number++;
                selectPodcast.add(artist.getAlbums().get(i));
                if (number == TOP5) {
                    break;
                }
            }
            if (number == TOP5) {
                break;
            }

        }

        return selectPodcast;

    }

}

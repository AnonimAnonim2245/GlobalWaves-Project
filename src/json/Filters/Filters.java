package json.Filters;

import lombok.Getter;
import types.Library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this filter is the simplest and is for the podcast, I made it according to the name
 * podcast and for the owner
 */
@Getter
public class Filters {

    private String name;
    private String owner;

    /**
     * sets the name
     *
     * @param name2 the name of the podcast
     */
    public void setName(final String name2) {
        this.name = name2;
    }

    /**
     * sets the name of the owner
     */

    public void setOwner(final String owner2) {
        this.owner = owner2;
    }

    private static final int TOP5 = 5;

    /**
     * selects by criteria
     *
     * @param criteria criteria
     * @return List<String>
     * @throws IOException exceptie
     */
    public List<String> select(final Filters criteria) throws IOException {
        List<String> selectPodcast = new ArrayList<>();
        int number = 0;
        for (int i = 0; i < Library.getDatabase().getPodcasts().size(); i++) {

            if (criteria.getName() != null) {
                if (!Library.getDatabase().getPodcasts().get(i).
                        getName().startsWith(criteria.getName())) {
                    continue;
                }
            }

            if (criteria.getOwner() != null) {
                if (!Library.getDatabase().getPodcasts().get(i).
                        getOwner().equals(criteria.getOwner())) {
                    continue;
                }

            }

            number++;
            selectPodcast.add(Library.getDatabase().getPodcasts().get(i).getName());
            if (number == TOP5) {
                break;
            }
        }
        return selectPodcast;

    }


}

package types;

import lombok.Getter;
import lombok.Setter;

/**
 * folosim clasa pentru episoadele din cadrul unui podcast, in care avem mai multe functii
 * sa vedem duratia unui episod normal, cat au mai ramas din episodul din podcast,
 * numele, descriereea lui
 */
public final class Episode {
    private String name;
    private Integer duration;
    @Getter
    @Setter
    private Integer remainingduration = 0;
    private String description;

    public Episode() {
    }

    /**
     * searches host by episode
     * @return host class
     */
    public Host searchHostbyEpisode() {
        for (Podcast podcast : Library.getDatabase().getPodcasts()) {
            for (Episode episode1 : podcast.getEpisodes()) {
                if (episode1.equals(this)) {
                    Host host = Library.getDatabase().getHostInstance(podcast.getOwner());
                    return host;
                }
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}

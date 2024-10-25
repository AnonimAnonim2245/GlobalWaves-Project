package types;
/**
 This functions acts as a hashmap for an Episode class, taking into consideration
 the number of times an Episode has been listened
 to.
 **/
public class EpisodeListenCount {
    private Episode episode;
    private int count = 0;
    /**
     * constructor for EpisodeListenCount
     * @param episode the episode
     * @param count the times the episode was listened to
     */
    public EpisodeListenCount(final Episode episode, final int count) {
        this.episode = episode;
        this.count = count;
    }

    /**
     * getter for episode
     * @return episode
     */
    public Episode getEpisode() {
        return episode;
    }

    /**
     * setter for episode
     * @param episode the episode
     */
    public void setEpisode(final Episode episode) {
        this.episode = episode;
    }

    /**
     * getter for count
     * @return count
     */
    public int getCount() {
        return count;
    }
    /**
     * setter for count
     * @param count the times the episode was listened to
     */
    public void setCount(final int count) {
        this.count = count;
    }

    /**
     * getter for count by name
     * @param name the name of the episode
     * @return count the times the episode was listened to
     */
    public int getCountByName(final Episode name) {
        if (episode.equals(name)) {
            return count;
        }
        return 0;
    }
}

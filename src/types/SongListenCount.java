package types;
/**
  This functions acts as a hashmap for a Song class, taking into consideration the
 number of times a song has been listened
    to.
 **/
public class SongListenCount {
    private Song song;
    private int count = 0;

    /**
     * constructor for SongListenCount
     * @param song the song
     * @param count the times the song was listened to
     */
    public SongListenCount(final Song song, final int count) {
        this.song = song;
        this.count = count;
    }

    /**
     * getter for song
     * @return song
     */

    public Song getSong() {
        return song;
    }
    /**
     * setter for song
     * @param song the song
     */
    public void setSong(final Song song) {
        this.song = song;
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
     * @param count the times the song was listened to
     */
    public void setCount(final int count) {
        this.count = count;
    }
    /**
     * getter for count by name
     * @param name the name of the song
     * @return count the times the song was listened to
     */
    public int getCountByName(final Song name) {
        if (song.getName().equals(name)) {
            return count;
        }
        return 0;
    }
}

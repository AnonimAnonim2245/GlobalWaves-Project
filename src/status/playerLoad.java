package status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.Filters.SongFilters;
import json.JsonConvertToCategories;
import lombok.Getter;
import lombok.Setter;
import types.*;


/**
 * this is the load function in which I implement
 * type to see what type it is, if it is a song, podcast or library to manage
 * each situation, the repeat state (0,1,2),
 * the remaining time of the episode, song,
 * which we use to see the state of the song/podcast, if
 * let's finish it or stay with that podcast.
 * In the Load function we set information about the load and what type of repeat it must be
 * if it is a podcast, load episode 0
 * or the first song if it's a playlist/album, but we'll take the others
 * depending on the selected element and loadjson
 * we take the information and put it in json format to put in the main output
 * I implemented a barrier in order to prevent other users to get the name and stats
 * of the jsonElement, when I do the sync for all users before the delete Albums or
 * delete Users
 */
@Getter
@Setter
/**
 * the parent of class playerStatus
 */
public class playerLoad {
    private static int count;
    private Song song;
    private String name;
    private String genre;
    private String album;
    private String owner;
    private Integer remainedTime;
    private String repeat;
    private Boolean shuffle;
    private String type;
    private Boolean paused;

    public playerLoad(final playerLoad status) {
        if (status == null) {
            return;
        }
        this.setName(status.getName());
        this.setGenre(status.getGenre());
        this.setAlbum(status.getAlbum());
        this.setRemainedTime(status.getRemainedTime());
        this.setOwner(status.getOwner());
        this.setRepeat(status.getRepeat());
        this.setShuffle(status.getShuffle());
        this.setPaused(status.getPaused());
        this.setType(status.getType());

    }

    /**
     * returns type of the current element
     *
     * @param type2 string of type
     * @return type(whether is a podcast or not)
     */
    public String getType(final String type2) {
        return type2;
    }

    /**
     * return the set type
     *
     * @param type2 the type
     */
    public void setType(final String type2) {
        this.type = type2;
    }

    /**
     * is used in order to load the select element
     */
    public playerLoad(final User user) {

    }

    public playerLoad() {

        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());

        if (Library.getDatabase().getBarrier() == true) {
            return;
        }

        this.setAlbum("");
        this.setGenre("");


        if (!user.getUserDatabase().getSelectStatus()) {
            return;
        }



        if (user.getUserDatabase().getType().startsWith("podcast")) {

            Podcast podcast = Library.podcastByName(user.getUserDatabase().getSelectElement());
            assert podcast != null;
            if (user.getRepeat() == 0) {
                this.setRepeat("No Repeat");
            } else if (user.getRepeat() == 1) {
                this.setRepeat("Repeat Once");
            } else {
                this.setRepeat("Repeat Infinite");
            }
            this.type = "podcast";
            user.setLoadedEpisode(0);
            Episode episode = podcast.getEpisodes().get(user.getLoadedEpisode());
            user.listenToEpisode(episode);

            this.setName(podcast.getEpisodes().get(user.getLoadedEpisode()).getName());
            this.setAlbum("");
            this.setGenre("");
            user.getUserDatabase().setLoadedPodcast(podcast);

            Library.getDatabase().setHostElement(podcast.getOwner());
            int original = user.getUserDatabase().
                    getLoadedPodcast().getEpisodes().
                    get(user.getLoadedEpisode()).getDuration();
            int nou = user.getUserDatabase().
                    getLoadedPodcast().getEpisodes().
                    get(user.getLoadedEpisode()).getRemainingduration();
            if (nou == 0) {
                user.getUserDatabase().getLoadedPodcast().
                        getEpisodes().get(user.getLoadedEpisode()).
                        setRemainingduration(original);
            }
            this.setRemainedTime(user.getUserDatabase().getLoadedPodcast().
                    getEpisodes().get(user.getLoadedEpisode()).getDuration());

        } else if (user.getUserDatabase().getType().startsWith("song")) {


            int index = Library.searchSongByNameAndAlbum(user.getUserDatabase().
                    getSelectElement(), user.getUserDatabase().getSelectElementAlbum());

            Song song2 = Library.getDatabase().getSongs().get(index);
            this.setAlbum("");


            assert song2 != null;
            if (user.getRepeat() == 0) {
                this.setRepeat("No Repeat");
            } else if (user.getRepeat() == 1) {
                this.setRepeat("Repeat Once");
            } else {
                this.setRepeat("Repeat Infinite");
            }
            this.setRepeat("No Repeat");

            this.setName(user.getUserDatabase().getSelectElement());
            this.setAlbum(user.getUserDatabase().getSelectElementAlbum());


            this.setGenre(user.getUserDatabase().getSelectElementGenre());

            this.type = "song";
            if (!song2.getName().equals("Ad Break")) {
                user.listenToSong(song2);
            }


            this.setRemainedTime((SongFilters.songDurationByName(user.getUserDatabase().
                    getSelectElement())));

        } else if (user.getUserDatabase().getType().startsWith("playlist")) {

            Playlist playlist = user.getUserDatabase().
                    selectPlaylistByName(user.getUserDatabase().
                            getSelectElement());
            assert playlist != null;
            if (user.getRepeat() == 0) {
                this.setRepeat("No Repeat");
            } else if (user.getRepeat() == 1) {
                this.setRepeat("Repeat All");
            } else {
                this.setRepeat("Repeat Current Song");
            }
            this.setRepeat("No Repeat");

            this.type = "playlist";
            user.getUserDatabase().setLoadedPlaylist(playlist);
            user.setSongIndex(0);
            playlist.setSongList();


            user.setStatusshuffle(false);
            String searchSong = playlist.getSongList().get(playlist.getnewSongIndex(user));
            String searchAlbum = playlist.getAlbumList().get(playlist.getnewSongIndex(user));
            int index = Library.searchSongByNameAndAlbum(searchSong, searchAlbum);
            this.setAlbum(Library.getDatabase().getSongs().get(index).getAlbum());
            Song song2 = Library.getDatabase().getSongs().get(index);
            if (!song2.getName().equals("Ad Break")) {
                user.listenToSong(song2);
            }


            this.setGenre(Library.getDatabase().getSongs().get(index).getGenre());
            this.setName(Library.getDatabase().getSongs().get(index).getName());
            this.setRemainedTime(Library.getDatabase().getSongs().
                    get(index).getDuration());

        } else if (user.getUserDatabase().getType().startsWith("album")) {

            String albumName = user.getUserDatabase().getSelectAlbum2();
            String ownerName = user.getUserDatabase().getSelectElementOwner();
            Album album2 = user.getUserDatabase().selectAlbumByNameAndOwner(albumName, ownerName);
            assert album2 != null;
            if (user.getRepeat() == 0) {
                this.setRepeat("No Repeat");
            } else if (user.getRepeat() == 1) {
                this.setRepeat("Repeat All");
            } else {
                this.setRepeat("Repeat Current Song");
            }
            this.setRepeat("No Repeat");

            this.type = "album";
            user.getUserDatabase().setLoadedAlbum(album2);
            user.setSongIndex(0);
            album2.setSongList();
            album2.setStatusshuffle(false);

            String searchSong = album2.getSongs().get(album2.
                    getnewSongIndex(user)).getName();
            String searchAlbum = album2.getSongs().get(album2.
                    getnewSongIndex(user)).getAlbum();
            int index = Library.searchSongByNameAndAlbum(searchSong, searchAlbum);
            Song song2 = Library.getDatabase().getSongs().get(index);
            if (!song2.getName().equals("Ad Break")) {
                user.listenToSong(song2);
            }
            this.setOwner(Library.getDatabase().getSongs().get(index).getArtist());
            this.setAlbum(Library.getDatabase().getSongs().get(index).getAlbum());
            this.setGenre(Library.getDatabase().getSongs().get(index).getGenre());
            this.setName(Library.getDatabase().getSongs().get(index).getName());
            this.setRemainedTime(Library.getDatabase().getSongs().
                    get(index).getDuration());

        }

        user.getUserDatabase().setLoadedElement(user.getUserDatabase().
                getSelectElement());
        user.getUserDatabase().setShufflestatus(true);
        this.setShuffle(this.getShuffle());
        this.setPaused(false);

        user.getUserDatabase().setStatus(this);

    }

    /**
     * is used to load the json
     *
     * @param prevTimestamp the previous timestamp
     * @return json
     */
    public JsonNode loadJson(final int prevTimestamp) {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        User user = Library.getDatabase().getUserInstance(jsonElement.getUsername());

        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
      /* if(Library.getDatabase().checkIfArtistValid("Elton John")){
            Artist artist = Library.getDatabase().getArtistInstance("Elton John");
            if(!artist.getTopSongsListened().isEmpty()){
                jsonObject.put("topSongsArtist", artist.getTopAllSongsListened());
                jsonObject.put("topAlbumsArtist", artist.getTopAllAlbumsListened());
                jsonObject.put("topFansArtist", artist.getTopAllFansListened());
            }

        }*/



        if (user.getUserDatabase().getSelectStatus()) {
            user.getUserDatabase().setLoadStatus(true);
            jsonObject.put("message", "Playback loaded successfully.");

        } else {

            user.getUserDatabase().setLoadStatus(false);
            if (user.getUserDatabase().getStatus() != null) {
                playerStatus status = new playerStatus();
                status.moveTimeStamp(user, prevTimestamp);
            }
            jsonObject.put("message", "Please select a source before attempting to load.");
            return jsonObject;
        }

        user.getUserDatabase().setLoadStatus(true);
        user.getUserDatabase().setShufflestatus(true);
        user.setRepeat(0);
        user.setPrevAdTimestamp(-1);
        user.getUserDatabase().setSelectstatus(false);
        user.getUserDatabase().setLikestatus(true);

        return jsonObject;
    }

    /**
     * sets the pause status
     *
     * @param b true or false
     */
    public void setPaused(final boolean b) {
        this.paused = b;
    }

    /**
     * sets the remaining time
     *
     * @param i the remaining time
     */
    public void setRemainedTime(final int i) {
        this.remainedTime = i;
    }

}

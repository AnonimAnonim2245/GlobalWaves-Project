package database;

import lombok.Getter;
import lombok.Setter;
import status.playerLoad;
import types.*;

import java.util.*;

/**
 * this is the function that stores categories of elements that
 * we use during the function
 * , and we change them during the function, they are different for each user,
 * here we have LoadedPodcast for the podcast
 * which is loaded, after loaded playlist, selectelement, for selected element,
 * and a loaded element
 * a simple element, we have several Statuses (Loaded, Shuffle, Search, Like),
 * they help us during orders
 * if it is okay or not to implement them.
 * We have several functions to select in functions of several criteria
 */
public class Database {
    private static final int TOP5INDEX = 5;

    @Getter
    private Podcast loadedPodcast;
    @Getter
    @Setter
    private String selectAlbum2;
    @Getter
    @Setter
    private String pageStatus = "HOME";
    @Getter
    @Setter
    private Stack<String> pageStatusStack =  new Stack<>();
    @Getter
    @Setter
    private Stack<String> pageStatusNextStack = new Stack<>();

    @Getter
    @Setter
    private String pageElement = "";
    @Getter
    private Playlist loadedPlaylist;
    @Getter
    private Album loadedAlbum;
    @Getter
    private String selectElement = "";
    @Getter
    @Setter
    private String selectElementAlbum = "";
    @Getter
    @Setter
    private String selectElementOwner  = "";
    @Getter
    @Setter
    private String loadedElement = "";

    @Getter
    @Setter
    private String selectElementGenre = "";


    @Getter
    private final List<String> selected = new ArrayList<>();
    @Getter
    private final List<String> previousSelected = new ArrayList<>();
    @Getter
    private final List<Song> selectedSong = new ArrayList<>();
    @Getter
    private final List<Album> previousSelectedAlbumSinger = new ArrayList<>();
    @Getter
    private final List<Album> selectedAlbumSinger = new ArrayList<>();
    @Getter
    private final List<Song> previousSelectedSong = new ArrayList<>();
    @Getter
    private String type = "";
    @Getter
    @Setter
    private String searchType = "";
    @Getter
    @Setter
    private Boolean premiumStatus = false;
    @Getter
    private playerLoad status;
    private boolean pause;
    private boolean selectstatus;
    @Getter
    private Boolean loadStatus = false;
    @Getter
    @Setter
    private Boolean likestatus;
    @Getter
    @Setter
    private Boolean shufflestatus;
    @Getter
    @Setter
    private Boolean searchstatus;
    @Getter
    private String accesedUser;

    /**
     * gives us the user which was acceesed(the artist
     * or host)
     * @param accesedUser2 the user which was accesed
     * @param user our normal user
     */
    public void setAccesedUser(final String accesedUser2, final User user) {

        for (User element : Library.getDatabase().getUsers()) {

            if (element.getUsername().equals(user.getUsername())) {
                element.getUserDatabase().accesedUser = (accesedUser2);
            }

        }
    }

    /**
     * our database class which is unique for each user
     */
    public Database() {
        pageStatusStack.push("HOME");
    }

    /**
     * select playlist by owner
     */
    public List<Playlist> selectPlaylistByOwner(final String name2) {
        List<Playlist> selectedlist = new ArrayList<>();
        if (Library.getDatabase().getPlaylistList().isEmpty()) {
            return selectedlist;
        }
        for (Playlist elements : Library.getDatabase().
                getPlaylistList()) {
            if (elements.getOwner().equals(name2)) {
                selectedlist.add(elements);
            }
        }
        return selectedlist;
    }

    /**
     * select playlist by name, name2 is the name of the song
     */
    public Playlist selectPlaylistByName(final String name2) {

        if (!Library.getDatabase().getPlaylistList().isEmpty()) {
            for (Playlist element : Library.getDatabase().
                    getPlaylistList()) {
                if (element.getName() == null) {
                    return null;
                }
                if (element.getName().equals(name2)) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * select album by name and owner, name2 is the name of the song
     */
    public Album selectAlbumByNameAndOwner(final String name, final String owner) {

        if (!Library.getDatabase().getAlbums().isEmpty()) {
            for (Album album : Library.getDatabase().getAlbums()) {
                if (album.getName() == null) {
                    return null;
                }
                if (album.getName().equals(name) && album.getArtist().equals(owner)) {
                    return album;
                }
            }
        }
        return null;
    }

    /**
     * select album by name,name2 is the name of the song
     */
    public Album selectAlbumByName(final String name2) {

        if (!Library.getDatabase().getAlbums().isEmpty()) {
            for (Album album : Library.getDatabase().getAlbums()) {
                if (album.getName() == null) {
                    return null;
                }
                if (album.getName().equals(name2)) {
                    return album;
                }
            }
        }
        return null;
    }

    /**
     * select album by name, name2 is the name of the song
     */
    public Podcast selectPodcastByName(final String name2) {

        if (!Library.getDatabase().getPodcasts().isEmpty()) {
            for (Podcast podcast : Library.getDatabase().getPodcasts()) {
                if (podcast.getName() == null) {
                    return null;
                }
                if (podcast.getName().equals(name2)) {
                    return podcast;
                }
            }
        }
        return null;
    }

    /**
     * Select playlists of the owner by iD
     */
    public int selectListById(final String owner, final int number) {
        int count = 1;
        int numberOfPlaylist = 0;
        if (!Library.getDatabase().getPlaylistList().isEmpty()) {
            for (Playlist element : Library.getDatabase().getPlaylistList()) {

                if (element.getOwner() == null) {
                    return -1;
                }
                if (element.getOwner().equals(owner)) {
                    if (count == number) {
                        return numberOfPlaylist;
                    } else {
                        count++;
                    }
                }
                numberOfPlaylist++;
            }
        }
        return -1;
    }

    /**
     * if there are several maximum values,
     * we choose the first one, and the first
     * one is the one added among the first
     */
    public List<String> getTop5Playlists() {
        if (Library.getDatabase().getPlaylistList().isEmpty()) {
            return new ArrayList<>();
        }
        List<String> top5 = new ArrayList<>();
        int i = 0;
        int maxprev = -1;
        int maxi = -1, maxiIndex = -1;
        while (i < TOP5INDEX && i < Library.getDatabase().getPlaylistList().size()) {
            maxi = -1;
            maxiIndex = -1;
            int j = 0;
            for (Playlist element3 : Library.getDatabase().
                    getPlaylistList()) {

                if (maxprev == -1) {
                    if (element3.getFollowers() > maxi) {
                        maxi = element3.getFollowers();
                        maxiIndex = j;
                    }
                } else {
                    if (element3.getFollowers() > maxi
                            && element3.getFollowers()
                            <= maxprev
                            && !top5.contains(element3.getName())) {
                        maxi = element3.getFollowers();
                        maxiIndex = j;
                    }
                }
                j++;
            }
            top5.add(Library.getDatabase().
                    getPlaylistList().get(maxiIndex).getName());
            maxprev = maxi;

            i++;
        }

        return top5;
    }


    /**
     * in cazul in care sunt mai multe valori maxime,
     * o alegem pe aia prima, iar aia prima este cea adaugata
     * printre primele
     */
    public List<String> getTop5Albums() {
        if (Library.getDatabase().getPlaylistList().isEmpty()) {
            return new ArrayList<>();
        }
        List<String> top5 = new ArrayList<>();
        int i = 0;
        int maxprev = -1;
        int maxi = -1, maxiIndex = -1;
        while (i < TOP5INDEX && i < Library.getDatabase().getAlbums().size()) {
            maxi = -1;
            maxiIndex = -1;
            int j = 0;
            for (Album album2 : Library.getDatabase().
                    getAlbums()) {

                if (maxprev == -1) {
                    if (album2.sumofLikesongAlbums() > maxi) {
                        maxi = album2.sumofLikesongAlbums();
                        maxiIndex = j;
                    }
                } else {
                    boolean ok = album2.sumofLikesongAlbums() == maxi;
                    boolean ok2 = !top5.contains(album2.getName());

                    if (ok && Library.getDatabase().getAlbums().
                            get(maxiIndex).getName().compareTo(album2.getName()) > 0 && ok2) {
                        maxi = album2.sumofLikesongAlbums();
                        maxiIndex = j;
                    }
                    if (album2.sumofLikesongAlbums() > maxi && album2.sumofLikesongAlbums()
                            <= maxprev && !top5.contains(album2.getName())) {
                        maxi = album2.sumofLikesongAlbums();
                        maxiIndex = j;
                    }

                }
                j++;
            }
            top5.add(Library.getDatabase().
                    getAlbums().get(maxiIndex).getName());
            maxprev = maxi;

            i++;
        }

        return top5;
    }

    /**
     * if there are several maximum values,
     * we choose the first one, and the first one
     * is the one added among the first ones
     */
    public List<String> getTop5Artists() {
        if (Library.getDatabase().getArtists().isEmpty()) {
            return new ArrayList<>();
        }
        List<String> top5 = new ArrayList<>();
        int i = 0;
        int maxprev = -1;
        int maxi = -1, maxiIndex = -1;
        while (i < TOP5INDEX && i < Library.getDatabase().getArtists().size()) {
            maxi = -1;
            maxiIndex = -1;
            int j = 0;
            for (Artist artist : Library.getDatabase().
                    getArtists()) {

                if (maxprev == -1) {
                    if (artist.sumofLikeAlbumsUsers() > maxi) {
                        maxi = artist.sumofLikeAlbumsUsers();
                        maxiIndex = j;
                    }
                } else {
                    if (artist.sumofLikeAlbumsUsers() > maxi
                            && artist.sumofLikeAlbumsUsers()
                            <= maxprev
                            && !top5.contains(artist.getUsername())) {
                        maxi = artist.sumofLikeAlbumsUsers();
                        maxiIndex = j;
                    }
                }
                j++;
            }
            top5.add(Library.getDatabase().
                    getArtists().get(maxiIndex).getUsername());
            maxprev = maxi;

            i++;
        }

        return top5;
    }


    /**
     * in top5songs we implement the principle with the
     * origin in which likes were given by the user,
     * if several elements have maximum values, we do as
     to get5Playlists where we add the first of them
     */
    public List<String> getTop5Songs() {

        List<String> top5 = new ArrayList<>();
        int i = 0;
        int maxPrev = -1;
        int maxi = -1, maxiIndex = -1;
        while (i < TOP5INDEX && i < Library.getDatabase().getSongs().size()) {
            maxi = -1;
            maxiIndex = -1;
            int j = 0;
            for (Song element3 : Library.getDatabase().getSongs()) {
                if (maxPrev == -1) {
                    if (element3.getUsersLiked().size() > maxi) {
                        maxi = element3.getUsersLiked().size();
                        maxiIndex = j;
                    }
                } else {
                    if (element3.getUsersLiked().size() > maxi
                            && element3.getUsersLiked().size()
                            <= maxPrev && !top5.contains(element3.getName())) {
                        maxi = element3.getUsersLiked().size();
                        maxiIndex = j;
                    }
                }
                j++;
            }
            top5.add(Library.getDatabase().getSongs().
                    get(maxiIndex).getName());
            maxPrev = maxi;

            i++;
        }

        return top5;
    }


    /**
     * added selected a list<string> for the selected elements,
     * deletes the previous ones
     *
     * @param selected2 list of selected elements
     */
    public void addSelected(final List<String> selected2) {
        if (this.selected.equals(selected2)) {
            return;
        }
        this.selected.removeAll(this.getSelected());
        this.selected.addAll(selected2);
    }

    /**
     * addSelectedSong adds selected songs
     * @param selected2 the selected songs
     *                  which use for
     *                  selected and search class
     *
     */
    public void addSelectedSong(final List<Song> selected2) {
        if (this.selectedSong.equals(selected2)) {
            return;
        }
        this.selectedSong.removeAll(this.getSelected());
        this.selectedSong.addAll(selected2);
    }

    /**
     * addSelectedAlbum adds selected albums
     * @param selected2 the selected albums
     *                  which use for
     *                  selected and search class
     *
     */
    public void addSelectedAlbum(final List<Album> selected2) {
        if (this.selectedAlbumSinger.equals(selected2)) {
            return;
        }
        this.selectedAlbumSinger.removeAll(this.getSelected());
        this.selectedAlbumSinger.addAll(selected2);
    }

    /**
     * addPreviousSelected helps us in this case
     * when our selected is empty, within selected and Search
     */
    public void addPreviousSelected(final List<String> selected2) {
        if (this.previousSelected.equals(selected2)) {
            return;
        }
        this.previousSelected.removeAll(this.getPreviousSelected());
        this.previousSelected.addAll(selected2);
    }

    /**
     * addPreviousSelectedSong adds previous selected songs
     * @param selected2 the previous selected songs
     */
    public void addPreviousSelectedSong(final List<Song> selected2) {
        if (this.previousSelectedSong.equals(selected2)) {
            return;
        }
        this.previousSelectedSong.removeAll(this.getPreviousSelectedSong());
        this.previousSelectedSong.addAll(selected2);
    }
    /**
     * addPreviousSelectedAlbum adds previous selected album
     * @param selected2 the previous selected album
     */
    public void addPreviousSelectedAlbum(final List<Album> selected2) {
        if (this.previousSelectedAlbumSinger.equals(selected2)) {
            return;
        }
        this.previousSelectedAlbumSinger.removeAll(this.getPreviousSelectedAlbumSinger());
        this.previousSelectedAlbumSinger.addAll(selected2);
    }

    /**
     * set element if it doesnt contain already
     */
    public void setElement(final String selectElement2) {
        if (this.selectElement.equals(selectElement2)) {
            return;
        }
        this.selectElement = (selectElement2);
    }

    /**
     * get the selected element
     */

    public String getElement() {
        return selectElement;
    }

    /**
     * set type
     */
    public void setType(final String type2) {
        this.type = type2;
    }

    /**
     * set Select status
     *
     * @param b true or false for selct status
     */

    public void setSelectstatus(final Boolean b) {
        this.selectstatus = b;
    }

    /**
     * set Load status
     */
    public void setLoadStatus(final Boolean b) {
        this.loadStatus = b;
    }

    /**
     * get Select status
     */
    public boolean getSelectStatus() {
        return selectstatus;
    }

    /**
     * get loadedplaylist
     */
    public void setLoadedPlaylist(final Playlist playlist2) {
        this.loadedPlaylist = playlist2;
    }

    /**
     * set loadedAlbum
     * @param album the album which is loaded
     */
    public void setLoadedAlbum(final Album album) {
        this.loadedAlbum = album;
    }

    /**
     * get setLoadedPodcast
     */
    public void setLoadedPodcast(final Podcast podcast2) {
        this.loadedPodcast = podcast2;
    }

    /**
     * search song by name
     *
     * @param songs2 list of songs
     * @param name2  the name of the song
     * @return true or false(if it contains or not)
     */
    public boolean searchSongByName(final List<String> songs2,
                final List<String> albums, final String name2, final String album2) {

        if (songs2.isEmpty()) {
            return false;
        }
        int i = 0;
        for (String element : songs2) {
            if (element.equals(name2) && albums.get(i).equals(album2)) {
                return true;
            }
            i++;
        }
        return false;
    }

    /**
     * set status
     */
    public void setStatus(final playerLoad status) {
        this.status = status;
    }

    /**
     * set Pause
     */
    public void setPause(final boolean pause) {
        this.pause = pause;
    }

    /**
     * get Pause
     */
    public boolean getPause() {
        return pause;
    }

    /**
     * Search user by name
     * @param name2 the name of the user
     */
    public User searchUserByName(final String name2) {
        if (Library.getDatabase().getUsers().isEmpty()) {
            return null;
        }
        for (User element : Library.getDatabase().getUsers()) {
            if (element.getUsername().equals(name2)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Search song by name, artist and album
     */
    public Song searchSongByNameArtistAlbum(final String name2,
                                            final String artist2, final String album2) {
        if (Library.getDatabase().getSongs().isEmpty()) {
            return null;
        }
        for (Song element : Library.getDatabase().getSongs()) {
            if (element.getName().equals(name2)
                    && element.getArtist().equals(artist2)
                    && !element.getAlbum().equals(album2)) {

            }
        }
        return null;
    }
}

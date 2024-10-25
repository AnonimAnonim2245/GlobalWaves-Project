package json.Filters;

import lombok.Getter;
import types.Song;
import types.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * we implement a filter function for the song,
 * here it is more complicated because we have more
 * criteria artist, album, release, genre, lyrics and tags.
 * Here we also include the cases in which the elements
 * ours either from the criteria, or ours are in lowercase to include them
 */
@Getter
public class SongFilters extends Filters {
    private static final int TOP5 = 5;

    private String artist;
    private String album;
    private String releaseYear;
    private String genre;
    private String lyrics;
    private List<String> tags;

    /**
     * sets the duration of a song according to the name
     */
    public static int songDurationByName(final String name2) {
        List<Song> songs = Library.getDatabase().getSongs();
        for (Song song : songs) {
            if (song.getName().startsWith(name2)) {
                return song.getDuration();
            }
        }


        return 0;
    }

    /**
     * Select songs according to various criteria, artist, genre, etc.
     */
    public List<Song> selectSongs(final SongFilters songCriteria) {
        List<Song> selectSong = new ArrayList<>();
        int number = 0;
        for (int i = 0; i < Library.getDatabase().getSongs().size(); i++) {
            if (songCriteria.getArtist() != null) {
                if (!Library.getDatabase().getSongs().get(i).getArtist().
                        equals(songCriteria.getArtist())) {
                    continue;
                }
            }
            if (songCriteria.getName() != null) {

                int subCriteriaIndex = songCriteria.getName().indexOf(" - ");
                int subsongNameIndex = Library.getDatabase().getSongs().get(i).
                        getName().indexOf(" - ");
                String subCriteria, subsongName;
                subCriteria = songCriteria.getName();

                if (subsongNameIndex == -1) {
                    subsongName = Library.getDatabase().getSongs().get(i).getName();
                } else {
                    subsongName = Library.getDatabase().getSongs().get(i).getName().
                            substring(0, subsongNameIndex);
                }

                String lowersubsongName = subsongName.toLowerCase();
                String lowersubCriteria = subCriteria.toLowerCase();

                boolean option1 = lowersubsongName.startsWith(lowersubCriteria);
                boolean option2 = Library.getDatabase().getSongs().get(i).getName().
                        startsWith(songCriteria.getName());
                if (!option1 && !option2) {
                    continue;
                }
            }
            if (songCriteria.getReleaseYear() != null) {
                String release = songCriteria.getReleaseYear();
                if (release.charAt(0) == ('>')) {
                    release = release.substring(1);
                    if (Integer.parseInt(release) > Library.getDatabase().
                            getSongs().get(i).getReleaseYear()) {
                        continue;
                    }
                } else if (release.charAt(0) == ('<')) {
                    release = release.substring(1);
                    if (Integer.parseInt(release) < Library.getDatabase().
                            getSongs().get(i).getReleaseYear()) {
                        continue;
                    }
                } else {
                    if (Integer.parseInt(release) != Library.getDatabase().
                            getSongs().get(i).getReleaseYear()) {
                        continue;
                    }
                }


            }
            if (songCriteria.getGenre() != null) {
                if (!Library.getDatabase().getSongs().get(i).getGenre().
                        toLowerCase().equals(songCriteria.getGenre())
                        && !Library.getDatabase().getSongs().get(i).getGenre().
                        equals(songCriteria.getGenre())) {
                    continue;
                }

            }
            if (songCriteria.getAlbum() != null) {
                if (!Library.getDatabase().getSongs().get(i).getAlbum().
                        contains(songCriteria.getAlbum())) {
                    continue;
                }

            }
            if (songCriteria.getLyrics() != null) {
                if (!Library.getDatabase().getSongs().get(i).getLyrics().
                        contains(songCriteria.getLyrics())
                        && !Library.getDatabase().getSongs().get(i).getLyrics().
                        toLowerCase().contains(songCriteria.getLyrics())
                        && !Library.getDatabase().getSongs().get(i).getLyrics().
                        contains(songCriteria.getLyrics().toLowerCase())) {
                    continue;
                }

            }
            if (songCriteria.getTags() != null) {
                if (!Library.getDatabase().getSongs().get(i).getTags().
                        containsAll(songCriteria.getTags())) {
                    continue;
                }
            }
            number++;
            selectSong.add(Library.getDatabase().getSongs().get(i));
            if (number == TOP5) {
                break;
            }
        }
        return selectSong;

    }

}

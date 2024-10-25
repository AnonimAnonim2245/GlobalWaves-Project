package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.Database;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * In the host function, there are the announcements and podcasts
 * classes related the host
 */
@Getter
public class Host {
    private static final int TOP5 = 5;
    @Setter
    private String pageStatus = "HOME";
    @Setter
    private List<Announcement> announcements = new ArrayList<>();
    @Setter
    private List<Podcast> podcasts = new ArrayList<>();
    private String username;
    private final Database userDatabase;

    public Host() {
        this.userDatabase = new Database();
    }

    public Host(final String username) {
        this.username = username;
        this.userDatabase = new Database();
    }

    /**
     * tops of the episodes listened by the user
     */
    public JsonNode getTopEpisodesListened() {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> episodeListens = new HashMap<>();

        for (User user : Library.getDatabase().getUsers()) {
            if (!user.getEpisodeListen().isEmpty()) {
                for (Episode episode : user.getListenedEpisodes()) {
                    if (episode.searchHostbyEpisode().
                            getUsername().equals(this.username)) {
                        if (episodeListens.containsKey(episode.getName())) {
                            int count = episodeListens.get(episode.getName());
                            int countEpisode = user.getEpisodeListenCount(episode);
                            int countElement = countEpisode;

                            episodeListens.put(episode.getName(), countElement + count);
                        } else {
                            int countEpisode = user.getEpisodeListenCount(episode);
                            int countElement = countEpisode;
                            episodeListens.put(episode.getName(), countElement);
                        }
                    }
                }
            }
        }

        List<String> top5Episode = new ArrayList<>();
        int z = -1;
        int count = episodeListens.entrySet().size();
        for (int i = 0; i < TOP5 && i < count; i++) {
            int max = 0;
            String maxHost = "";
            int k = 0;
            for (Map.Entry<String, Integer> entry : episodeListens.entrySet()) {
                int result = entry.getKey().compareTo(maxHost);

                if (((entry.getValue() > max) || (entry.getValue() == max && result < 0))
                        && !top5Episode.contains(entry.getKey())) {
                    max = entry.getValue();
                    maxHost = entry.getKey();
                }
            }
            top5Episode.add(maxHost);
        }

        for (String episode : top5Episode) {
            jsonObject.put(episode, episodeListens.get(episode));
        }
        return jsonObject;


    }

    /**
     * listeners of the user
     */
    public int getListeners() {
        ObjectMapper objectMapper = new ObjectMapper();
        int count = 0;
        ObjectNode jsonObject = objectMapper.createObjectNode();
        Map<String, Integer> fanListens = new HashMap<>();
        for (User user : Library.getDatabase().getUsers()) {
            if (!user.getEpisodeListen().isEmpty()) {
                for (Episode episode : user.getListenedEpisodes()) {
                    if (Objects.requireNonNull(episode.searchHostbyEpisode()).
                            getUsername().equals(this.username)) {
                        if (!fanListens.containsKey(user.getUsername())) {
                            count++;
                            int countEpisode = user.getEpisodeListenCount(episode);
                            int countElement = countEpisode;
                            fanListens.put(user.getUsername(), countElement);
                        }
                    }
                }
            }
        }


        return count;


    }
    /**
     * removes all podcasts
     */
    public void removeAllPodcasts() {
        for (Podcast podcast : podcasts) {
            Library.getDatabase().getPodcasts().removeIf(podcast2 -> podcast2.getName().
                    equals(podcast.getName()));
        }
        podcasts.clear();
    }

    /**
     * add a certain podcast
     * @param podcast2 the podcast with its episodes
     */
    public void addPodcast(final Podcast podcast2) {
        podcasts.add(podcast2);
    }

    /**
     * checks if the announcement exists
     * @param name the name of the
     *             supposed announcement
     * @return true or false
     */
    public boolean checkIfAnnouncementExists(final String name) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}

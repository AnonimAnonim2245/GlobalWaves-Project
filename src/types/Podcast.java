package types;

import status.playerLoad;
import visitor.Visitor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Aici avem functia de podcast, unde store-im mai multe informatii precum episodul pe care
 * il avem in load currently(in status)
 * Avem mai mai multe informatii precum numarul de episoade, ownerul.
 */
@Getter
public final class Podcast {
    private String name;
    private String owner;
    private ArrayList<Episode> episodes;
    private int numberEpisode;


    public Podcast() {
    }

    public Podcast(final String name, final String owner,
                   final ArrayList<Episode> episodes, final int numberEpisode) {
        this.name = name;
        this.owner = owner;
        this.episodes = episodes;
        this.numberEpisode = numberEpisode;
    }

    /**
     * adds a podcast if the user is a host or the name is valid
     *
     * @return the json object
     * @throws IOException it is for the object mapper
     */
    public JsonNode addPodcastJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {

            if (Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " is not a host.");

            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
            }
        } else {
            if (Library.getDatabase().checkIfPodcastExists(jsonElement.getName())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " has another podcast with the same name.");
            } else {

                Host host = Library.getDatabase().getHostInstance(jsonElement.getUsername());

                int size = jsonElement.getEpisodes().size();
                Podcast podcast = new Podcast(jsonElement.getName(), jsonElement.getUsername(),
                        jsonElement.getEpisodes(), size);
                this.name = podcast.name;
                this.owner = podcast.owner;
                this.episodes = podcast.episodes;
                this.numberEpisode = podcast.numberEpisode;

                String episodesString = objectMapper.writeValueAsString(jsonElement.getEpisodes());
                TypeReference<ArrayList<Episode>> typeReferenceEpisode = new TypeReference<>() {
                };
                ArrayList<Episode> episodeClass = objectMapper.readValue(episodesString,
                        typeReferenceEpisode);

                for (int i = 0; i <= episodeClass.size() - 2; i++) {
                    for (int j = i + 1; j <= episodeClass.size() - 1; j++) {
                        if (episodeClass.get(i).getName().equals(episodeClass.get(j).getName())) {
                            jsonObject.put("message",
                                    jsonElement.getUsername()
                                            + " has the same episode in this podcast.");
                            return jsonObject;
                        }
                    }
                }


                Library.getDatabase().addPodcast(this);
                host.addPodcast(this);
                for (User user : Library.getDatabase().getUsers()) {
                    int i = 0;
                    for (Host host2 : user.getHostsNotification()) {
                        if (host2.getUsername().equals(jsonElement.getUsername())) {
                            user.getNotification().add("Podcast  " + jsonElement.getName()
                                    + "  " + jsonElement.getUsername());
                            break;
                        }

                        i++;
                    }
                }
                jsonObject.put("message",
                        jsonElement.getUsername() + " has added new podcast successfully.");
            }
        }
        Library.getDatabase().setHostElement(jsonElement.getUsername());
        Library.getDatabase().setPageStatus("HOST");


        return jsonObject;
    }

    /**
     * shows all the current podcasts returned by a host user
     *
     * @return the json format of it
     */
    public JsonNode showPodcastjson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        ArrayList<ObjectNode> nodes = new ArrayList<>();
        for (Podcast podcast : Library.getDatabase().
                getHostInstance(jsonElement.getUsername()).getPodcasts()) {
            ObjectNode smallJsonObject = objectMapper.createObjectNode();

            smallJsonObject.put("name", podcast.getName());
            ArrayNode outputs = objectMapper.createArrayNode();
            for (Episode episode : podcast.getEpisodes()) {
                outputs.add(episode.getName());
            }
            smallJsonObject.set("episodes", outputs);
            nodes.add(smallJsonObject);


        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (ObjectNode node : nodes) {

            arrayNode.add(node);
        }
        jsonObject.put("result", arrayNode);
        Library.getDatabase().setHostElement(jsonElement.getUsername());
        Library.getDatabase().setPageStatus("HOST");

        return jsonObject;
    }

    /**
     * removes the podcasts, but it checks if it is
     * a host or if the podcast exists
     *
     * @return the json format of it
     */
    public JsonNode removePodcastJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());
        jsonObject.put("user", jsonElement.getUsername());
        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!Library.getDatabase().checkIfHostValid(jsonElement.getUsername())) {

            if (Library.getDatabase().checkifArtistInstance(jsonElement.getUsername())
                    || Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " not a host.");

            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
            }
        } else {
            if (!Library.getDatabase().checkIfPodcastExists(jsonElement.getName())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " doesn't have a podcast with the given name.");
            } else {
                for (User user : Library.getDatabase().getUsers()) {
                    if (user.getUserDatabase().getLoadedElement().equals(jsonElement.getName())) {
                        jsonObject.put("message",
                                jsonElement.getUsername() + " can't delete this podcast.");
                        return jsonObject;
                    }
                }

                Host host = Library.getDatabase().getHostInstance(jsonElement.getUsername());


                Library.getDatabase().getPodcasts().removeIf(podcast ->
                        podcast.getName().equals(jsonElement.getName()));
                for (Podcast podcast : host.getPodcasts()) {
                    if (podcast.getName().equals(jsonElement.getName())) {
                        podcast.getEpisodes().clear();
                        break;
                    }
                }
                host.getPodcasts().removeIf(podcast -> podcast.getName().
                        equals(jsonElement.getName()));

                for (User user : Library.getDatabase().getUsers()) {
                    int i = 0;
                    for (Host host2 : user.getHostsNotification()) {
                        if (host2.getUsername().equals(jsonElement.getUsername())) {
                            user.getNotification().remove("Podcast  " + jsonElement.getName()
                                    + "  " + jsonElement.getUsername());
                            break;
                        }

                        i++;
                    }
                }
                jsonObject.put("message", jsonElement.getUsername()
                        + " deleted the podcast successfully.");
            }
        }

        return jsonObject;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    /**
     * we use the accept method in order to implement the visitor
     * design pattern for status json within status class
     *
     * @param visitor    the visitor which can be either a
     *                   playlist, album, song or podcast
     * @param playerLoad the loaded element
     * @param user       the current user
     * @param newI       the current timestamp
     * @return the element returned by the function
     */
    public playerLoad acceptNegativeRemaininingTimeForStatusJson(final Visitor visitor,
             final playerLoad playerLoad, final User user, final Integer newI) {
        return visitor.visitNegativeRemaininingTimeForStatusJson(this, playerLoad, user, newI);
    }
}

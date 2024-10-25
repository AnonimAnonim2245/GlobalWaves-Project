package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;
import lombok.Getter;
import lombok.Setter;

/**
 * Announcement class
 * with name and description
 * and the methods to add and remove announcements
 */
public class Announcement {

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;

    public Announcement() {
    }

    public Announcement(final String name2, final String description2) {
        this.name = name2;
        this.description = description2;

    }

    /**
     * add announcement
     * checks if the user is a host
     * checks if the announcement exists
     *
     * @return the jsonnode
     */
    public JsonNode announcementJson() {
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
                return jsonObject;
            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
                return jsonObject;
            }
        }
        if (Library.getDatabase().getHostInstance(jsonElement.getUsername()).
                checkIfAnnouncementExists(jsonElement.getUsername())) {
            jsonObject.put("message",
                    jsonElement.getUsername()
                            + " has already added an announcement with this name.");
            return jsonObject;
        }
        Announcement announcement = new Announcement(jsonElement.getName(),
                jsonElement.getDescription());
        Library.getDatabase().getHostInstance(jsonElement.getUsername()).
                getAnnouncements().add(announcement);
        for (User user : Library.getDatabase().getUsers()) {
            int i = 0;
            for (Host host : user.getHostsNotification()) {
                if (host.getUsername().equals(jsonElement.getUsername())) {
                    user.getNotification().add("Announcement  " + jsonElement.getName()
                            + "  " + jsonElement.getUsername());
                    break;
                }

                i++;
            }
        }
        jsonObject.put("message", jsonElement.getUsername()
                + " has successfully added new announcement.");
        return jsonObject;
    }

    /**
     * remove announcement
     * check if the user is a host
     * check if the announcement does not exist
     *
     * @return jsonnode
     */
    public JsonNode removeannouncementJson() {
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
                return jsonObject;
            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
                return jsonObject;
            }
        }
        int original = Library.getDatabase().
                getHostInstance(jsonElement.getUsername()).getAnnouncements().size();
        Library.getDatabase().getHostInstance(jsonElement.getUsername()).getAnnouncements().
                removeIf(announcement -> announcement.getName().equals(jsonElement.getName()));
        int newSize = Library.getDatabase().
                getHostInstance(jsonElement.getUsername()).getAnnouncements().size();
        if (original == newSize) {
            jsonObject.put("message",
                    jsonElement.getUsername() + " has no announcement with the given name.");
            return jsonObject;
        }
        Library.getDatabase().setHostElement(jsonElement.getUsername());
        Library.getDatabase().setPageStatus("HOST");
        for (User user : Library.getDatabase().getUsers()) {
            int i = 0;
            for (Host host : user.getHostsNotification()) {
                if (host.getUsername().equals(jsonElement.getUsername())) {
                    user.getNotification().remove("Announcement  " + jsonElement.getName()
                            + "  " + jsonElement.getUsername());
                    break;
                }

                i++;
            }
        }
        jsonObject.put("message",
                jsonElement.getUsername() + " has successfully deleted the announcement.");
        return jsonObject;
    }
}

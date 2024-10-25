package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;
import lombok.Getter;

@Getter
/**
 * We have the merch function here, where we store the name,
 * price and description of the merch.
 * We also have the merchJson function, where we add the merch if the
 * user is an artist or the name is valid.
 */
public class Merch {

    private String name;
    private Integer price;
    private String description;

    public Merch() {
    }

    public Merch(final String name2, final Integer price2,
                 final String description2) {
        this.name = name2;
        this.price = price2;
        this.description = description2;
    }

    /**
     * adds a merch if the user is an artist or the name is valid
     *
     * @return the json object
     */
    public JsonNode merchJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());

        jsonObject.put("user", jsonElement.getUsername());

        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {
            if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
                jsonObject.put("message",
                        jsonElement.getUsername() + " is not an artist.");
                return jsonObject;
            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
                return jsonObject;
            }
        }
        if (Library.getDatabase().getArtistInstance(jsonElement.getUsername()).
                checkIfMerchExists(jsonElement.getName())) {
            jsonObject.put("message",
                    jsonElement.getUsername() + " has merchandise with the same name.");
            return jsonObject;
        }
        if (jsonElement.getPrice() < 0) {
            jsonObject.put("message", "Price for merchandise can not be negative.");
            return jsonObject;
        }
        Library.getDatabase().setArtistElement(jsonElement.getUsername());
        Library.getDatabase().setPageStatus("ARTIST");
        Merch merch = new Merch(jsonElement.getName(),
                jsonElement.getPrice(), jsonElement.getDescription());
        Library.getDatabase().
                getArtistInstance(jsonElement.getUsername()).getMerchList().add(merch);
        jsonObject.put("message",
                jsonElement.getUsername() + " has added new merchandise successfully.");

        for (User user : Library.getDatabase().getUsers()) {
            int i = 0;
            for (Artist artist : user.getArtistsNotification()) {
                if (artist.getUsername().equals(jsonElement.getUsername())) {
                    user.getNotification().add("Merchandise  " + merch.getName()
                            + "  " + artist.getUsername());
                    break;
                }

                i++;
            }
        }
        return jsonObject;

    }
}

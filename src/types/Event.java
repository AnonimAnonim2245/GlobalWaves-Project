package types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import json.JsonConvertToCategories;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Event class
 * with name, date and description
 * and the methods to add and remove events
 */
public class Event {
    @Setter
    @Getter
    private String name;
    @Getter
    @Setter
    private String date;
    @Getter
    @Setter
    private String description;
    private static final Integer EVENMONTHNUMBER = 30;
    private static final Integer ODDMONTHNUMBER = 31;
    private static final Integer FEBRUARY = 2;
    private static final Integer LEAPYEAR = 29;
    private static final Integer NOTLEAPYEAR = 28;
    private static final Integer APRIL = 4;
    private static final Integer JUNE = 6;
    private static final Integer SEPTEMBER = 9;
    private static final Integer NOVEMBER = 11;

    /**
     * verify if the date is valid
     * ysubg the matcher pattern format
     * implemented with chatGpt
     *
     * @param dateString the current date
     * @return
     */
    public static boolean isFormatDateValid(final String dateString) {
        String regex = "^\\d{2}-\\d{2}-\\d{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dateString);
        return matcher.matches();
    }

    /**
     * verify if the date is valid
     *
     * @param dateString2 the current date
     * @return true or false
     * implemented with Chatgpt
     */
    public static boolean isValidDate(final String dateString2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

        try {
            LocalDate date = LocalDate.parse(dateString2, formatter);

            if (date.getMonthValue() == FEBRUARY) {
                if (date.isLeapYear() && date.getDayOfMonth() > LEAPYEAR) {
                    return false;
                } else if (!date.isLeapYear() && date.getDayOfMonth() > NOTLEAPYEAR) {
                    return false;
                }
            } else {
                if (date.getMonthValue() == APRIL || date.getMonthValue() == JUNE
                        || date.getMonthValue() == SEPTEMBER || date.getMonthValue() == NOVEMBER) {
                    if (date.getDayOfMonth() > EVENMONTHNUMBER) {
                        return false;
                    }
                } else {
                    if (date.getDayOfMonth() > ODDMONTHNUMBER) {
                        return false;
                    }
                }

            }
            return true;

        } catch (DateTimeParseException e) {
            return false;
        }

    }

    public Event() {

    }

    public Event(final String name2, final String date2,
                 final String description2) {
        this.name = name2;
        this.date = date2;
        this.description = description2;

    }

    /**
     * add event, checks if the artist
     * is valid or if the event exists already
     *
     * @return THE JSON STATUS
     */
    public JsonNode eventJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());

        jsonObject.put("user", jsonElement.getUsername());

        jsonObject.put("timestamp", jsonElement.getTimestamp());
        if (!isFormatDateValid(jsonElement.getDate())) {
            jsonObject.put("message", "Event for " + jsonElement.getUsername()
                    + " does not have a valid date.");
            return jsonObject;
        }
        if (!isValidDate(jsonElement.getDate())) {
            jsonObject.put("message", "Event for " + jsonElement.getUsername()
                    + " does not have a valid date.");
            return jsonObject;
        }
        if (!Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {
            if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
                jsonObject.put("message", jsonElement.getUsername() + " is not an artist.");
                return jsonObject;
            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
                return jsonObject;
            }
        }
        if (Library.getDatabase().getArtistInstance(jsonElement.getUsername()).
                checkIfEventExists(jsonElement.getUsername())) {
            jsonObject.put("message",
                    "Event for " + jsonElement.getUsername() + " already exists.");
            return jsonObject;
        }
        Event event = new Event(jsonElement.getName(),
                jsonElement.getDate(), jsonElement.getDescription());
        Library.getDatabase().
                getArtistInstance(jsonElement.getUsername()).getEvents().add(event);

        Library.getDatabase().setArtistElement(jsonElement.getUsername());
        Library.getDatabase().setPageStatus("ARTIST");
        for (User user : Library.getDatabase().getUsers()) {
            int i = 0;
            for (Artist artist : user.getArtistsNotification()) {
                if (artist.getUsername().equals(jsonElement.getUsername())) {
                    user.getNotification().add("Event  " + event.getName()
                            + "  " + artist.getUsername());
                    break;
                }

                i++;
            }
        }
        jsonObject.put("message",
                jsonElement.getUsername() + " has added new event successfully.");
        return jsonObject;
    }

    /**
     * remove event, checks if the artist is valid or
     * if the event does not exist
     *
     * @return the json
     */
    public JsonNode removeEventJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonObject = objectMapper.createObjectNode();
        JsonConvertToCategories jsonElement = Library.getDatabase().getJson();
        jsonObject.put("command", jsonElement.getCommand());

        jsonObject.put("user", jsonElement.getUsername());

        jsonObject.put("timestamp", jsonElement.getTimestamp());

        if (!Library.getDatabase().checkIfArtistValid(jsonElement.getUsername())) {
            if (Library.getDatabase().checkIfHostValid(jsonElement.getUsername())
                    || Library.getDatabase().checkIfNormalUserValid(jsonElement.getUsername())) {
                jsonObject.put("message", jsonElement.getUsername() + " is not an artist.");
                return jsonObject;
            } else {
                jsonObject.put("message",
                        "The username " + jsonElement.getUsername() + " doesn't exist.");
                return jsonObject;
            }
        }
        int original = Library.getDatabase().
                getArtistInstance(jsonElement.getUsername()).getEvents().size();
        Library.getDatabase().getArtistInstance(jsonElement.getUsername()).
                getEvents().removeIf(event ->
                        event.getName().equals(jsonElement.getName()));
        int newSize = Library.getDatabase().getArtistInstance(jsonElement.
                getUsername()).getEvents().size();
        if (original == newSize) {
            jsonObject.put("message",
                    jsonElement.getUsername() + " has no event with the given name.");
            return jsonObject;
        }
        Library.getDatabase().setArtistElement(jsonElement.getUsername());
        Library.getDatabase().setPageStatus("ARTIST");
        for (User user : Library.getDatabase().getUsers()) {
            int i = 0;
            for (Artist artist : user.getArtistsNotification()) {

                if (artist.getUsername().equals(jsonElement.getUsername())) {
                    user.getNotification().remove("Event  " + jsonElement.getName()
                            + "  " + artist.getUsername());
                    break;
                }
                i++;
            }
        }
        jsonObject.put("message",
                jsonElement.getUsername() + " deleted the event successfully.");
        return jsonObject;
    }
}

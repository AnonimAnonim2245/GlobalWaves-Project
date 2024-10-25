package command;
import com.fasterxml.jackson.databind.JsonNode;
import types.User;
import java.io.IOException;

/**
 * Command interface
 */
public interface Command {
    /**
     * @param user the user that executes
     * the command
     * @return the json node
     * @throws IOException exception
     */
    JsonNode execute(User user) throws IOException;
}

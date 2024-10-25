package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import command.Command;
import command.CommandFactory;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.CommandRunner;
import json.JsonConvertToCategories;
import json.JsonList;
import types.Library;
import types.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {

    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    public static String getLibraryPath() {
        return LIBRARY_PATH;
    }

    /**
     * for coding style
     */

    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);
        int i = 0;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();

            if (isCreated) {
                action(file.getName(), filepath);
                i++;
            }


        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput  for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to
     * reading / writing
     * first we take the elements from our function and
     * take them from LIBRARY_PATH, which is the input
     * with information about everything, we put them in
     * what we need and put them in our common library
     * database, we put songs, podcasts, users and then
     * we convert the users into hash map after we take
     * the jsons from checker constants
     * of tests, and using the JsonConvertToCategories class
     * we convert them in our class into what we need, and
     * then
     * we take according to our user given by json a database
     * for each user and what order we want to implement, we
     * have changed_timestamp that marks if we have to
     * synchronize
     * the song from load with the timestamp with the
     * move_time_stamp
     * function, in the case of search (in which we have
     * to do this
     * before [to shuffled, prev,next,like,repeat] to
     * synchronize to
     * whom we do the respective action, to status,load,
     * paused
     * playPaused
     * in which we do the synchronization inside the function,
     * for the
     * rest of the commands we set them with changed_timestamp
     *with false to synchronize them and send the final json,
     * after deleting the data, and resetting our statuses and the
     * elements we put in loaded or selected
     */
    public static void action(final String filePathInput,
                              final String filePathOutput)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Library librarycopy = objectMapper.readValue(new File(LIBRARY_PATH),
                Library.class);
        Library.getDatabase().setSongs(librarycopy.getSongs());
        Library.getDatabase().setPodcasts(librarycopy.getPodcasts());
        Library.getDatabase().setUsers(librarycopy.getUsers());
        Library.getDatabase().convertListToMap();
        ArrayNode outputs = objectMapper.createArrayNode();

        JsonList.readJsonFile(CheckerConstants.TESTS_PATH + filePathInput);
        List<JsonConvertToCategories> list = Library.getDatabase().getJsonList();

        for (JsonConvertToCategories element : list) {
            User user = Library.getDatabase().getUserInstance(element.getUsername());
            assert user != null;
            Library.getDatabase().setJson(element);

            Command command = CommandFactory.getCommand(element.getCommand());
            if (command != null) {
                JsonNode result = command.execute(user);
                outputs.add(result);
            }
            user.setPrevtime(element.getTimestamp());

        }
        outputs.add(CommandRunner.endProgramCommand());
        CommandRunner.removePreviousData();

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}

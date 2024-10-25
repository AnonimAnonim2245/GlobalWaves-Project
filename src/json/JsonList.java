package json;

import java.io.IOException;

import types.Library;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
/**
 * this function is used to convert
 * the json elements from the tests, to put them in our list
 * of json from the library we do with object mapper
 */
public final class JsonList {

    private JsonList() {
        throw new UnsupportedOperationException("This is a utility class"
                + " and cannot be instantiated");
    }

    /**
     * reads the json file and converts it
     *
     * @param filePath fisierul
     * @throws IOException exceptia
     */
    public static void readJsonFile(final String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath);
        Library.getDatabase().addJsonList(objectMapper.readValue(jsonFile, new TypeReference<>() {
        }));
    }
}

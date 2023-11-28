package HTTPServer.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class UserJsonUtil {
    //converts JSON username and password into Map<String, String>
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, String> jsonToMap(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, Map.class);
    }
}

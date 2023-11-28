package HTTPServer.Utils;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestUtils {
    //util class to convert GET parameters into Username & Password
    public static Map<String, String> parseQueryString(String query) {
        Map<String, String> parameters = new HashMap<>();

        if (query != null) {
            String[] pairs = query.split("&");

            for (String pair : pairs) {
                String[] keyValue = pair.split("=");

                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : null;

                parameters.put(key, value);
            }
        }

        return parameters;
    }


}

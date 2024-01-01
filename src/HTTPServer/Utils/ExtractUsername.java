package HTTPServer.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractUsername {
    public static String extract(String token) {
        // Define a regular expression pattern to match the username
        String pattern = "\\[Bearer\\s+([^\\]-]+)-mtcgToken\\]";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher m = r.matcher(token);

        // Check if a match is found
        if (m.find()) {
            // Return the username from the first capturing group
            return m.group(1);
        } else {
            // Return null if no match is found
            return null;
        }
    }
}

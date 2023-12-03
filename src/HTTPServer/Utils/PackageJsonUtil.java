package HTTPServer.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackageJsonUtil {
    public static List<Triple<String, String, Double>> convertJsonToTriplets(String jsonString) {
        List<Triple<String, String, Double>> triplets = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            for (JsonNode node : jsonNode) {
                String id = node.get("Id").asText();
                String name = node.get("Name").asText();
                Double damage = node.get("Damage").asDouble();

                triplets.add(Triple.of(id, name, damage));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return triplets;
    }
}

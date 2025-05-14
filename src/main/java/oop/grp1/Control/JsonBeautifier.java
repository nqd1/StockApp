package oop.grp1.Control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonBeautifier {
    public static String beautify(String rawJson) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(rawJson);
        return gson.toJson(jsonElement);
    }
}

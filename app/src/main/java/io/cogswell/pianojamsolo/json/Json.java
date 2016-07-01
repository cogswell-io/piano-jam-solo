package io.cogswell.pianojamsolo.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by jedwards on 5/3/16.
 */
public class Json {
    public static JsonNode parse(String json) {
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject root = new JSONObject(tokener);
            return new JsonObject(root);
        } catch (JSONException e) {
            return JsonNull.singleton;
        }
    }
}

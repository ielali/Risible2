package risible.json;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 10/15/13
 * Time: 7:09 PM
 * To change this template use File | Settings | File Templates.
 */
class ExceptionSerializer implements JsonSerializer<Throwable> {
    @Override
    public JsonElement serialize(Throwable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cause", new JsonPrimitive(String.valueOf(src.getCause())));
        jsonObject.add("message", new JsonPrimitive(String.valueOf(src.getMessage())));
        return jsonObject;
    }
}

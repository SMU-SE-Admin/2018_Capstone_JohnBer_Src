package smu.ac.kr.johnber.util;

import android.location.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * sharedPreference에 ArrayList<Location> 저장을 위한 parser
 */
public class LocationtoJsonUtil {
    class LocationSerializer implements JsonSerializer<Location> {
        public JsonElement serialize(Location t, Type type,
                                     JsonSerializationContext jsc) {
            JsonObject jo = new JsonObject();
            jo.addProperty("mProvider", t.getProvider());
            jo.addProperty("mAccuracy", t.getAccuracy());
            // etc for all the publicly available getters
            // for the information you're interested in
            // ...
            return jo;
        }

    }


    class LocationDeserializer implements JsonDeserializer<Location> {
        public Location deserialize(JsonElement je, Type type,
                                    JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonObject jo = je.getAsJsonObject();
            Location l = new Location(jo.getAsJsonPrimitive("mProvider").getAsString());
            l.setAccuracy(jo.getAsJsonPrimitive("mAccuracy").getAsFloat());
            // etc, getting and setting all the data
            return l;
        }


    }
}

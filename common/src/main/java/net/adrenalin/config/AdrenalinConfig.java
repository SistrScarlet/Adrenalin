package net.adrenalin.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.architectury.platform.Platform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.OptionalInt;

public class AdrenalinConfig {
    private static final AdrenalinConfig INSTANCE = new AdrenalinConfig();
    private boolean enable = true;
    private float maxTimeStasisFactor = 0.5f;
    private int adrenalinDecay = 100;
    private float lowHealthThreshold = 0.5f;
    private float continuousDamageThreshold = 0.2f;
    private int continuousDamageDecay = 100;
    private boolean chunkBaseTimeStasis = false;

    public static AdrenalinConfig getINSTANCE() {
        return INSTANCE;
    }

    public void load() {
        Path path = Platform.getConfigFolder().resolve(Paths.get("adrenalin.json"));
        if (!Files.isReadable(path)) {
            return;
        }
        try (var reader = new JsonReader(Files.newBufferedReader(path))) {
            var jsonElement = Streams.parse(reader);
            if (jsonElement.isJsonObject()) {
                var jsonObject = (JsonObject) jsonElement;
                getBoolean(jsonObject.get("enable")).ifPresent(this::setEnable);
                getFloat(jsonObject.get("maxTimeStasisFactor")).ifPresent(this::setMaxTimeStasisFactor);
                getInt(jsonObject.get("adrenalinDecay")).ifPresent(this::setAdrenalinDecay);
                getFloat(jsonObject.get("lowHealthThreshold")).ifPresent(this::setLowHealthThreshold);
                getFloat(jsonObject.get("continuousDamageThreshold")).ifPresent(this::setContinuousDamageThreshold);
                getInt(jsonObject.get("continuousDamageDecay")).ifPresent(this::setContinuousDamageDecay);
                getBoolean(jsonObject.get("chunkBaseTimeStasis")).ifPresent(this::setChunkBaseTimeStasis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        Path path = Platform.getConfigFolder().resolve(Paths.get("adrenalin.json"));
        try (var writer = new JsonWriter(Files.newBufferedWriter(path))) {
            JsonObject element = new JsonObject();
            element.add("enable", new JsonPrimitive(this.isEnable()));
            element.add("maxTimeStasisFactor", new JsonPrimitive(this.getMaxTimeStasisFactor()));
            element.add("adrenalinDecay", new JsonPrimitive(this.getAdrenalinDecay()));
            element.add("lowHealthThreshold", new JsonPrimitive(this.getLowHealthThreshold()));
            element.add("continuousDamageThreshold", new JsonPrimitive(this.getContinuousDamageThreshold()));
            element.add("continuousDamageDecay", new JsonPrimitive(this.getContinuousDamageDecay()));
            element.add("chunkBaseTimeStasis", new JsonPrimitive(this.isChunkBaseTimeStasis()));
            Streams.write(element, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<Boolean> getBoolean(JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean()) {
            return Optional.of(jsonElement.getAsBoolean());
        }
        return Optional.empty();
    }

    public Optional<Float> getFloat(JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return Optional.of(jsonElement.getAsFloat());
        }
        return Optional.empty();
    }

    public OptionalInt getInt(JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return OptionalInt.of(jsonElement.getAsInt());
        }
        return OptionalInt.empty();
    }

    public float getMaxTimeStasisFactor() {
        return maxTimeStasisFactor;
    }

    public int getAdrenalinDecay() {
        return adrenalinDecay;
    }

    public float getLowHealthThreshold() {
        return lowHealthThreshold;
    }

    public float getContinuousDamageThreshold() {
        return continuousDamageThreshold;
    }

    public int getContinuousDamageDecay() {
        return continuousDamageDecay;
    }

    public void setMaxTimeStasisFactor(Float newValue) {
        this.maxTimeStasisFactor = newValue;
    }

    public void setMaxTimeStasisFactor(float maxTimeStasisFactor) {
        this.maxTimeStasisFactor = maxTimeStasisFactor;
    }

    public void setAdrenalinDecay(int adrenalinDecay) {
        this.adrenalinDecay = adrenalinDecay;
    }

    public void setLowHealthThreshold(float lowHealthThreshold) {
        this.lowHealthThreshold = lowHealthThreshold;
    }

    public void setContinuousDamageThreshold(float continuousDamageThreshold) {
        this.continuousDamageThreshold = continuousDamageThreshold;
    }

    public void setContinuousDamageDecay(int continuousDamageDecay) {
        this.continuousDamageDecay = continuousDamageDecay;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public float getLowHealthLowLimit() {
        return 0.1f;
    }

    public float getContinuousDamageMaxLimit() {
        return 0.5f;
    }

    public float getContinuousDamageLowLimit() {
        return 0.1f;
    }

    public boolean isChunkBaseTimeStasis() {
        return chunkBaseTimeStasis;
    }

    public void setChunkBaseTimeStasis(boolean chunkBaseTimeStasis) {
        this.chunkBaseTimeStasis = chunkBaseTimeStasis;
    }
}

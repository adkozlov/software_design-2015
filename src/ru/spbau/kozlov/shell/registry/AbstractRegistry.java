package ru.spbau.kozlov.shell.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author adkozlov
 */
public abstract class AbstractRegistry<T> {

    @NotNull
    private final Map<String, T> keysToValues = new HashMap<>();
    @NotNull
    private final Map<T, String> valuesToKeys = new HashMap<>();

    protected void register(@NotNull String key, @NotNull T value) {
        if (keysToValues.containsKey(key)) {
            throw new RuntimeException(value + " is already registered");
        }
        keysToValues.put(key, value);
        valuesToKeys.put(value, key);
    }

    public void unregister(@NotNull T value) {
        if (!valuesToKeys.containsKey(value)) {
            throw new RuntimeException(value + " is not yet registered");
        }
        String className = valuesToKeys.remove(value);
        keysToValues.remove(className);
    }

    @Nullable
    public T get(@NotNull String key) {
        return keysToValues.get(key);
    }

    @Nullable
    public String getKey(@NotNull T value) {
        return valuesToKeys.get(value);
    }
}

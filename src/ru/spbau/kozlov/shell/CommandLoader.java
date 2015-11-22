package ru.spbau.kozlov.shell;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.commands.Command;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author adkozlov
 */
public class CommandLoader {

    private CommandLoader() {
    }

    @NotNull
    public static Class<? extends Command> loadCommand(@NotNull ClassLoader classLoader, @NotNull String className) {
        try {
            return Class.forName(className, true, classLoader).asSubclass(Command.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Stream<Class<? extends Command>> loadCommands(@NotNull String classPath, @NotNull String... classNames) {
        ClassLoader classLoader = getClassLoader(classPath);
        return Arrays.stream(classNames).map(className -> loadCommand(classLoader, className));
    }

    @NotNull
    private static ClassLoader getClassLoader(@NotNull String classPath) {
        try {
            return new URLClassLoader(new URL[]{Paths.get(classPath).toUri().toURL()});
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}

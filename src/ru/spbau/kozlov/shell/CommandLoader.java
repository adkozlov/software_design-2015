package ru.spbau.kozlov.shell;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.commands.Command;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author adkozlov
 */
public class CommandLoader {

    @NotNull
    private static final CommandLoader INSTANCE = new CommandLoader();

    private CommandLoader() {
    }

    @NotNull
    public static CommandLoader getInstance() {
        return INSTANCE;
    }

    @NotNull
    private static ClassLoader getClassLoader(@NotNull String classPath) {
        try {
            return new URLClassLoader(new URL[]{Paths.get(classPath).toUri().toURL()});
        } catch (MalformedURLException e) {
            // cannot be
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Class<? extends Command> loadCommand(@NotNull String className, @NotNull ClassLoader classLoader) {
        try {
            return Class.forName(className, true, classLoader).asSubclass(Command.class);
        } catch (ClassNotFoundException e) {
            // cannot be
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public List<Class<? extends Command>> loadCommands(@NotNull List<String> classNames, @NotNull String classPath) {
        ClassLoader classLoader = getClassLoader(classPath);
        return classNames.stream().map(className -> loadCommand(className, classLoader)).collect(Collectors.toList());
    }
}

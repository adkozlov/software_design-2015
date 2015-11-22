package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.invoker.ExecutionException;
import ru.spbau.kozlov.shell.api.invoker.PrintUsageException;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author adkozlov
 */
public abstract class AbstractCommand implements Command {

    @Override
    public void execute(@NotNull InputStream inputStream,
                        @NotNull PrintStream outputStream,
                        @NotNull List<String> arguments) throws ExecutionException {
        try {
            if (arguments.isEmpty() && inputStream.available() == 0) {
                throw new PrintUsageException(getCommandName());
            }

            if (!arguments.isEmpty()) {
                handleArguments(arguments, outputStream);
            } else {
                handleInputStream(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new ExecutionException(getCommandName(), "I/O error occurred", e);
        }
    }

    protected abstract void handleInputStream(@NotNull InputStream inputStream,
                                              @NotNull PrintStream outputStream) throws IOException;

    protected abstract void handleArgument(@NotNull String fileName,
                                           @NotNull PrintStream outputStream) throws IOException;

    private void handleArguments(@NotNull List<String> arguments,
                                 @NotNull PrintStream outputStream) throws IOException {
        for (String argument : arguments) {
            try {
                handleArgument(argument, outputStream);
            } catch (FileNotFoundException e) {
                printError(e, outputStream);
            }
        }
    }

    protected static void forEachLine(@NotNull InputStream inputStream,
                                      @NotNull Predicate<String> filter,
                                      @NotNull Consumer<String> consumer) throws IOException {
        if (inputStream.available() != 0) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                bufferedReader.lines().filter(filter).forEach(consumer);
            }
        }
    }

    protected static void forEachLine(@NotNull InputStream inputStream,
                                      @NotNull Consumer<String> consumer) throws IOException {
        forEachLine(inputStream, (s -> true), consumer);
    }

    protected static void forEachLine(@NotNull String fileName,
                                      @NotNull Predicate<String> filter,
                                      @NotNull Consumer<String> consumer) throws IOException {
        forEachLine(new FileInputStream(fileName), filter, consumer);
    }

    protected static void forEachLine(@NotNull String fileName,
                                      @NotNull Consumer<String> consumer) throws IOException {
        forEachLine(fileName, (s -> true), consumer);
    }
}

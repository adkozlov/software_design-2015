package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.invoker.ExecutionException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author adkozlov
 */
@FunctionalInterface
public interface Command {

    void execute(@NotNull InputStream inputStream,
                 @NotNull PrintStream outputStream,
                 @NotNull List<String> arguments) throws ExecutionException;

    @NotNull
    default String getCommandName() {
        return getClass().getAnnotation(CommandName.class).value();
    }

    @NotNull
    default String getUsageMessage() {
        return "";
    }

    default void printUsage(@NotNull PrintStream outputStream) {
        outputStream.printf("usage: %s %s", getCommandName(), getUsageMessage());
        outputStream.println();
    }

    default void printError(@NotNull Throwable throwable, @NotNull PrintStream errorStream) {
        errorStream.printf("%s: %s", getCommandName(), throwable.getMessage());
        errorStream.println();
        Arrays.stream(throwable.getSuppressed()).forEach(suppressed -> printError(suppressed, errorStream));
    }
}

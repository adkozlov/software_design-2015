package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.executions.Executable;

import java.io.PrintStream;
import java.util.List;

/**
 * @author adkozlov
 */
public interface Command extends Executable {

    int OK_EXIT_CODE = 0;

    @NotNull
    List<String> getArguments();

    void setArguments(@NotNull List<String> arguments);

    @NotNull
    default String getCommandName() {
        return getClass().getAnnotation(CommandName.class).value();
    }

    default void printUsage(@NotNull String usageMessage, @NotNull PrintStream outputStream) {
        outputStream.printf("usage: %s %s", getCommandName(), usageMessage);
        outputStream.println();
    }

    default void printError(@NotNull Exception exception, @NotNull PrintStream errorStream) {
        errorStream.printf("%s: %s", getCommandName(), exception);
        errorStream.println();
    }
}

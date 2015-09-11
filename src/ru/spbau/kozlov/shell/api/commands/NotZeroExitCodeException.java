package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.executions.ExecutionException;

/**
 * @author adkozlov
 */
public final class NotZeroExitCodeException extends ExecutionException {

    private final int exitCode;

    public NotZeroExitCodeException(int exitCode, @NotNull String commandName) {
        super(commandName);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}

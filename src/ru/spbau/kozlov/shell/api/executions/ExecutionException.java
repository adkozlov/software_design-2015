package ru.spbau.kozlov.shell.api.executions;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class ExecutionException extends Exception {

    @NotNull
    private final String executableName;

    protected ExecutionException(@NotNull String executableName) {
        this.executableName = executableName;
    }

    public ExecutionException(@NotNull String executableName, @NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
        this.executableName = executableName;
    }

    public ExecutionException(@NotNull String executableName, @NotNull Throwable cause) {
        super(cause);
        this.executableName = executableName;
    }

    @NotNull
    public String getExecutableName() {
        return executableName;
    }
}
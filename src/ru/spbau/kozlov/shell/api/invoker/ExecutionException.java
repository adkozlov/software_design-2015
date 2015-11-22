package ru.spbau.kozlov.shell.api.invoker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author adkozlov
 */
public class ExecutionException extends Exception {

    @NotNull
    private final String executableName;

    public ExecutionException(@NotNull String executableName, @NotNull String message, @Nullable Throwable cause) {
        super(message, cause);
        this.executableName = executableName;
    }

    @NotNull
    public String getExecutableName() {
        return executableName;
    }
}
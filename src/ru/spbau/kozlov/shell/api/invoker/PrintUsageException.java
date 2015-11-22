package ru.spbau.kozlov.shell.api.invoker;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class PrintUsageException extends ExecutionException {

    public PrintUsageException(@NotNull String executableName) {
        super(executableName, "Print usage", null);
    }
}

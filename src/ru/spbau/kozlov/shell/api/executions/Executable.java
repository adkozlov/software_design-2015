package ru.spbau.kozlov.shell.api.executions;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
@FunctionalInterface
public interface Executable {

    void execute(@NotNull Executor.StreamsContainer streamsContainer) throws ExecutionException;
}

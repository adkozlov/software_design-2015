package ru.spbau.kozlov.shell.pipelines;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
public class ParsedCommand {

    @NotNull
    private final String name;
    @NotNull
    private final List<String> arguments;

    public ParsedCommand(@NotNull List<String> arguments) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Argument list cannot be empty");
        }
        this.name = arguments.get(0);
        this.arguments = arguments.subList(1, arguments.size());
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
}

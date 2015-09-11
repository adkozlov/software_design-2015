package ru.spbau.kozlov.shell.pipelines;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    public ParsedCommand(@NotNull String name, @NotNull List<String> arguments) {
        this.name = name;
        this.arguments = new ArrayList<>(arguments);
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

package ru.spbau.kozlov.shell.pipelines;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author adkozlov
 */
public class PipelineParser {

    @NotNull
    public static final String BAR = "|";

    @NotNull
    private final StringTokenizer tokenizer;

    public PipelineParser(@NotNull String line) {
        tokenizer = new StringTokenizer(line);
    }

    @NotNull
    public Pipeline parse() {
        List<ParsedCommand> parsedCommands = new ArrayList<>();
        ParsedCommand parsedCommand;
        while ((parsedCommand = parseNextCommand()) != null) {
            parsedCommands.add(parsedCommand);
        }
        return new Pipeline(parsedCommands);
    }

    @Nullable
    private ParsedCommand parseNextCommand() {
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }

        String name = tokenizer.nextToken();
        List<String> arguments = new ArrayList<>();
        String argument;
        while (tokenizer.hasMoreTokens() && (argument = tokenizer.nextToken()) != null && !argument.equals(BAR)) {
            arguments.add(argument);
        }
        return new ParsedCommand(name, arguments);
    }
}

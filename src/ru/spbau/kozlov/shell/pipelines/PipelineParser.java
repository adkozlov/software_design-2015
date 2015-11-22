package ru.spbau.kozlov.shell.pipelines;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author adkozlov
 */
public class PipelineParser {

    @NotNull
    public static final Pattern SINGLE_COMMAND = Pattern.compile("\"?\\|(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
    @NotNull
    public static final Pattern SINGLE_TOKEN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    private PipelineParser() {
    }

    @NotNull
    public static List<ParsedCommand> parse(@NotNull String line) {
        return SINGLE_COMMAND.splitAsStream(line)
                .map(PipelineParser::parseNextCommand)
                .collect(Collectors.toList());
    }

    @Nullable
    private static ParsedCommand parseNextCommand(@NotNull String command) {
        List<String> arguments = new ArrayList<>();
        for (Matcher regexMatcher = SINGLE_TOKEN.matcher(command); regexMatcher.find(); ) {
            arguments.add(Stream.of(2, 1, 0)
                    .map(regexMatcher::group)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .get());
        }
        return new ParsedCommand(arguments);
    }
}

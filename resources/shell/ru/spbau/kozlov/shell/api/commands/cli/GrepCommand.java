package ru.spbau.kozlov.shell.api.commands.cli;

import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.commands.AbstractGrepCommand;
import ru.spbau.kozlov.shell.api.executions.ExecutionException;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
@CommandName("grep")
@ManPage({"Searches globally for lines matching the regular expression, and prints them"})
public class GrepCommand extends AbstractGrepCommand {

    @NotNull
    public static final String IGNORE_CASE_KEY = "i";
    @NotNull
    public static final String WORD_REGEX_KEY = "w";
    @NotNull
    public static final String AFTER_CONTEXT_KEY = "A";

    @NotNull
    private final Options options = new Options();
    @NotNull
    private final Option isCaseInsensitiveOption = new Option(IGNORE_CASE_KEY, "ignore-case", false, "Perform case insensitive matching");
    @NotNull
    private final Option isWordRegexpOption = new Option(WORD_REGEX_KEY, "word-regexp", false, "The expression is searched for as a word");
    @NotNull
    private final Option afterContextOption = OptionBuilder.hasArg()
            .withArgName("num")
            .withType(PatternOptionBuilder.NUMBER_VALUE)
            .withDescription("Print num lines of trailing context after each match")
            .create(AFTER_CONTEXT_KEY);
    @NotNull
    private final CommandLineParser parser = new DefaultParser();

    @Nullable
    private CommandLine commandLine;
    @NotNull
    private List<String> arguments = new ArrayList<>();

    public GrepCommand() {
        options.addOption(isCaseInsensitiveOption);
        options.addOption(isWordRegexpOption);
        options.addOption(afterContextOption);
    }

    @NotNull
    @Override
    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public void setArguments(@NotNull List<String> arguments) {
        this.arguments = new ArrayList<>(arguments);
    }

    @NotNull
    @Override
    protected List<String> parseArguments() throws ExecutionException {
        try {
            commandLine = parser.parse(options, getArgumentsAsArray(arguments));
            arguments = commandLine.getArgList();
            return arguments;
        } catch (ParseException e) {
            throw createParseException(e);
        }
    }

    @Override
    protected boolean isCaseInsensitive() {
        return hasOption(IGNORE_CASE_KEY);
    }

    @Override
    protected boolean isWordRegexp() {
        return hasOption(WORD_REGEX_KEY);
    }

    @Override
    protected long getAfterContextNum() throws ExecutionException {
        if (hasOption(AFTER_CONTEXT_KEY)) {
            try {
                assert commandLine != null;
                return (long) commandLine.getParsedOptionValue(AFTER_CONTEXT_KEY);
            } catch (ParseException e) {
                throw createParseException(e);
            }
        }
        return 0;
    }

    private boolean hasOption(@NotNull String optionName) {
        return commandLine != null && commandLine.hasOption(optionName);
    }

    @Override
    protected void printUsage(@NotNull PrintStream outputStream) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(new PrintWriter(outputStream), 80, getCommandName(), options);
    }
}

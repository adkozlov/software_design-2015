package ru.spbau.kozlov.shell.api.commands.jcommander;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.commands.AbstractGrepCommand;
import ru.spbau.kozlov.shell.api.executions.ExecutionException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
@CommandName("grep")
@ManPage({"Searches globally for lines matching the regular expression, and prints them"})
public class GrepCommand extends AbstractGrepCommand {

    @Parameter
    @NotNull
    private List<String> arguments = new ArrayList<>();

    @Parameter(names = {"-i", "--ignore-case"}, description = "Perform case insensitive matching")
    private boolean isCaseInsensitive = false;

    @Parameter(names = {"-w", "--word-regexp"}, description = "The expression is searched for as a word")
    private boolean isWordRegexp = false;

    @Parameter(names = {"-A", "--after-context"}, description = "Print num lines of trailing context after each match")
    private long afterContextNum = 0;

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
            new JCommander(this, getArgumentsAsArray(arguments));
            return arguments;
        } catch (ParameterException e) {
            throw createParseException(e);
        }
    }

    @Override
    protected boolean isCaseInsensitive() {
        return isCaseInsensitive;
    }

    @Override
    protected boolean isWordRegexp() {
        return isWordRegexp;
    }

    @Override
    protected long getAfterContextNum() {
        return afterContextNum;
    }

    @Override
    protected void printUsage(@NotNull PrintStream outputStream) {
        printUsage("[-iw] [-A num] [file]", outputStream);
    }
}

package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.executions.ExecutionException;
import ru.spbau.kozlov.shell.api.executions.Executor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author adkozlov
 */
public abstract class AbstractGrepCommand implements Command {

    protected static void print(@NotNull BufferedReader bufferedReader, @NotNull Predicate<String> stringPredicate, @NotNull PrintStream outputStream) {
        bufferedReader.lines().filter(stringPredicate).forEach(outputStream::println);
    }

    @NotNull
    protected static String[] getArgumentsAsArray(@NotNull List<String> arguments) {
        return arguments.toArray(new String[arguments.size()]);
    }

    @NotNull
    protected ExecutionException createParseException(@NotNull Exception parseException) {
        return new ExecutionException(getCommandName(), "Invalid argument", parseException);
    }

    @NotNull
    protected abstract List<String> parseArguments() throws ExecutionException;

    protected abstract boolean isCaseInsensitive();

    protected abstract boolean isWordRegexp();

    protected abstract long getAfterContextNum() throws ExecutionException;

    protected abstract void printUsage(@NotNull PrintStream outputStream);

    public void execute(@NotNull Executor.StreamsContainer streamsContainer) throws ExecutionException {
        List<String> arguments = parseArguments();
        PrintStream outputStream = streamsContainer.getOutputStream();

        try {
            Iterator<String> iterator = arguments.iterator();
            if (!iterator.hasNext()) {
                printUsage(outputStream);
                return;
            }

            Predicate<String> stringPredicate = getPredicate(getRegexp(iterator.next()));
            if (!iterator.hasNext()) {
                InputStream inputStream = streamsContainer.getInputStream();
                if (inputStream.available() != 0) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        print(bufferedReader, stringPredicate, outputStream);
                    }
                }
            } else {
                while (iterator.hasNext()) {
                    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(iterator.next()))) {
                        print(bufferedReader, stringPredicate, outputStream);
                    }
                }
            }
        } catch (IOException | UncheckedIOException e) {
            printError(e, streamsContainer.getErrorStream());
        }
    }

    @NotNull
    protected String getRegexp(@NotNull String argument) {
        argument = argument.startsWith("\"") && argument.endsWith("\"") ? argument.substring(1, argument.length() - 1) : argument;
        return isWordRegexp() ? "\\b" + argument + "\\b" : argument;
    }

    @NotNull
    protected Predicate<String> getPredicate(@NotNull String regexp) throws ExecutionException {
        Pattern pattern = Pattern.compile(regexp, isCaseInsensitive() ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0);
        Predicate<String> stringPredicate = pattern.asPredicate();

        long afterContextNum = getAfterContextNum();
        return afterContextNum != 0 ? new AfterContextPredicate(stringPredicate, afterContextNum) : stringPredicate;
    }

    private static class AfterContextPredicate implements Predicate<String> {

        @NotNull
        private final Predicate<String> predicate;
        private final long number;

        private long counter = -1;

        private AfterContextPredicate(@NotNull Predicate<String> predicate, long number) {
            this.predicate = predicate;
            this.number = number;
        }

        @Override
        public boolean test(@NotNull String s) {
            if (predicate.test(s)) {
                counter = 0;
                return true;
            }

            if (counter >= 0) {
                if (counter < number) {
                    counter++;
                    return true;
                } else if (number < 0) {
                    return true;
                }
            }

            return false;
        }
    }
}

package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.kozlov.shell.api.invoker.ExecutionException;
import ru.spbau.kozlov.shell.api.invoker.PrintUsageException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author adkozlov
 */
public abstract class AbstractGrepCommand extends AbstractCommand {

    @Nullable
    private Predicate<String> stringPredicate;

    @NotNull
    protected abstract List<String> parseArguments(@NotNull String[] arguments) throws ExecutionException;

    protected abstract boolean isCaseInsensitive();

    protected abstract boolean isWordRegexp();

    protected abstract long getAfterContextNum() throws ExecutionException;

    @Override
    public void execute(@NotNull InputStream inputStream,
                        @NotNull PrintStream outputStream,
                        @NotNull List<String> arguments) throws ExecutionException {
        List<String> parsedArguments = parseArguments(arguments);
        if (parsedArguments.isEmpty()) {
            throw new PrintUsageException(getCommandName());
        }

        stringPredicate = getPredicate(getRegexp(parsedArguments.get(0)));

        super.execute(inputStream, outputStream, parsedArguments.subList(1, parsedArguments.size()));
    }

    @Override
    protected void handleInputStream(@NotNull InputStream inputStream, @NotNull PrintStream outputStream) throws IOException {
        assert stringPredicate != null;
        forEachLine(inputStream, stringPredicate, outputStream::println);
    }

    @Override
    protected void handleArgument(@NotNull String fileName, @NotNull PrintStream outputStream) throws IOException {
        assert stringPredicate != null;
        forEachLine(fileName, stringPredicate, outputStream::println);
    }

    @NotNull
    protected ExecutionException createParseException(@NotNull Exception parseException) {
        return new ExecutionException(getCommandName(), "Invalid argument", parseException);
    }

    @NotNull
    private List<String> parseArguments(@NotNull List<String> arguments) throws ExecutionException {
        return parseArguments(arguments.toArray(new String[arguments.size()]));
    }

    @NotNull
    private String getRegexp(@NotNull String argument) {
        return isWordRegexp() ? "\\b" + argument + "\\b" : argument;
    }

    @NotNull
    private Predicate<String> getPredicate(@NotNull String regexp) throws ExecutionException {
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

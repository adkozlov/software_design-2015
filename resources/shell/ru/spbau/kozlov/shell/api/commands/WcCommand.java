package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.invoker.ExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author adkozlov
 */
@CommandName("wc")
@ManPage({"Prints the number of lines in specified files"})
public class WcCommand extends AbstractCommand {

    private final long[] total = {0};

    @Override
    public void execute(@NotNull InputStream inputStream,
                        @NotNull PrintStream outputStream,
                        @NotNull List<String> arguments) throws ExecutionException {
        super.execute(inputStream, outputStream, arguments);

        if (arguments.size() > 1) {
            outputStream.println(total[0] + " total");
        }
    }

    @Override
    protected void handleInputStream(@NotNull InputStream inputStream,
                                     @NotNull PrintStream outputStream) throws IOException {
        WcConsumer wcConsumer = new WcConsumer();
        forEachLine(inputStream, wcConsumer);
        outputStream.println(wcConsumer.result);
    }

    @Override
    protected void handleArgument(@NotNull String fileName, @NotNull PrintStream outputStream) throws IOException {
        WcConsumer wcConsumer = new WcConsumer();
        forEachLine(fileName, wcConsumer);
        outputStream.println(wcConsumer.result + " " + fileName);

        total[0] += wcConsumer.result;
    }

    @NotNull
    @Override
    public String getUsageMessage() {
        return "[file]";
    }

    private static class WcConsumer implements Consumer<String> {
        private long result = 0;

        @Override
        public void accept(String line) {
            result++;
        }
    }
}

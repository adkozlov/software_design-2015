package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.executions.Executor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author adkozlov
 */
@CommandName("cat")
@ManPage({"Prints the content of specified files"})
public class CatCommand extends AbstractCommand {

    @Override
    public void execute(@NotNull Executor.StreamsContainer streamsContainer) {
        List<String> arguments = getArguments();
        PrintStream outputStream = streamsContainer.getOutputStream();

        try {
            if (arguments.isEmpty()) {
                InputStream inputStream = streamsContainer.getInputStream();
                if (inputStream.available() != 0) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        print(bufferedReader, outputStream);
                    }
                } else {
                    printUsage(outputStream);
                }
            } else {
                for (String fileName : arguments) {
                    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(fileName))) {
                        print(bufferedReader, outputStream);
                    }
                }
            }
        } catch (IOException | UncheckedIOException e) {
            printError(e, streamsContainer.getErrorStream());
        }
    }

    @NotNull
    @Override
    protected String getUsageMessage() {
        return "[file]";
    }

    private static void print(@NotNull BufferedReader bufferedReader, @NotNull PrintStream outputStream) {
        bufferedReader.lines().forEach(outputStream::println);
    }
}

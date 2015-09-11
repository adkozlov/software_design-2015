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
@CommandName("wc")
@ManPage({"Prints the number of lines in specified files"})
public class WcCommand extends AbstractCommand {

    @Override
    public void execute(@NotNull Executor.StreamsContainer streamsContainer) {
        List<String> arguments = getArguments();
        PrintStream outputStream = streamsContainer.getOutputStream();

        try {
            if (arguments.isEmpty()) {
                InputStream inputStream = streamsContainer.getInputStream();
                if (inputStream.available() != 0) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        printLinesCount(getLinesCount(bufferedReader), "", outputStream);
                    }
                } else {
                    printUsage(outputStream);
                }
            } else {
                long totalLinesCount = 0;
                for (String fileName : arguments) {
                    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(fileName))) {
                        long linesCount = getLinesCount(bufferedReader);
                        printLinesCount(linesCount, fileName, outputStream);
                        totalLinesCount += linesCount;
                    }
                }

                if (arguments.size() != 1) {
                    printLinesCount(totalLinesCount, "total", outputStream);
                }
            }
        } catch (IOException | UncheckedIOException e) {
            printError(e, streamsContainer.getErrorStream());
        }
    }

    @NotNull
    @Override
    public String getUsageMessage() {
        return "[file]";
    }

    private static long getLinesCount(@NotNull BufferedReader bufferedReader) {
        long[] result = {0};
        bufferedReader.lines().forEach(line -> result[0]++);
        return result[0];
    }

    private static void printLinesCount(long linesCount, @NotNull String target, @NotNull PrintStream outputStream) {
        outputStream.printf("%d %s", linesCount, target);
        outputStream.println();
    }
}

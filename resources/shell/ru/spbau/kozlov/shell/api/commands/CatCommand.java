package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author adkozlov
 */
@CommandName("cat")
@ManPage({"Prints the content of specified files"})
public class CatCommand extends AbstractCommand {

    @NotNull
    @Override
    public String getUsageMessage() {
        return "[file]";
    }

    @Override
    protected void handleInputStream(@NotNull InputStream inputStream,
                                     @NotNull PrintStream outputStream) throws IOException {
        forEachLine(inputStream, outputStream::println);
    }

    @Override
    protected void handleArgument(@NotNull String fileName, @NotNull PrintStream outputStream) throws IOException {
        forEachLine(fileName, outputStream::println);
    }
}

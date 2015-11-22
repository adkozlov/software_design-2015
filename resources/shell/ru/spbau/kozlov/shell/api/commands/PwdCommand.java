package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.invoker.Invoker;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author adkozlov
 */
@CommandName("pwd")
@ManPage({"Prints current directory"})
public class PwdCommand implements Command {

    @Override
    public void execute(@NotNull InputStream inputStream,
                        @NotNull PrintStream outputStream,
                        @NotNull List<String> arguments) {
        outputStream.println(Paths.get("").toAbsolutePath().toString());
    }
}

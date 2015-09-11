package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.executions.Executor;

import java.nio.file.Paths;

/**
 * @author adkozlov
 */
@CommandName("pwd")
@ManPage({"Prints current directory"})
public class PwdCommand extends AbstractCommand {

    @Override
    public void execute(@NotNull Executor.StreamsContainer streamsContainer) {
        streamsContainer.getOutputStream().println(Paths.get("").toAbsolutePath().toString());
    }
}

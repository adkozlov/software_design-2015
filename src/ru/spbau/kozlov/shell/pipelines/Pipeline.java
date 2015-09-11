package ru.spbau.kozlov.shell.pipelines;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.Shell;
import ru.spbau.kozlov.shell.api.commands.Command;
import ru.spbau.kozlov.shell.api.executions.ExecutionException;
import ru.spbau.kozlov.shell.api.executions.Executor;
import ru.spbau.kozlov.shell.registry.CommandsRegistry;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author adkozlov
 */
public class Pipeline {

    @NotNull
    private final List<ParsedCommand> parsedCommands;

    public Pipeline(@NotNull List<ParsedCommand> parsedCommands) {
        this.parsedCommands = new ArrayList<>(parsedCommands);
    }

    private static void printByteArray(@NotNull byte[] bytes, @NotNull PrintStream printStream) {
        printStream.write(bytes, 0, bytes.length);
    }

    @NotNull
    private static Command createCommand(@NotNull Class<? extends Command> commandClass, @NotNull List<String> arguments) {
        try {
            Command command = commandClass.newInstance();
            command.setArguments(arguments);
            return command;
        } catch (InstantiationException | IllegalAccessException e) {
            // cannot be
            throw new RuntimeException(e);
        }
    }

    public void execute() throws ExecutionException {
        Executor executor = Executor.createExecutor(System.in);
        for (Iterator<ParsedCommand> iterator = parsedCommands.iterator(); !Shell.getInstance().isTerminated() && iterator.hasNext(); ) {
            ParsedCommand parsedCommand = iterator.next();
            String commandName = parsedCommand.getName();
            Class<? extends Command> commandClass = CommandsRegistry.getInstance().get(commandName);

            if (commandClass != null) {
                executor.execute(createCommand(commandClass, parsedCommand.getArguments()));
            } else {
                System.out.println(commandName + ": not found");
            }
            if (iterator.hasNext()) {
                executor = Executor.redirectStreams(executor);
            }
        }

        printByteArray(executor.getOutputStreamAsByteArray(), System.out);
        printByteArray(executor.getErrorStreamAsByteArray(), System.err);
    }
}

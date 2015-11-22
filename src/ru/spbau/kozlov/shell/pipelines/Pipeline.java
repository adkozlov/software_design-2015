package ru.spbau.kozlov.shell.pipelines;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.Shell;
import ru.spbau.kozlov.shell.api.commands.Command;
import ru.spbau.kozlov.shell.api.invoker.Invoker;
import ru.spbau.kozlov.shell.registry.CommandsRegistry;

import java.util.Iterator;
import java.util.List;

/**
 * @author adkozlov
 */
public class Pipeline {

    private Pipeline() {
    }

    public static void execute(@NotNull List<ParsedCommand> parsedCommands) {
        Invoker invoker = Invoker.createInvoker(System.in);
        for (Iterator<ParsedCommand> iterator = parsedCommands.iterator();
             !Shell.getInstance().isTerminated() && iterator.hasNext(); ) {
            invokeCommand(invoker, iterator.next());

            if (iterator.hasNext()) {
                invoker = Invoker.redirectStreams(invoker);
            }
        }
        invoker.writeTo(System.out);
    }

    private static void invokeCommand(@NotNull Invoker invoker, @NotNull ParsedCommand parsedCommand) {
        Class<? extends Command> commandClass = CommandsRegistry.getInstance().get(parsedCommand.getName());
        if (commandClass != null) {
            invoker.invoke(createCommand(commandClass), parsedCommand.getArguments());
        } else {
            System.out.println("shell: " + parsedCommand.getName() + ": not found");
        }
    }

    @NotNull
    private static Command createCommand(@NotNull Class<? extends Command> commandClass) {
        try {
            return commandClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

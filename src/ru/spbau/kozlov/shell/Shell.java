package ru.spbau.kozlov.shell;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.commands.AbstractCommand;
import ru.spbau.kozlov.shell.api.commands.Command;
import ru.spbau.kozlov.shell.api.commands.NotZeroExitCodeException;
import ru.spbau.kozlov.shell.api.executions.ExecutionException;
import ru.spbau.kozlov.shell.api.executions.Executor;
import ru.spbau.kozlov.shell.pipelines.PipelineParser;
import ru.spbau.kozlov.shell.registry.CommandsRegistry;
import ru.spbau.kozlov.shell.registry.ManPagesRegistry;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author adkozlov
 */
public class Shell implements Closeable {

    @NotNull
    public static final String PS1 = "# ";
    @NotNull
    private static final Shell INSTANCE = new Shell();

    static {
        Stream.of(ExitCommand.class, ManCommand.class).forEach(commandClass ->
                CommandLoader.getInstance().loadCommand(commandClass.getName(), ClassLoader.getSystemClassLoader()));
    }

    @NotNull
    private final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private boolean isTerminated = false;

    private static void registerCommand(@NotNull Class<? extends Command> commandClass) {
        CommandsRegistry.getInstance().register(commandClass);
        ManPagesRegistry.getInstance().register(commandClass);
    }

    public static void main(@NotNull String[] args) {
        CommandLoader.getInstance().loadCommands(Arrays.asList(args), ".").stream().forEach(Shell::registerCommand);

        int exitCode = Command.OK_EXIT_CODE;
        try (Shell shell = getInstance()) {
            shell.run();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (NotZeroExitCodeException e) {
            exitCode = e.getExitCode();
        }
        System.exit(exitCode);
    }

    @NotNull
    public static Shell getInstance() {
        return INSTANCE;
    }

    public void run() throws IOException, NotZeroExitCodeException {
        while (!isTerminated()) {
            try {
                System.out.print(PS1);
                PipelineParser pipelineParser = new PipelineParser(bufferedReader.readLine());
                pipelineParser.parse().execute();
            } catch (NotZeroExitCodeException e) {
                throw e;
            } catch (ExecutionException e) {
                System.err.println(e.getExecutableName() + ": " + e.getMessage());
            }
        }
    }

    public void terminate() {
        isTerminated = true;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

    @CommandName("exit")
    @ManPage({"Closes shell"})
    public static class ExitCommand extends AbstractCommand {

        static {
            registerCommand(ExitCommand.class);
        }

        @Override
        public void execute(@NotNull Executor.StreamsContainer streamsContainer) throws NotZeroExitCodeException {
            Shell.getInstance().terminate();

            List<String> arguments = getArguments();
            if (arguments.isEmpty()) {
                return;
            }

            try {
                int exitCode = Integer.valueOf(arguments.get(0));
                if (exitCode != OK_EXIT_CODE) {
                    throw new NotZeroExitCodeException(exitCode, getCommandName());
                }
            } catch (NumberFormatException e) {
                streamsContainer.getOutputStream().println(getCommandName() + ": numeric value required");
            }
        }
    }

    @CommandName("man")
    @ManPage({"Prints manual page for specified commands"})
    public static class ManCommand extends AbstractCommand {

        static {
            registerCommand(ManCommand.class);
        }

        @Override
        public void execute(@NotNull Executor.StreamsContainer streamsContainer) {
            PrintStream outputStream = streamsContainer.getOutputStream();
            List<String> arguments = getArguments();
            if (arguments.isEmpty()) {
                outputStream.println("What manual page do you want?");
                return;
            }

            for (String commandName : arguments) {
                String[] lines = ManPagesRegistry.getInstance().get(commandName);
                if (lines != null) {
                    for (String line : lines) {
                        outputStream.println(line);
                    }
                } else {
                    outputStream.println("No manual entry for " + commandName);
                }
            }
        }
    }
}

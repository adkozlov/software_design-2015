package ru.spbau.kozlov.shell;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.commands.AbstractCommand;
import ru.spbau.kozlov.shell.api.commands.Command;
import ru.spbau.kozlov.shell.api.invoker.ExecutionException;
import ru.spbau.kozlov.shell.api.invoker.PrintUsageException;
import ru.spbau.kozlov.shell.pipelines.Pipeline;
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
        Stream.of(ExitCommand.class, ManCommand.class).forEach(commandClass -> CommandLoader.loadCommand(
                ClassLoader.getSystemClassLoader(), commandClass.getName()));
    }

    @NotNull
    private final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private int exitCode = -1;

    public static void main(@NotNull String[] args) {
        CommandLoader.loadCommands(".", args).forEach(Shell::registerCommand);

        try (Shell shell = getInstance()) {
            shell.run();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
        System.exit(Shell.getInstance().exitCode);
    }

    @NotNull
    public static Shell getInstance() {
        return INSTANCE;
    }

    public void run() throws IOException {
        while (!isTerminated()) {
            System.out.print(PS1);
            Pipeline.execute(PipelineParser.parse(bufferedReader.readLine()));
        }
    }

    public boolean isTerminated() {
        return exitCode >= 0;
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

    private static void registerCommand(@NotNull Class<? extends Command> commandClass) {
        CommandsRegistry.getInstance().register(commandClass);
        ManPagesRegistry.getInstance().register(commandClass);
    }

    @CommandName("exit")
    @ManPage({"Closes shell"})
    public static class ExitCommand implements Command {

        static {
            registerCommand(ExitCommand.class);
        }

        @Override
        public void execute(@NotNull InputStream inputStream,
                            @NotNull PrintStream outputStream,
                            @NotNull List<String> arguments) throws ExecutionException {
            int exitCode = 0;
            try {
                if (!arguments.isEmpty()) {
                    exitCode = Integer.parseInt(arguments.get(0));
                }
            } catch (NumberFormatException e) {
                throw new ExecutionException(getCommandName(), "numeric value required", e);
            } finally {
                Shell.getInstance().exitCode = exitCode;
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
        public void execute(@NotNull InputStream inputStream,
                            @NotNull PrintStream outputStream,
                            @NotNull List<String> arguments) throws ExecutionException {
            if (arguments.isEmpty()) {
                throw new PrintUsageException(getCommandName());
            }

            super.execute(inputStream, outputStream, arguments);
        }

        @Override
        protected void handleInputStream(@NotNull InputStream inputStream,
                                         @NotNull PrintStream outputStream) throws IOException {
        }

        @Override
        protected void handleArgument(@NotNull String argument, @NotNull PrintStream outputStream) throws IOException {
            String[] lines = ManPagesRegistry.getInstance().get(argument);
            if (lines != null) {
                Arrays.stream(lines).forEach(outputStream::println);
            } else {
                outputStream.println("No manual entry for " + argument);
            }
        }

        @NotNull
        @Override
        public String getUsageMessage() {
            return "What manual page do you want?";
        }

        @Override
        public void printUsage(@NotNull PrintStream outputStream) {
            outputStream.println(getUsageMessage());
        }
    }
}

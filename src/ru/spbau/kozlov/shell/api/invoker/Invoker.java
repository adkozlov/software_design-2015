package ru.spbau.kozlov.shell.api.invoker;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.commands.Command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * @author adkozlov
 */
public class Invoker {

    @NotNull
    private final InputStream inputStream;
    @NotNull
    private final ByteArrayOutputStream outputStream;

    private Invoker(@NotNull InputStream inputStream) {
        this.inputStream = inputStream;
        this.outputStream = new ByteArrayOutputStream();
    }

    @NotNull
    public static Invoker createInvoker(@NotNull InputStream inputStream) {
        return new Invoker(inputStream);
    }

    @NotNull
    public static Invoker redirectStreams(@NotNull Invoker invoker) {
        return new Invoker(new ByteArrayInputStream(invoker.outputStream.toByteArray()));
    }

    public void invoke(@NotNull Command command, @NotNull List<String> arguments) {
        PrintStream printStream = new PrintStream(outputStream);
        try {
            command.execute(inputStream, printStream, arguments);
        } catch (PrintUsageException e) {
            command.printUsage(printStream);
        } catch (ExecutionException e) {
            command.printError(e, printStream);
        }
    }

    public void writeTo(@NotNull PrintStream printStream) {
        byte[] bytes = outputStream.toByteArray();
        printStream.write(bytes, 0, bytes.length);
    }
}

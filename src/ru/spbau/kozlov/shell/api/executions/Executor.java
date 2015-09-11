package ru.spbau.kozlov.shell.api.executions;

import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * @author adkozlov
 */
public class Executor {

    @NotNull
    private final InputStream inputStream;
    @NotNull
    private final OutputStream outputStream;
    @NotNull
    private final OutputStream errorStream;

    private Executor(@NotNull InputStream inputStream, @NotNull OutputStream outputStream, @NotNull OutputStream errorStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
    }

    @NotNull
    private static byte[] getBytes(@NotNull OutputStream outputStream) {
        return outputStream instanceof ByteArrayOutputStream ? ((ByteArrayOutputStream) outputStream).toByteArray() : new byte[0];
    }

    @NotNull
    public static Executor createExecutor(@NotNull InputStream inputStream) {
        return new Executor(inputStream, new ByteArrayOutputStream(), new ByteArrayOutputStream());
    }

    @NotNull
    public static Executor redirectStreams(@NotNull Executor executor) {
        return new Executor(new ByteArrayInputStream(getBytes(executor.outputStream)), new ByteArrayOutputStream(), executor.errorStream);
    }

    public void execute(@NotNull Executable executable) throws ExecutionException {
        executable.execute(new StreamsContainer());
    }

    @NotNull
    public byte[] getOutputStreamAsByteArray() {
        return getBytes(outputStream);
    }

    @NotNull
    public byte[] getErrorStreamAsByteArray() {
        return getBytes(errorStream);
    }

    public class StreamsContainer {

        @NotNull
        private final PrintStream outputStream;
        @NotNull
        private final PrintStream errorStream;

        public StreamsContainer() {
            this.outputStream = new PrintStream(Executor.this.outputStream);
            this.errorStream = new PrintStream(Executor.this.errorStream);
        }

        @NotNull
        public InputStream getInputStream() {
            return Executor.this.inputStream;
        }

        @NotNull
        public PrintStream getOutputStream() {
            return outputStream;
        }

        @NotNull
        public PrintStream getErrorStream() {
            return errorStream;
        }
    }
}

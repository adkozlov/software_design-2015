package ru.spbau.kozlov.shell.api.commands;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
public abstract class AbstractCommand implements Command {

    @NotNull
    private List<String> arguments = new ArrayList<>();

    @NotNull
    @Override
    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public void setArguments(@NotNull List<String> arguments) {
        this.arguments = new ArrayList<>(arguments);
    }

    @NotNull
    protected String getUsageMessage() {
        return "";
    }

    protected void printUsage(@NotNull PrintStream outputStream) {
        printUsage(getUsageMessage(), outputStream);
    }
}

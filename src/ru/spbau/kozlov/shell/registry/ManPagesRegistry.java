package ru.spbau.kozlov.shell.registry;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.annotations.ManPage;
import ru.spbau.kozlov.shell.api.commands.Command;

/**
 * @author adkozlov
 */
public class ManPagesRegistry extends AbstractRegistry<String[]> {

    @NotNull
    private final static ManPagesRegistry INSTANCE = new ManPagesRegistry();

    private ManPagesRegistry() {
    }

    @NotNull
    public static ManPagesRegistry getInstance() {
        return INSTANCE;
    }

    public void register(@NotNull Class<? extends Command> commandClass) {
        if (commandClass.isAnnotationPresent(CommandName.class) && commandClass.isAnnotationPresent(ManPage.class)) {
            register(commandClass.getAnnotation(CommandName.class).value(), commandClass.getAnnotation(ManPage.class).value());
        }
    }
}

package ru.spbau.kozlov.shell.registry;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.shell.api.annotations.CommandName;
import ru.spbau.kozlov.shell.api.commands.Command;

import java.lang.reflect.Modifier;

/**
 * @author adkozlov
 */
public class CommandsRegistry extends AbstractRegistry<Class<? extends Command>> {

    @NotNull
    private static final CommandsRegistry INSTANCE = new CommandsRegistry();

    private CommandsRegistry() {
    }

    @NotNull
    public static CommandsRegistry getInstance() {
        return INSTANCE;
    }

    public void register(@NotNull Class<? extends Command> commandClass) {
        if (commandClass.isAnnotationPresent(CommandName.class)) {
            try {
                if (Modifier.isPublic(commandClass.getDeclaredConstructor().getModifiers())) {
                    register(commandClass.getAnnotation(CommandName.class).value(), commandClass);
                } else {
                    throw new RuntimeException(commandClass + " class has no public default constructor");
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(commandClass + " class has no default constructor", e);
            }
        }
    }
}

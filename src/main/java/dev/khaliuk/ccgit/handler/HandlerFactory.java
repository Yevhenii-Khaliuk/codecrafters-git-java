package dev.khaliuk.ccgit.handler;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

public class HandlerFactory {
    private static final Handler DEFAULT_HANDLER =
        arguments -> System.out.printf("Unknown command: %s%n", arguments[0]);

    private final Map<Command, Handler> commandHandlers = new EnumMap<>(Command.class);

    public HandlerFactory() throws NoSuchMethodException, InvocationTargetException,
        InstantiationException, IllegalAccessException {
        initHandlers();
    }

    public Handler getHandler(String commandName) {

        if (!Command.contains(commandName)) {
            return DEFAULT_HANDLER;
        }

        var command = Command.valueOf(commandName);
        return commandHandlers.get(command);
    }

    private void initHandlers() throws NoSuchMethodException, InstantiationException,
        IllegalAccessException, InvocationTargetException {
        for (Command command : Command.values()) {
            Constructor<? extends Handler> handlerConstructor = command.handler.getConstructor();
            Handler handler = handlerConstructor.newInstance();
            commandHandlers.put(command, handler);
        }
    }

    enum Command {
        CAT_FILE(CatFile.class),
        HASH_OBJECT(HashObject.class),
        INIT(Init.class),
        LS_TREE(LsTree.class);

        private final Class<? extends Handler> handler;

        Command(Class<? extends Handler> handler) {
            this.handler = handler;
        }

        static boolean contains(String commandName) {
            for (Command command : values()) {
                if (command.name().equals(commandName)) {
                    return true;
                }
            }
            return false;
        }
    }
}

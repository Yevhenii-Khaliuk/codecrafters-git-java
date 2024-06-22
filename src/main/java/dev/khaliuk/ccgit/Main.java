package dev.khaliuk.ccgit;

import dev.khaliuk.ccgit.handler.HandlerFactory;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
        InstantiationException, IllegalAccessException {
        var handlerFactory = new HandlerFactory();
        var commandName = args[0].replace('-', '_').toUpperCase();
        handlerFactory.getHandler(commandName)
            .handle(args);
    }
}

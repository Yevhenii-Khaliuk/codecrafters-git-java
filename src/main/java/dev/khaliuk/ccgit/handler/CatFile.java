package dev.khaliuk.ccgit.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.InflaterInputStream;

public class CatFile implements Handler {
    @Override
    public void handle(String[] arguments) {
        var dir = arguments[2].substring(0, 2);
        var file = arguments[2].substring(2);
        var path = Paths.get(".git/objects", dir, file);

        try {
            var raw = Files.readAllBytes(path); // compressed data
            var inputStream = new InflaterInputStream(new ByteArrayInputStream(raw));
            // leveraging InputStream.readAllBytes() method we can skip all the bytes up until the '\0' byte
            var current = inputStream.read();
            while (current != 0) {
                current = inputStream.read();
            }
            System.out.print(new String(inputStream.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

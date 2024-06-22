package dev.khaliuk.ccgit.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.InflaterInputStream;

public class LsTree implements Handler {
    @Override
    public void handle(String[] arguments) {
        var dir = arguments[2].substring(0, 2);
        var file = arguments[2].substring(2);
        var path = Paths.get(".git/objects", dir, file);

        try {
            var raw = Files.readAllBytes(path); // compressed data
            var inputStream = new InflaterInputStream(new ByteArrayInputStream(raw));
            // The format of a tree object file looks like this
            // (after Zlib decompression; newlines are added for readability):
            //  tree <size>\0
            //  <mode> <name>\0<20_byte_sha>
            //  <mode> <name>\0<20_byte_sha>

            // 1. Skip header 'tree <size>\0'
            var current = inputStream.read();
            while (current != 0) {
                current = inputStream.read();
            }

            var names = new ArrayList<String>();

            while (inputStream.available() != 0) {
                // 2. Skip <mode> with following space character
                do {
                    current = inputStream.read();
                } while (current != ' ');

                var nameBuffer = new byte[256]; // there are filesystems that allow longer filenames
                var index = 0;
                current = inputStream.read();
                while (current != 0) {
                    nameBuffer[index] = (byte) current;
                    index++;
                    current = inputStream.read();
                }
                names.add(new String(nameBuffer, 0, index));

                // 3. Skip 20 bytes of hash
                for (int i = 0; i < 20; i++) {
                    inputStream.read();
                }
            }

            System.out.println(String.join("\n", names));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

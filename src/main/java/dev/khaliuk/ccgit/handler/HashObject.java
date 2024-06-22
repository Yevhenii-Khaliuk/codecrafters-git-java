package dev.khaliuk.ccgit.handler;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.DeflaterInputStream;

public class HashObject implements Handler {
    @Override
    public void handle(String[] arguments) {
        var fileName = arguments[2];
        var path = Paths.get(fileName);
        try {
            var uncompressedContent = Files.readAllBytes(path);

            var header = "blob %s\0".formatted(uncompressedContent.length).getBytes();
            var uncompressedBlob = new byte[header.length + uncompressedContent.length];
            System.arraycopy(header, 0, uncompressedBlob, 0, header.length);
            System.arraycopy(uncompressedContent, 0, uncompressedBlob, header.length, uncompressedContent.length);
            var inputStream = new DeflaterInputStream(new ByteArrayInputStream(uncompressedBlob));
            var compressedBlob = inputStream.readAllBytes();

            var hash = DigestUtils.sha1Hex(uncompressedBlob);
            var dir = hash.substring(0, 2);
            var blobFile = hash.substring(2);
            var blobPath = Paths.get(".git/objects", dir, blobFile);
            Files.createDirectories(Paths.get(".git/objects", dir));
            Files.write(blobPath, compressedBlob);
            System.out.println(hash);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

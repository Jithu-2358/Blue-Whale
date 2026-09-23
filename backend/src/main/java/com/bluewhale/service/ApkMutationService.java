// service/ApkMutationService.java
package com.bluewhale.service;

import com.bluewhale.storage.FileStorageService;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.UUID;

@Service
public class ApkMutationService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final FileStorageService storage;

    public ApkMutationService(FileStorageService storage) {
        this.storage = storage;
    }

    /**
     * Copies the base APK and appends random junk bytes (100-500KB).
     * Appending to the END of a ZIP/APK is safe because the ZIP central
     * directory is at the end — extra trailing bytes are ignored by the
     * Android package parser. Result: a NEW SHA256 hash every download.
     */
    public Path mutate() throws IOException {
        Path base = storage.apkPath("GameApp.apk");
        if (!Files.exists(base)) {
            throw new IOException("Base APK not found: " + base);
        }

        String name = "BlueWhale_" + UUID.randomUUID().toString().substring(0, 8) + ".apk";
        Path out = storage.apkPath(name);
        Files.copy(base, out, StandardCopyOption.REPLACE_EXISTING);

        int junkSize = 100 * 1024 + RANDOM.nextInt(400 * 1024);
        byte[] junk = new byte[junkSize];
        RANDOM.nextBytes(junk);

        try (RandomAccessFile raf = new RandomAccessFile(out.toFile(), "rw")) {
            raf.seek(raf.length());
            raf.write(junk);
        }

        // Only keep the latest 20 mutated copies to avoid disk bloat
        cleanupOldMutated();

        return out;
    }

    private void cleanupOldMutated() throws IOException {
        try (var stream = Files.list(Paths.get(storage.apkPath("" ).toString()))) {
            var list = stream
                    .filter(p -> p.getFileName().toString().startsWith("BlueWhale_"))
                    .sorted((a, b) -> b.getFileName().toString().compareTo(a.getFileName().toString()))
                    .toList();
            for (int i = 20; i < list.size(); i++) {
                Files.deleteIfExists(list.get(i));
            }
        }
    }
}
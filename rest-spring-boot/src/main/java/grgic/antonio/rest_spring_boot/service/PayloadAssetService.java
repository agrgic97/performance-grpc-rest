package grgic.antonio.rest_spring_boot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PayloadAssetService {

    @Value("${bench.payloads-dir}")
    private String payloadsDir;

    private byte[] small;
    private byte[] medium;
    private byte[] large;

    @PostConstruct
    public void load() throws Exception {
        Path dir = Path.of(payloadsDir).toAbsolutePath().normalize();

        small  = Files.readAllBytes(dir.resolve("small_100b.json"));
        medium = Files.readAllBytes(dir.resolve("medium_50kb.json"));
        large  = Files.readAllBytes(dir.resolve("large_2mb.png"));
    }

    public byte[] small()  { return small; }
    public byte[] medium() { return medium; }
    public byte[] large()  { return large; }
}

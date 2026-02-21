package grgic.antonio.rest_spring_boot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PayloadAssetService {

    @Value("${bench.payloads-dir}")
    private String payloadsDir;

    private final ObjectMapper objectMapper;

    private byte[] small;
    private byte[] medium;
    private byte[] large;

    private JsonNode smallJson;
    private JsonNode mediumJson;

    public PayloadAssetService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void load() throws Exception {
        Path dir = Path.of(payloadsDir).toAbsolutePath().normalize();

        small  = Files.readAllBytes(dir.resolve("small_100b.json"));
        medium = Files.readAllBytes(dir.resolve("medium_50kb.json"));
        large  = Files.readAllBytes(dir.resolve("large_2mb.png"));

        smallJson = objectMapper.readTree(small);
        mediumJson = objectMapper.readTree(medium);
    }

    public byte[] small()  { return small; }
    public byte[] medium() { return medium; }
    public byte[] large()  { return large; }

    public JsonNode smallJson() { return smallJson; }
    public JsonNode mediumJson() { return mediumJson; }
}

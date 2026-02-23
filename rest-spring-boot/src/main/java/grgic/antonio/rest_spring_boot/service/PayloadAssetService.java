package grgic.antonio.rest_spring_boot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import grgic.antonio.rest_spring_boot.model.MediumPayload;
import grgic.antonio.rest_spring_boot.model.SmallPayload;
import org.springframework.stereotype.Service;
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

    private SmallPayload smallJson;
    private MediumPayload mediumJson;

    public PayloadAssetService() {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void load() throws Exception {
        Path dir = Path.of(payloadsDir).toAbsolutePath().normalize();

        small  = Files.readAllBytes(dir.resolve("small_100b.json"));
        medium = Files.readAllBytes(dir.resolve("medium_50kb.json"));
        large  = Files.readAllBytes(dir.resolve("large_2mb.png"));

        smallJson = objectMapper.readValue(small, SmallPayload.class);
        mediumJson = objectMapper.readValue(medium, MediumPayload.class);
    }

    public byte[] small()  { return small; }
    public byte[] medium() { return medium; }
    public byte[] large()  { return large; }

    public SmallPayload smallJson() { return smallJson; }
    public MediumPayload mediumJson() { return mediumJson; }
}

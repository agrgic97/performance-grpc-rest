package grgic.antonio.grpc_spring_boot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import grgic.antonio.grpc_spring_boot.model.MediumObject;
import grgic.antonio.grpc_spring_boot.model.SmallObject;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private SmallObject smallObject;
    private MediumObject mediumObject;

    public PayloadAssetService() {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void load() throws Exception {
        Path dir = Path.of(payloadsDir).toAbsolutePath().normalize();

        small = Files.readAllBytes(dir.resolve("small_100b.json"));
        medium = Files.readAllBytes(dir.resolve("medium_50kb.json"));
        large = Files.readAllBytes(dir.resolve("large_2mb.png"));

        smallObject = objectMapper.readValue(small, SmallObject.class);
        mediumObject = objectMapper.readValue(medium, MediumObject.class);
    }

    public byte[] small() { return small; }
    public byte[] medium() { return medium; }
    public byte[] large() { return large; }

    public SmallObject smallObject() {
        return smallObject;
    }

    public MediumObject mediumObject() {
        return mediumObject;
    }
}

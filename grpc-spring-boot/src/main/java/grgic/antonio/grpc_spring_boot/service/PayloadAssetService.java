package grgic.antonio.grpc_spring_boot.service;

import com.google.protobuf.util.JsonFormat;
import grgic.antonio.grpc_spring_boot.proto.MediumPayload;
import grgic.antonio.grpc_spring_boot.proto.SmallPayload;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

@Service
public class PayloadAssetService {

    @Value("${bench.payloads-dir}")
    private String payloadsDir;

    private byte[] small;
    private byte[] medium;
    private byte[] large;
    private SmallPayload smallPayload;
    private MediumPayload mediumPayload;

    @PostConstruct
    public void load() throws Exception {
        Path dir = Path.of(payloadsDir).toAbsolutePath().normalize();

        small = Files.readAllBytes(dir.resolve("small_100b.json"));
        medium = Files.readAllBytes(dir.resolve("medium_50kb.json"));
        large = Files.readAllBytes(dir.resolve("large_2mb.png"));

        SmallPayload.Builder smallBuilder = SmallPayload.newBuilder();
        JsonFormat.parser()
                .ignoringUnknownFields()
                .merge(new String(small, StandardCharsets.UTF_8), smallBuilder);
        smallPayload = smallBuilder.build();

        MediumPayload.Builder mediumBuilder = MediumPayload.newBuilder();
        JsonFormat.parser()
                .ignoringUnknownFields()
                .merge(new String(medium, StandardCharsets.UTF_8), mediumBuilder);
        mediumPayload = mediumBuilder.build();
    }

    public byte[] small() { return small; }
    public byte[] medium() { return medium; }
    public byte[] large() { return large; }

    public SmallPayload smallPayload() {
        return smallPayload;
    }

    public MediumPayload mediumPayload() {
        return mediumPayload;
    }
}

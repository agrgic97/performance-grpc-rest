package grgic.antonio.grpc_spring_boot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import grgic.antonio.grpc_spring_boot.model.MediumObject;
import grgic.antonio.grpc_spring_boot.model.MediumObjectItem;
import grgic.antonio.grpc_spring_boot.model.SmallObject;
import grgic.antonio.grpc_spring_boot.proto.MediumPayload;
import grgic.antonio.grpc_spring_boot.proto.MediumPayloadItem;
import grgic.antonio.grpc_spring_boot.proto.PayloadResponse;
import grgic.antonio.grpc_spring_boot.proto.SmallPayload;
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
    private SmallPayload smallPayload;
    private MediumPayload mediumPayload;

    public PayloadAssetService() {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void load() throws Exception {
        Path dir = Path.of(payloadsDir).toAbsolutePath().normalize();

        small = Files.readAllBytes(dir.resolve("small_100b.json"));
        medium = Files.readAllBytes(dir.resolve("medium_50kb.json"));
        large = Files.readAllBytes(dir.resolve("large_2mb.png"));

        SmallObject smallObject  = objectMapper.readValue(small, SmallObject.class);
        MediumObject mediumObject  = objectMapper.readValue(medium, MediumObject.class);

        smallPayload = toSmallPayloadMessage(smallObject);
        mediumPayload = toMediumPayloadMessage(mediumObject);
    }

    public PayloadResponse small() { return toPayloadResponse(small); }
    public PayloadResponse medium() { return toPayloadResponse(medium); }
    public PayloadResponse large() { return toPayloadResponse(large); }

    public SmallPayload smallObject() {
        return this.smallPayload;
    }

    public MediumPayload mediumObject() {
        return this.mediumPayload;
    }

    private SmallPayload toSmallPayloadMessage(SmallObject payload) {
        return SmallPayload.newBuilder()
                .setId(payload.id())
                .setProtocol(payload.protocol())
                .setService(payload.service())
                .setPayloadType(payload.payloadType())
                .setStatus(payload.status())
                .setFixed(payload.fixed())
                .build();
    }

    private MediumPayload toMediumPayloadMessage(MediumObject payload) {
        MediumPayload.Builder builder = MediumPayload.newBuilder()
                .setPayloadType(payload.payloadType())
                .setDescription(payload.description())
                .setUnit(payload.unit())
                .setPad(payload.pad());

        for (MediumObjectItem item : payload.items()) {
            builder.addItems(MediumPayloadItem.newBuilder()
                    .setId(item.id())
                    .setValue(item.value())
                    .build());
        }

        return builder.build();
    }

    private PayloadResponse toPayloadResponse(byte[] payload) {
        return PayloadResponse.newBuilder()
                .setPayload(ByteString.copyFrom(payload))
                .build();
    }
}

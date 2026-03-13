package grgic.antonio.grpc_spring_boot.service;

import grgic.antonio.grpc_codegen.proto.LargePayload;
import grgic.antonio.grpc_codegen.proto.MediumPayload;
import grgic.antonio.grpc_codegen.proto.SmallPayload;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class PayloadAssetService {
    private SmallPayload smallPayload;
    private MediumPayload mediumPayload;
    private LargePayload largePayload;

    @PostConstruct
    public void load() {
        this.smallPayload = generateSmallObject();
        this.mediumPayload = generateMediumObject();
        this.largePayload = generateLargeObject();
    }

    public SmallPayload smallObject() {
        return this.smallPayload;
    }

    public MediumPayload mediumObject() {
        return this.mediumPayload;
    }

    public LargePayload largeObject() {
        return this.largePayload;
    }

    private SmallPayload generateSmallObject() {
        return SmallPayload.newBuilder()
                .setId(1)
                .setProtocol("http")
                .setService("benchmark-service")
                .setPayloadType("small")
                .setStatus("ok")
                .setFixed(true)
                .build();
    }

    private MediumPayload generateMediumObject() {
        MediumPayload.Builder builder = MediumPayload.newBuilder()
                .setPayloadType("medium")
                .setDescription("Generated medium JSON payload")
                .setUnit("POJO");

        for (int i = 0; i < 40_000; i++) {
            builder.addItems(i + 10_000_000);
        }

        return builder.build();
    }

    private LargePayload generateLargeObject() {
        LargePayload.Builder builder = LargePayload.newBuilder();

        for (int i = 0; i < 10; i++) {
            builder.addItems(generateMediumObject());
        }

        return builder.build();
    }
}

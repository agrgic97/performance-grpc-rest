package grgic.antonio.grpc_spring_boot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.ByteString;
import grgic.antonio.grpc_spring_boot.model.LargeObject;
import grgic.antonio.grpc_spring_boot.model.MediumObject;
import grgic.antonio.grpc_spring_boot.model.MediumObjectItem;
import grgic.antonio.grpc_spring_boot.model.SmallObject;
import grgic.antonio.grpc_spring_boot.proto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PayloadAssetService {

    private final ObjectMapper objectMapper;

    private byte[] small;
    private byte[] medium;
    private byte[] large;

    private SmallPayload smallPayload;
    private MediumPayload mediumPayload;
    private LargePayload largePayload;

    public PayloadAssetService() {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void load() throws IOException {
        this.small = generateSmallJsonBytes();
        this.medium = generateMediumJsonBytes();
        this.large = generateLargeJsonBytes();

        this.smallPayload = createSmallPayload(this.small);
        this.mediumPayload = createMediumPayload(this.medium);
        this.largePayload = createLargePayload(this.large);
    }

    public PayloadResponse small() {
        return toPayloadResponse(this.small);
    }

    public PayloadResponse medium() {
        return toPayloadResponse(this.medium);
    }

    public PayloadResponse large() {
        return toPayloadResponse(this.large);
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

    private SmallPayload createSmallPayload(byte[] bytes) throws IOException {
        return toSmallPayload(objectMapper.readValue(bytes, SmallObject.class));
    }

    private MediumPayload createMediumPayload(byte[] bytes) throws IOException {
        return toMediumPayload(objectMapper.readValue(bytes, MediumObject.class));
    }

    private LargePayload createLargePayload(byte[] bytes) throws IOException {
        return toLargePayload(objectMapper.readValue(bytes, LargeObject.class));
    }

    private byte[] generateSmallJsonBytes() throws JsonProcessingException {
        ObjectNode json = objectMapper.createObjectNode();

        json.put("id", 1);
        json.put("protocol", "http");
        json.put("service", "benchmark-service");
        json.put("payloadType", "small");
        json.put("status", "ok");
        json.put("fixed", true);

        return objectMapper.writeValueAsBytes(json);
    }

    private byte[] generateMediumJsonBytes() throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(generateMediumJsonNode());
    }

    private byte[] generateLargeJsonBytes() throws JsonProcessingException {
        ObjectNode json = objectMapper.createObjectNode();

        json.put("payloadType", "large");
        json.put("description", "Generated large JSON payload");
        json.put("unit", "bytes");

        ArrayNode items = json.putArray("items");

        for (int i = 0; i < 20; i++) {
            ObjectNode entry = generateMediumJsonNode();

            items.add(entry);
        }

        return objectMapper.writeValueAsBytes(json);
    }

    private ObjectNode generateMediumJsonNode() {
        ObjectNode json = objectMapper.createObjectNode();

        json.put("payloadType", "medium");
        json.put("description", "Generated medium JSON payload");
        json.put("unit", "bytes");

        ArrayNode items = json.putArray("items");

        for (int i = 0; i < 2000; i++) {
            ObjectNode entry = objectMapper.createObjectNode();
            entry.put("id", i);
            entry.put("value", i * 10_000_000);

            items.add(entry);
        }

        return json;
    }

    private PayloadResponse toPayloadResponse(byte[] payload) {
        return PayloadResponse.newBuilder()
                .setPayload(ByteString.copyFrom(payload))
                .build();
    }

    private SmallPayload toSmallPayload(SmallObject object) {
        return SmallPayload.newBuilder()
                .setId(object.id())
                .setProtocol(object.protocol())
                .setService(object.service())
                .setPayloadType(object.payloadType())
                .setStatus(object.status())
                .setFixed(object.fixed())
                .build();
    }

    private MediumPayload toMediumPayload(MediumObject object) {
        MediumPayload.Builder builder = MediumPayload.newBuilder()
                .setPayloadType(object.payloadType())
                .setDescription(object.description())
                .setUnit(object.unit());

        for (MediumObjectItem item : object.items()) {
            builder.addItems(MediumPayloadItem.newBuilder()
                    .setId(item.id())
                    .setValue(item.value())
                    .build());
        }

        return builder.build();
    }

    private LargePayload toLargePayload(LargeObject object) {
        LargePayload.Builder builder = LargePayload.newBuilder()
                .setPayloadType(object.payloadType())
                .setDescription(object.description())
                .setUnit(object.unit());

        for (MediumObject item : object.items()) {
            builder.addItems(toMediumPayload(item));
        }

        return builder.build();
    }
}

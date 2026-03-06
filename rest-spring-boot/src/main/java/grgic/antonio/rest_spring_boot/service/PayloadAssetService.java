package grgic.antonio.rest_spring_boot.service;

import grgic.antonio.rest_spring_boot.model.LargePayload;
import grgic.antonio.rest_spring_boot.model.MediumPayload;
import grgic.antonio.rest_spring_boot.model.SmallPayload;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Service
public class PayloadAssetService {

    private final ObjectMapper objectMapper;

    private byte[] small;
    private byte[] medium;
    private byte[] large;

    private SmallPayload smallJson;
    private MediumPayload mediumJson;
    private LargePayload largeJson;

    public PayloadAssetService() {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void load() {
        this.small = generateSmallJsonBytes();
        this.medium = generateMediumJsonBytes();
        this.large = generateLargeJsonBytes();

        this.smallJson = createSmallPayloadObject(this.small);
        this.mediumJson = createMediumPayloadObject(this.medium);
        this.largeJson = createLargePayloadObject(this.large);
    }

    public byte[] small() {
        return small;
    }

    public byte[] medium() {
        return medium;
    }

    public byte[] large() {
        return large;
    }

    public SmallPayload smallJson() {
        return smallJson;
    }

    public MediumPayload mediumJson() {
        return mediumJson;
    }

    public LargePayload largeJson() {
        return largeJson;
    }

    private SmallPayload createSmallPayloadObject(byte[] bytes) {
        return objectMapper.readValue(bytes, SmallPayload.class);
    }

    private MediumPayload createMediumPayloadObject(byte[] bytes) {
        return objectMapper.readValue(bytes, MediumPayload.class);
    }

    private LargePayload createLargePayloadObject(byte[] bytes) {
        return objectMapper.readValue(bytes, LargePayload.class);
    }

    private byte[] generateSmallJsonBytes() {
        ObjectNode json = objectMapper.createObjectNode();

        json.put("id", 1);
        json.put("protocol", "http");
        json.put("service", "benchmark-service");
        json.put("payloadType", "small");
        json.put("status", "ok");
        json.put("fixed", true);

        return objectMapper.writeValueAsBytes(json);
    }

    private byte[] generateMediumJsonBytes() {
        return objectMapper.writeValueAsBytes(generateMediumJsonNode());
    }

    private byte[] generateLargeJsonBytes() {
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
}

package grgic.antonio.rest_spring_boot.service;

import grgic.antonio.rest_spring_boot.model.MediumPayload;
import grgic.antonio.rest_spring_boot.model.MediumPayloadItem;
import grgic.antonio.rest_spring_boot.model.SmallPayload;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayloadAssetService {

    private static final int ONE_GIB = 1024 * 1024 * 1024;
    private static final int FIFTY_KIB = 50 * 1024;

    @Value("${bench.large-json-size-bytes:1073741824}")
    private int largeJsonSizeBytes;

    private byte[] small;
    private byte[] medium;
    private byte[] large;

    private SmallPayload smallJson;
    private MediumPayload mediumJson;

    @PostConstruct
    public void load() {
        this.smallJson = createSmallPayloadObject();
        this.mediumJson = createMediumPayloadObject(FIFTY_KIB);

        this.small = toJson(smallJson);
        this.medium = toJson(mediumJson);
        this.large = generateLargeJsonBytes(largeJsonSizeBytes > 0 ? largeJsonSizeBytes : ONE_GIB);
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

    private SmallPayload createSmallPayloadObject() {
        return new SmallPayload(1, "REST", "payload-service", "small", "ok", true);
    }

    private MediumPayload createMediumPayloadObject(int targetBytes) {
        List<MediumPayloadItem> items = new ArrayList<>(512);
        for (int i = 0; i < 512; i++) {
            items.add(new MediumPayloadItem(i + 1, String.valueOf((i + 1) * 1001)));
        }

        String payloadType = "medium";
        String description = "Generated medium JSON payload";
        String unit = "bytes";

        int baseSize = estimateMediumBaseJsonSize(payloadType, description, unit, items);
        int padLen = Math.max(0, targetBytes - baseSize - 10); // ,"pad":""

        return new MediumPayload(payloadType, description, unit, items, "7".repeat(padLen));
    }

    private int estimateMediumBaseJsonSize(String payloadType, String description, String unit, List<MediumPayloadItem> items) {
        int size = "{\"payloadType\":\"\",\"description\":\"\",\"unit\":\"\",\"items\":[]}".length();
        size += payloadType.length() + description.length() + unit.length();

        for (int i = 0; i < items.size(); i++) {
            MediumPayloadItem item = items.get(i);
            size += "{\"id\":,\"value\":\"\"}".length();
            size += String.valueOf(item.id()).length();
            size += item.value().length();
            if (i > 0) {
                size += 1; // comma between items
            }
        }

        return size;
    }

    private byte[] generateLargeJsonBytes(int targetBytes) {
        String prefix = "{\"payloadType\":\"large\",\"description\":\"Generated 1GiB numeric JSON payload\",\"unit\":\"numbers\",\"numbers\":[";
        String suffix = "]}";

        byte[] prefixBytes = prefix.getBytes(StandardCharsets.US_ASCII);
        byte[] suffixBytes = suffix.getBytes(StandardCharsets.US_ASCII);

        if (targetBytes <= prefixBytes.length + suffixBytes.length + 1) {
            throw new IllegalArgumentException("large JSON size too small for valid payload");
        }

        byte[] out = new byte[targetBytes];
        int offset = 0;

        System.arraycopy(prefixBytes, 0, out, offset, prefixBytes.length);
        offset += prefixBytes.length;

        String standardNumber = "1234567890";
        byte[] stdNumBytes = standardNumber.getBytes(StandardCharsets.US_ASCII);
        byte[] stdNumCommaBytes = ("," + standardNumber).getBytes(StandardCharsets.US_ASCII);

        boolean firstNumber = true;
        while (offset + stdNumCommaBytes.length + suffixBytes.length < targetBytes) {
            byte[] chunk = firstNumber ? stdNumBytes : stdNumCommaBytes;
            System.arraycopy(chunk, 0, out, offset, chunk.length);
            offset += chunk.length;
            firstNumber = false;
        }

        int remaining = targetBytes - offset - suffixBytes.length;
        if (remaining <= 0) {
            throw new IllegalStateException("could not fit final numeric token");
        }

        if (firstNumber) {
            for (int i = 0; i < remaining; i++) {
                out[offset++] = '9';
            }
        } else {
            if (remaining < 2) {
                throw new IllegalStateException("remaining space too small for comma + number token");
            }
            out[offset++] = ',';
            for (int i = 0; i < remaining - 1; i++) {
                out[offset++] = '9';
            }
        }

        System.arraycopy(suffixBytes, 0, out, offset, suffixBytes.length);
        offset += suffixBytes.length;

        if (offset != targetBytes) {
            throw new IllegalStateException("generated JSON size mismatch");
        }

        return out;
    }

    private byte[] toJson(SmallPayload payload) {
        String json = "{"
                + "\"id\":" + payload.id() + ","
                + "\"protocol\":\"" + payload.protocol() + "\"," 
                + "\"service\":\"" + payload.service() + "\"," 
                + "\"payloadType\":\"" + payload.payloadType() + "\"," 
                + "\"status\":\"" + payload.status() + "\"," 
                + "\"fixed\":" + payload.fixed()
                + "}";
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] toJson(MediumPayload payload) {
        StringBuilder sb = new StringBuilder();
        sb.append('{')
                .append("\"payloadType\":\"").append(payload.payloadType()).append("\",")
                .append("\"description\":\"").append(payload.description()).append("\",")
                .append("\"unit\":\"").append(payload.unit()).append("\",")
                .append("\"items\":[");

        for (int i = 0; i < payload.items().size(); i++) {
            MediumPayloadItem item = payload.items().get(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append('{')
                    .append("\"id\":").append(item.id()).append(',')
                    .append("\"value\":\"").append(item.value()).append("\"")
                    .append('}');
        }

        sb.append("],")
                .append("\"pad\":\"").append(payload.pad()).append("\"")
                .append('}');

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}

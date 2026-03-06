package grgic.antonio.grpc_spring_boot.service;

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

    private SmallPayload smallPayload;
    private MediumPayload mediumPayload;

    @PostConstruct
    public void load() {
        SmallObject smallObject = createSmallPayloadObject();
        MediumObject mediumObject = createMediumPayloadObject(FIFTY_KIB);

        this.small = toJson(smallObject);
        this.medium = toJson(mediumObject);
        this.large = generateLargeJsonBytes(largeJsonSizeBytes > 0 ? largeJsonSizeBytes : ONE_GIB);

        this.smallPayload = toSmallPayloadMessage(smallObject);
        this.mediumPayload = toMediumPayloadMessage(mediumObject);
    }

    public PayloadResponse small() {
        return toPayloadResponse(small);
    }

    public PayloadResponse medium() {
        return toPayloadResponse(medium);
    }

    public PayloadResponse large() {
        return toPayloadResponse(large);
    }

    public byte[] smallBytes() {
        return small;
    }

    public byte[] mediumBytes() {
        return medium;
    }

    public byte[] largeBytes() {
        return large;
    }

    public SmallPayload smallObject() {
        return this.smallPayload;
    }

    public MediumPayload mediumObject() {
        return this.mediumPayload;
    }

    private SmallObject createSmallPayloadObject() {
        return new SmallObject(1, "gRPC", "payload-service", "small", "ok", true);
    }

    private MediumObject createMediumPayloadObject(int targetBytes) {
        List<MediumObjectItem> items = new ArrayList<>(512);
        for (int i = 0; i < 512; i++) {
            items.add(new MediumObjectItem(i + 1, String.valueOf((i + 1) * 1001)));
        }

        String payloadType = "medium";
        String description = "Generated medium JSON payload";
        String unit = "bytes";

        int baseSize = estimateMediumBaseJsonSize(payloadType, description, unit, items);
        int padLen = Math.max(0, targetBytes - baseSize - 10); // ,"pad":""

        return new MediumObject(payloadType, description, unit, items, "7".repeat(padLen));
    }

    private int estimateMediumBaseJsonSize(String payloadType, String description, String unit, List<MediumObjectItem> items) {
        int size = "{\"payloadType\":\"\",\"description\":\"\",\"unit\":\"\",\"items\":[]}".length();
        size += payloadType.length() + description.length() + unit.length();

        for (int i = 0; i < items.size(); i++) {
            MediumObjectItem item = items.get(i);
            size += "{\"id\":,\"value\":\"\"}".length();
            size += String.valueOf(item.id()).length();
            size += item.value().length();
            if (i > 0) {
                size += 1;
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

    private byte[] toJson(SmallObject payload) {
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

    private byte[] toJson(MediumObject payload) {
        StringBuilder sb = new StringBuilder();
        sb.append('{')
                .append("\"payloadType\":\"").append(payload.payloadType()).append("\",")
                .append("\"description\":\"").append(payload.description()).append("\",")
                .append("\"unit\":\"").append(payload.unit()).append("\",")
                .append("\"items\":[");

        for (int i = 0; i < payload.items().size(); i++) {
            MediumObjectItem item = payload.items().get(i);
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

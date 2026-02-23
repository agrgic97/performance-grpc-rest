package grgic.antonio.grpc_spring_boot.model;

import java.util.List;

public record MediumPayload(
        String payloadType,
        String description,
        String unit,
        List<MediumPayloadItem> items
) {
}

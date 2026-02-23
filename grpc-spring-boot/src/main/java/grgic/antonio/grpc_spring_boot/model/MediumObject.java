package grgic.antonio.grpc_spring_boot.model;

import java.util.List;

public record MediumObject(
        String payloadType,
        String description,
        String unit,
        List<MediumObjectItem> items
) {
}

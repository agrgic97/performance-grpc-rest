package grgic.antonio.grpc_spring_boot.model;

import java.util.List;

public record LargeObject(
        String payloadType,
        String description,
        String unit,
        List<MediumObject> items
) {
}

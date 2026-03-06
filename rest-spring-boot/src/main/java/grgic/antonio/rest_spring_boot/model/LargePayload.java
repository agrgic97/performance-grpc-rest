package grgic.antonio.rest_spring_boot.model;

import java.util.List;

public record LargePayload(
        String payloadType,
        String description,
        String unit,
        List<MediumPayload> items
) {
}

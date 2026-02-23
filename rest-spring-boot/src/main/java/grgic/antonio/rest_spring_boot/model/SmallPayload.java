package grgic.antonio.rest_spring_boot.model;

public record SmallPayload(
        int id,
        String protocol,
        String service,
        String payloadType,
        String status,
        boolean fixed
) {}

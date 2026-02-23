package grgic.antonio.grpc_spring_boot.model;

public record SmallObject(
        int id,
        String protocol,
        String service,
        String payloadType,
        String status,
        boolean fixed
) {
}

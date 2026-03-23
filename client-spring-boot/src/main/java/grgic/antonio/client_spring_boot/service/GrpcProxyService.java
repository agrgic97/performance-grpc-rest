package grgic.antonio.client_spring_boot.service;

import grgic.antonio.client_spring_boot.grpc.Empty;
import grgic.antonio.client_spring_boot.grpc.MediumPayload;
import grgic.antonio.client_spring_boot.grpc.PayloadServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class GrpcProxyService {

    private final PayloadServiceGrpc.PayloadServiceBlockingStub javaStub;

    public GrpcProxyService(PayloadServiceGrpc.PayloadServiceBlockingStub javaStub) {
        this.javaStub = javaStub;
    }

    /**
     * Calls the gRPC method on grpc-spring-boot, fully deserializes the protobuf
     * response (reassembly), then discards it.
     */
    public void fetch(String size) {
        Empty request = Empty.newBuilder().build();

        switch (size) {
            case "small"            -> javaStub.getSmall(request);
            case "medium"           -> javaStub.getMedium(request);
            case "large"            -> javaStub.getLarge(request);
            case "large_compressed" -> javaStub.getLargeCompressed(request);
            case "stream_large"     -> {
                Iterator<MediumPayload> it = javaStub.streamLarge(request);
                while (it.hasNext()) it.next();
            }
            default -> throw new IllegalArgumentException("Unknown size: " + size);
        }
    }
}

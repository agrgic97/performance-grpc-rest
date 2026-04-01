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

    public void fetch(String size) {
        Empty request = Empty.newBuilder().build();

        switch (size) {
            case "small"            -> javaStub.getSmall(request);
            case "medium"           -> javaStub.getMedium(request);
            case "large"            -> javaStub.getLarge(request);
            case "large-compressed" -> javaStub.getLargeCompressed(request);
            case "stream-large"     -> {
                Iterator<MediumPayload> it = javaStub.streamLarge(request);
                int count = 0;
                while (it.hasNext()) {
                    it.next();
                    count++;
                }
                if (count != 10) {
                    throw new IllegalStateException(
                        "StreamLarge: expected 10 messages but received " + count
                    );
                }
            }
            default -> throw new IllegalArgumentException("Unknown size: " + size);
        }
    }
}

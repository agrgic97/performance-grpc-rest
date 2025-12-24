package grgic.antonio.grpc_spring_boot.service;

import grgic.antonio.grpc_spring_boot.proto.Empty;
import grgic.antonio.grpc_spring_boot.proto.PayloadResponse;
import grgic.antonio.grpc_spring_boot.proto.PayloadServiceGrpc;
import com.google.protobuf.ByteString;
import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.stub.StreamObserver;

@GrpcService
public class PayloadGrpcService extends PayloadServiceGrpc.PayloadServiceImplBase {

    private final PayloadAssetService assets;

    public PayloadGrpcService(PayloadAssetService assets) {
        this.assets = assets;
    }

    @Override
    public void getSmall(Empty request, StreamObserver<PayloadResponse> responseObserver) {
        responseObserver.onNext(PayloadResponse.newBuilder()
                .setPayload(ByteString.copyFrom(assets.small()))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMedium(Empty request, StreamObserver<PayloadResponse> responseObserver) {
        responseObserver.onNext(PayloadResponse.newBuilder()
                .setPayload(ByteString.copyFrom(assets.medium()))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getLarge(Empty request, StreamObserver<PayloadResponse> responseObserver) {
        responseObserver.onNext(PayloadResponse.newBuilder()
                .setPayload(ByteString.copyFrom(assets.large()))
                .build());
        responseObserver.onCompleted();
    }
}


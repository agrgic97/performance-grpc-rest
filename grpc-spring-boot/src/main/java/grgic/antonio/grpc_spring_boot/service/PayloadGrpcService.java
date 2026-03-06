package grgic.antonio.grpc_spring_boot.service;

import grgic.antonio.grpc_spring_boot.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class PayloadGrpcService extends PayloadServiceGrpc.PayloadServiceImplBase {

    private final PayloadAssetService assets;

    public PayloadGrpcService(@Autowired PayloadAssetService assets) {
        this.assets = assets;
    }

    @Override
    public void getSmall(Empty req, StreamObserver<PayloadResponse> obs) {
        obs.onNext(assets.small());
        obs.onCompleted();
    }

    @Override
    public void getMedium(Empty req, StreamObserver<PayloadResponse> obs) {
        obs.onNext(assets.medium());
        obs.onCompleted();
    }

    @Override
    public void getLarge(Empty req, StreamObserver<PayloadResponse> obs) {
        obs.onNext(assets.large());
        obs.onCompleted();
    }

    @Override
    public void getSmallStructured(Empty req, StreamObserver<SmallPayload> obs) {
        obs.onNext(assets.smallObject());
        obs.onCompleted();
    }

    @Override
    public void getMediumStructured(Empty req, StreamObserver<MediumPayload> obs) {
        obs.onNext(assets.mediumObject());
        obs.onCompleted();
    }

    @Override
    public void getLargeStructured(Empty req, StreamObserver<LargePayload> obs) {
        obs.onNext(assets.largeObject());
        obs.onCompleted();
    }
}

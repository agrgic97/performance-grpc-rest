package grgic.antonio.grpc_spring_boot.service;

import grgic.antonio.grpc_codegen.proto.*;
import io.grpc.stub.ServerCallStreamObserver;
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
    public void getSmall(Empty req, StreamObserver<SmallPayload> obs) {
        obs.onNext(assets.smallObject());
        obs.onCompleted();
    }

    @Override
    public void getMedium(Empty req, StreamObserver<MediumPayload> obs) {
        obs.onNext(assets.mediumObject());
        obs.onCompleted();
    }

    @Override
    public void getLarge(Empty req, StreamObserver<LargePayload> obs) {
        obs.onNext(assets.largeObject());
        obs.onCompleted();
    }

    @Override
    public void getLargeCompressed(Empty req, StreamObserver<LargePayload> obs) {
        ServerCallStreamObserver<LargePayload> serverObs = (ServerCallStreamObserver<LargePayload>) obs;
        serverObs.setCompression("gzip");
        serverObs.onNext(assets.largeObject());
        serverObs.onCompleted();
    }

    @Override
    public void streamLarge(Empty req, StreamObserver<MediumPayload> obs) {
        for (MediumPayload item : assets.largeObject().getItemsList()) {
            obs.onNext(item);
        }
        obs.onCompleted();
    }
}

package grgic.antonio.grpc_spring_boot.service;

import grgic.antonio.grpc_spring_boot.proto.*;
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
    public void getSmall(Empty req, StreamObserver<PayloadResponse> obs) {
        obs.onNext(PayloadResponse.newBuilder()
                .setPayload(ByteString.copyFrom(assets.small()))
                .build());
        obs.onCompleted();
    }

    @Override
    public void getMedium(Empty req, StreamObserver<PayloadResponse> obs) {
        obs.onNext(PayloadResponse.newBuilder()
                .setPayload(ByteString.copyFrom(assets.medium()))
                .build());
        obs.onCompleted();
    }

    @Override
    public void getLarge(Empty req, StreamObserver<PayloadResponse> obs) {
        obs.onNext(PayloadResponse.newBuilder()
                .setPayload(ByteString.copyFrom(assets.large()))
                .build());
        obs.onCompleted();
    }

    @Override
    public void streamSmall(ChunkRequest req, StreamObserver<ChunkPayloadResponse> obs) {
        byte[] data = assets.small();
        streamPayloadInChunks(req, obs, data);
    }

    @Override
    public void streamMedium(ChunkRequest req, StreamObserver<ChunkPayloadResponse> obs) {
        byte[] data = assets.medium();
        streamPayloadInChunks(req, obs, data);
    }

    @Override
    public void streamLarge(ChunkRequest req, StreamObserver<ChunkPayloadResponse> obs) {
        byte[] data = assets.large();
        streamPayloadInChunks(req, obs, data);
    }

    private void streamPayloadInChunks(ChunkRequest req, StreamObserver<ChunkPayloadResponse> obs, byte[] data) {
        int chunkSize = req.getChunkSize();

        if (chunkSize <= 0) {
            obs.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("chunkSize must be > 0")
                    .asRuntimeException());
            return;
        }

        int seq = 0;
        for (int offset = 0; offset < data.length; offset += chunkSize) {
            int len = Math.min(chunkSize, data.length - offset);
            ByteString bs = ByteString.copyFrom(data, offset, len);

            obs.onNext(ChunkPayloadResponse.newBuilder()
                    .setPayloadId(req.getPayloadId())
                    .setSeq(seq++)
                    .setData(bs)
                    .setEof(offset + len >= data.length)
                    .build());
        }
        obs.onCompleted();
    }
}


package grgic.antonio.grpc_spring_boot.service;

import com.google.protobuf.ByteString;
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
    public void streamSmall(ChunkRequest req, StreamObserver<ChunkPayloadResponse> obs) {
        byte[] data = assets.smallBytes();
        streamPayloadInChunks(req, obs, data);
    }

    @Override
    public void streamMedium(ChunkRequest req, StreamObserver<ChunkPayloadResponse> obs) {
        byte[] data = assets.mediumBytes();
        streamPayloadInChunks(req, obs, data);
    }

    @Override
    public void streamLarge(ChunkRequest req, StreamObserver<ChunkPayloadResponse> obs) {
        byte[] data = assets.largeBytes();
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

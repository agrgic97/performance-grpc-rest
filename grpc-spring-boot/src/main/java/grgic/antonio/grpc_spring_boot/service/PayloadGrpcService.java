package grgic.antonio.grpc_spring_boot.service;

import com.google.protobuf.ByteString;
import grgic.antonio.grpc_spring_boot.model.MediumPayloadItem;
import grgic.antonio.grpc_spring_boot.model.SmallPayload;
import grgic.antonio.grpc_spring_boot.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

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
    public void getSmallStructured(Empty req, StreamObserver<grgic.antonio.grpc_spring_boot.proto.SmallPayload> obs) {
        obs.onNext(toSmallPayloadMessage(assets.smallObject()));
        obs.onCompleted();
    }

    @Override
    public void getMediumStructured(Empty req, StreamObserver<MediumPayload> obs) {
        obs.onNext(toMediumPayloadMessage(assets.mediumObject()));
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

    private grgic.antonio.grpc_spring_boot.proto.SmallPayload toSmallPayloadMessage(SmallPayload payload) {
        return grgic.antonio.grpc_spring_boot.proto.SmallPayload.newBuilder()
                .setId(payload.id())
                .setProtocol(payload.protocol())
                .setService(payload.service())
                .setPayloadType(payload.payloadType())
                .setStatus(payload.status())
                .setFixed(payload.fixed())
                .build();
    }

    private MediumPayload toMediumPayloadMessage(grgic.antonio.grpc_spring_boot.model.MediumPayload payload) {
        MediumPayload.Builder builder = MediumPayload.newBuilder()
                .setPayloadType(payload.payloadType())
                .setDescription(payload.description())
                .setUnit(payload.unit());

        for (MediumPayloadItem item : payload.items()) {
            builder.addItems(grgic.antonio.grpc_spring_boot.proto.MediumPayloadItem.newBuilder()
                    .setId(item.id())
                    .setValue(item.value())
                    .build());
        }

        return builder.build();
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

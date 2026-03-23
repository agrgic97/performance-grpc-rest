package grgic.antonio.client_spring_boot.config;

import grgic.antonio.client_spring_boot.grpc.PayloadServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcChannelConfig {

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel grpcJavaChannel(
            @Value("${client.grpc-java.address}") String address) {
        return ManagedChannelBuilder.forTarget(address).usePlaintext().build();
    }

    @Bean
    public PayloadServiceGrpc.PayloadServiceBlockingStub javaStub(ManagedChannel grpcJavaChannel) {
        return PayloadServiceGrpc.newBlockingStub(grpcJavaChannel);
    }
}

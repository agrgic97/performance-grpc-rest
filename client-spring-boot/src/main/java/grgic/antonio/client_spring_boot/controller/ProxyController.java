package grgic.antonio.client_spring_boot.controller;

import grgic.antonio.client_spring_boot.service.GrpcProxyService;
import grgic.antonio.client_spring_boot.service.RestProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyController {

    private final RestProxyService restProxyService;
    private final GrpcProxyService grpcProxyService;

    public ProxyController(RestProxyService restProxyService, GrpcProxyService grpcProxyService) {
        this.restProxyService = restProxyService;
        this.grpcProxyService = grpcProxyService;
    }

    /**
     * Calls rest-spring-boot /api/payload/{size}, deserializes the response into
     * the corresponding model object, and returns an empty 200.
     */
    @GetMapping("/rest/{size}")
    public ResponseEntity<Void> proxyRest(@PathVariable("size") String size) {
        restProxyService.fetch(size);
        return ResponseEntity.ok().build();
    }

    /**
     * Calls grpc-spring-boot via gRPC for the given size, deserializes the
     * protobuf response, and returns an empty 200.
     */
    @GetMapping("/grpc/{size}")
    public ResponseEntity<Void> proxyGrpc(@PathVariable("size") String size) {
        grpcProxyService.fetch(size);
        return ResponseEntity.ok().build();
    }
}

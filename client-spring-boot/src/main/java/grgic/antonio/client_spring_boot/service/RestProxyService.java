package grgic.antonio.client_spring_boot.service;

import grgic.antonio.client_spring_boot.model.LargeObject;
import grgic.antonio.client_spring_boot.model.MediumObject;
import grgic.antonio.client_spring_boot.model.SmallObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RestProxyService {

    private final RestClient javaClient;

    public RestProxyService(
            @Value("${client.rest-java.base-url}") String javaBaseUrl) {
        this.javaClient = RestClient.create(javaBaseUrl);
    }

    public void fetch(String size) {
        switch (size) {
            case "small"  -> javaClient.get().uri("/api/payload/small").retrieve().body(SmallObject.class);
            case "medium" -> javaClient.get().uri("/api/payload/medium").retrieve().body(MediumObject.class);
            case "large"  -> javaClient.get().uri("/api/payload/large").retrieve().body(LargeObject.class);
            default -> throw new IllegalArgumentException("Unknown size: " + size);
        }
    }
}

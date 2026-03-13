package grgic.antonio.rest_spring_boot.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SmallObject {
    int id;
    String protocol;
    String service;
    String payloadType;
    String status;
    boolean fixed;
}

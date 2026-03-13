package grgic.antonio.rest_spring_boot.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MediumObject {
    String payloadType;
    String description;
    String unit;
    List<Integer> items;
}

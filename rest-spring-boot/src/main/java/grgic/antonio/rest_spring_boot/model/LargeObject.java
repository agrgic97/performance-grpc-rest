package grgic.antonio.rest_spring_boot.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class LargeObject {
    List<MediumObject> items;
}

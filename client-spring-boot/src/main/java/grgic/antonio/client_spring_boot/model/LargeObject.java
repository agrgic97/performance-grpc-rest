package grgic.antonio.client_spring_boot.model;

import lombok.Data;

import java.util.List;

@Data
public class LargeObject {
    List<SmallObject> items;
}

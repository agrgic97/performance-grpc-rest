package grgic.antonio.rest_spring_boot.service;

import grgic.antonio.rest_spring_boot.model.LargeObject;
import grgic.antonio.rest_spring_boot.model.MediumObject;
import grgic.antonio.rest_spring_boot.model.SmallObject;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayloadAssetService {
    private SmallObject smallObject;
    private MediumObject mediumObject;
    private LargeObject largeObject;

    @PostConstruct
    public void load() {
        this.smallObject = generateSmallObject();
        this.mediumObject = generateMediumObject();
        this.largeObject = generateLargeObject();
    }

    private SmallObject generateSmallObject() {
        return SmallObject.builder()
                .id(1)
                .payloadType("small")
                .protocol("http")
                .service("benchmark-service")
                .status("ok")
                .fixed(true)
                .build();
    }

    private MediumObject generateMediumObject() {
        MediumObject.MediumObjectBuilder builder = MediumObject.builder()
                .payloadType("medium")
                .unit("POJO")
                .description("Generated medium JSON payload");

        List<Integer> items = new ArrayList<>();

        for (int i = 0; i < 40_000; i++) {
            items.add(i + 10_000_000);
        }

        builder.items(items);

        return builder.build();
    }

    private LargeObject generateLargeObject() {
        LargeObject.LargeObjectBuilder builder = LargeObject.builder();

        List<MediumObject> items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            items.add(generateMediumObject());
        }

        builder.items(items);

        return builder.build();
    }

    public SmallObject small() {
        return this.smallObject;
    }

    public MediumObject medium() {
        return this.mediumObject;
    }

    public LargeObject large() {
        return this.largeObject;
    }
}

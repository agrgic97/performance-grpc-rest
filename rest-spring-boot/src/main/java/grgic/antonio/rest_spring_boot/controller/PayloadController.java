package grgic.antonio.rest_spring_boot.controller;

import grgic.antonio.rest_spring_boot.model.LargeObject;
import grgic.antonio.rest_spring_boot.model.MediumObject;
import grgic.antonio.rest_spring_boot.model.SmallObject;
import grgic.antonio.rest_spring_boot.service.PayloadAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payload")
public class PayloadController {

    private final PayloadAssetService assets;

    public PayloadController(@Autowired PayloadAssetService assets) {
        this.assets = assets;
    }

    @GetMapping(value = "/small", produces = MediaType.APPLICATION_JSON_VALUE)
    public SmallObject small() {
        return assets.small();
    }

    @GetMapping(value = "/medium", produces = MediaType.APPLICATION_JSON_VALUE)
    public MediumObject medium() {
        return assets.medium();
    }

    @GetMapping(value = "/large", produces = MediaType.APPLICATION_JSON_VALUE)
    public LargeObject large() {
        return assets.large();
    }
}

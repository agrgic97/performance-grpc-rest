package grgic.antonio.rest_spring_boot.controller;

import grgic.antonio.rest_spring_boot.service.PayloadAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payload")
public class PayloadController {

    private final PayloadAssetService assets;

    public PayloadController(@Autowired PayloadAssetService assets) {
        this.assets = assets;
    }

    @GetMapping(value = "/small", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> small() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.APPLICATION_JSON)
                .body(assets.small());
    }

    @GetMapping(value = "/medium", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> medium() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.APPLICATION_JSON)
                .body(assets.medium());
    }

    @GetMapping(value = "/large", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> large() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.IMAGE_PNG)
                .body(assets.large());
    }
}

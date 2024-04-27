package com.podlasenko.stringtoimage.controller;

import com.podlasenko.stringtoimage.api.model.ImageText;
import com.podlasenko.stringtoimage.service.ImageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
public class TextToImageController {

    private final ImageGenerator imageGenerator;

    @PostMapping("/convert")
    public ResponseEntity<Resource> getUserDetails(@RequestBody ImageText imageText) {
        byte[] bytes = imageGenerator.createImage(imageText);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }
}

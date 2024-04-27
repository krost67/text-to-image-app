package com.podlasenko.stringtoimage.api.model;

import lombok.Data;

@Data
public class ImageText {

    private String text;
    private Integer backgroundColor;
    private Integer fontSize;
    private Double displayWidth;
}

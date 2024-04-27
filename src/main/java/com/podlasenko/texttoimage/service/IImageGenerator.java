package com.podlasenko.texttoimage.service;

import com.podlasenko.texttoimage.api.model.ImageText;

public interface IImageGenerator {

    byte[] createImage(ImageText imageText);
}

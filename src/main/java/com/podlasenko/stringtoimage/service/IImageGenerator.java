package com.podlasenko.stringtoimage.service;

import com.podlasenko.stringtoimage.api.model.ImageText;

public interface IImageGenerator {

    byte[] createImage(ImageText imageText);
}

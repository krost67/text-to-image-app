package com.podlasenko.texttoimage.service;

import com.podlasenko.texttoimage.api.model.ImageText;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class ImageGenerator implements IImageGenerator {

    private static final Integer DEFAULT_DISPLAY_WIDTH = 1024;
    private static final Integer DEFAULT_FONT_SIZE = 15;
    private static final Integer DEFAULT_BACKGROUND_COLOR = 0xffffff;

    @Override
    public byte[] createImage(ImageText imageText) {
        double displayWidth = Objects.nonNull(imageText.getDisplayWidth()) ? imageText.getDisplayWidth() : DEFAULT_DISPLAY_WIDTH;
        int fontSize = Objects.nonNull(imageText.getFontSize()) ? imageText.getFontSize() : DEFAULT_FONT_SIZE;

        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = tempImage.createGraphics();
        setGraphicsHints(graphics2D);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
        graphics2D.setFont(font);

        List<String> questionLines = getImageTextLines(imageText.getText(), (int) displayWidth, graphics2D, font);
        int height = graphics2D.getFontMetrics().getHeight() * questionLines.size();
        graphics2D.dispose();

        BufferedImage image = new BufferedImage((int) displayWidth, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        setGraphicsHints(graphics);
        graphics.setColor(
                new Color(Objects.isNull(imageText.getBackgroundColor()) ? DEFAULT_BACKGROUND_COLOR : imageText.getBackgroundColor()));
        graphics.fillRect(0, 0, (int) displayWidth, height);
        graphics.setColor(Color.BLACK);
        graphics.setFont(font);

        drawTextLines(graphics, questionLines);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error during image creating", e);
        }
    }

    private void setGraphicsHints(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    }

    private void drawTextLines(Graphics2D graphics, List<String> lines) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int y = fontMetrics.getAscent();
        for (String line : lines) {
            graphics.drawString(line, 0, y);
            y += fontMetrics.getHeight();
        }
    }

    private List<String> getImageTextLines(String imageText, int displayWidth, Graphics2D graphics, Font font) {
        List<String> imageTextLines = new ArrayList<>();
        String[] textLines = imageText.split("\n");

        for (String line : textLines) {
            imageTextLines.addAll(convertToImageTextLines(line, displayWidth, graphics, font));
        }

        return imageTextLines;
    }

    private List<String> convertToImageTextLines(String textLine, int displayWidth, Graphics2D graphics, Font font) {
        String[] words = textLine.split(" ");
        FontMetrics fontMetrics = graphics.getFontMetrics(font);

        List<String> imageTextLines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int currentWidth = 0;

        for (String word : words) {
            int wordWidth = fontMetrics.stringWidth(word + " ");

            // Check if the current word can fit on the current line
            if (currentWidth + wordWidth > displayWidth) {
                if (currentLine.isEmpty()) {
                    // This case handles very long words that exceed the display width even on a new line
                    imageTextLines.add(word);
                    currentWidth = 0;
                } else {
                    // Finish the current line and start a new one with the current word
                    imageTextLines.add(currentLine.toString());
                    currentLine = new StringBuilder(word + " ");
                    currentWidth = wordWidth;
                }
            } else {
                // Append the word to the current line
                currentLine.append(word).append(" ");
                currentWidth += wordWidth;
            }
        }

        // Add the last line if there's any content left
        if (!currentLine.isEmpty()) {
            imageTextLines.add(currentLine.toString().trim());
        }

        return imageTextLines;
    }
}

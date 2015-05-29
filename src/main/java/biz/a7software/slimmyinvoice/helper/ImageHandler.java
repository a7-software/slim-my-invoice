package biz.a7software.slimmyinvoice.helper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The ImageHandler class retrieves an image from file path and holds it.
 */
public class ImageHandler {

    private BufferedImage image;

    public ImageHandler(File file) throws IOException {
        image = ImageIO.read(file);
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }
}
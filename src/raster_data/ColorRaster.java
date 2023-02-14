package raster_data;

import transforms.Col;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class ColorRaster implements Raster<Col> {

    private final BufferedImage image;
    private final Col background;

    public ColorRaster(int width, int height, Col background) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.background = background;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public boolean setPixel(int x, int y, Col pixel) {
        if (isValidAddress(x, y)) {
            image.setRGB(x, y, pixel.getRGB());
            return true;
        }
        return false;
    }

    @Override
    public Optional<Col> getPixel(int x, int y) {
        if (isValidAddress(x, y)) return Optional.of(new Col(image.getRGB(x, y)));
        return Optional.empty();
    }

    @Override
    public void clear() {
        Graphics g = image.getGraphics();
        g.setColor(new Color(background.getRGB()));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    public void present(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}

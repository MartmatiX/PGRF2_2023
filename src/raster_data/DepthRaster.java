package raster_data;

import java.util.Optional;

public class DepthRaster implements Raster<Double> {

    private final double[][] array;

    public DepthRaster(int width, int height) {
        this.array = new double[width][height];
    }

    @Override
    public int getWidth() {
        return array.length;
    }

    @Override
    public int getHeight() {
        return array[0].length;
    }

    @Override
    public boolean setPixel(int x, int y, Double pixel) {
        if (isValidAddress(x, y)) {
            array[x][y] = pixel;
            return true;
        }
        return false;
    }

    @Override
    public Optional<Double> getPixel(int x, int y) {
        if (isValidAddress(x, y)) {
            return Optional.of(array[x][y]);
        }
        return Optional.empty();
    }

    @Override
    public void clear() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                array[i][j] = 1.0;
            }
        }
    }
}

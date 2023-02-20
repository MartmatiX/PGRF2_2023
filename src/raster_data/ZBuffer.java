package raster_data;

import transforms.Col;

import java.util.Optional;

public class ZBuffer {

    private final Raster<Col> colRaster;
    private final Raster<Double> depthRaster;


    public ZBuffer(Raster<Col> colRaster) {
        this.colRaster = colRaster;
        this.depthRaster = new DepthRaster(colRaster.getWidth(), colRaster.getHeight());
        this.clear();
    }

    public void setPixel(int x, int y, double z, Col pixel) {
        Optional<Double> zOptional = depthRaster.getPixel(x, y);
        Optional<Col> colOptional = colRaster.getPixel(x, y);
        if (zOptional.isEmpty() || colOptional.isEmpty()) {
            return;
        }
        if (zOptional.get() > z) {
            if (colRaster.isValidAddress(x, y) && depthRaster.isValidAddress(x, y)) {
                depthRaster.setPixel(x, y, z);
                colRaster.setPixel(x, y, pixel);
            }
        }
    }

    public void clear() {
        depthRaster.clear();
        colRaster.clear();
    }

    public Raster<Col> getColRaster() {
        return colRaster;
    }
}

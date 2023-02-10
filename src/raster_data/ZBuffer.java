package raster_data;

import transforms.Col;

public class ZBuffer {

    private final Raster<Col> colRaster;
    private final Raster<Double> depthRaster;


    public ZBuffer(Raster<Col> colRaster) {
        this.colRaster = colRaster;
        this.depthRaster = new DepthRaster(10, 10);
    }

    public void setPixel(int x, int y, double z, Col pixel) {
        if (colRaster.isValidAddress(x, y) && depthRaster.isValidAddress(x, y)) {
            depthRaster.setPixel(x, y, z);
            colRaster.setPixel(x, y, pixel);
        }
    }

    /**
     * Method cycles through entire panel and for each pixel sets its value in depthRaster to 1.0
     * and for every pixel in colRaster to default color
     */
    public void clear() {
        for (int i = 0; i < depthRaster.getWidth(); i++) {
            for (int j = 0; j < depthRaster.getHeight(); j++) {
                depthRaster.setPixel(i, j, 1.0);
                colRaster.setPixel(i, j, new Col(0, 0, 0));
            }
        }
    }
}

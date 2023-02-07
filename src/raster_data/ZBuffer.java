package raster_data;

import transforms.Col;

public class ZBuffer {

    private final Raster<Col> colRaster;
    private final Raster<Double> depthRaster;


    public ZBuffer(Raster<Col> colRaster) {
        this.colRaster = colRaster;
        this.depthRaster = new DepthRaster(10, 10);
    }

    public void setPixel(int x, int y, double z, Col pixe) {
        // TODO: finish
    }

    public void clear() {
        // TODO: finish
    }
}

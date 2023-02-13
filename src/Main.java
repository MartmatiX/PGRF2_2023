import raster_data.ColorRaster;
import raster_data.Raster;
import raster_data.ZBuffer;
import transforms.Col;

public class Main {

    public static void main(String[] args) {
        final Raster<Col> colRaster = new ColorRaster(10, 10, new Col(50, 50, 50));
        colRaster.getPixel(3, 2).ifPresent(System.out::println);
        colRaster.setPixel(3, 2, new Col(255, 0, 0));
        colRaster.getPixel(3, 2).ifPresent(System.out::println);
        colRaster.setPixel(3, 2, new Col(0, 0, 255));
        colRaster.getPixel(3, 2).ifPresent(System.out::println);
        colRaster.setPixel(3, 2, new Col(0, 255, 0));
        colRaster.getPixel(3, 2).ifPresent(System.out::println); // (0.0, 1.0, 0.0, 1.0)
        colRaster.clear();
        System.out.println();

        final ZBuffer zBuffer = new ZBuffer(colRaster);
        colRaster.getPixel(3, 2).ifPresent(System.out::println);
        zBuffer.setPixel(3, 2, 0.5, new Col(255, 0, 0));
        colRaster.getPixel(3, 2).ifPresent(System.out::println);
        zBuffer.setPixel(3, 2, 0.2, new Col(0, 255, 0));
        colRaster.getPixel(3, 2).ifPresent(System.out::println);
        zBuffer.setPixel(3, 2, 0.7, new Col(0, 0, 255));
        colRaster.getPixel(3, 2).ifPresent(System.out::println); // should be (0.0, 0.0, 1.0, 1.0)

    }

}

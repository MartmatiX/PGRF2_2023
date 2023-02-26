package rasterops;

import linalg.Lerp;
import object_data.Vertex;
import raster_data.ZBuffer;

public class Liner {

    private final ZBuffer zBuffer;
    private final Lerp lerp = new Lerp();

    public Liner(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }


    /**
     * Bresenham's Line Algorithm
     * https://www.sanfoundry.com/java-program-bresenham-line-algorithm/
     *
     * @param v1 start vertex
     * @param v2 end vertex
     */
    public void draw(Vertex v1, Vertex v2) {
        int x1 = (int) v1.getPosition().getX();
        int y1 = (int) v1.getPosition().getY();

        int x2 = (int) v2.getPosition().getX();
        int y2 = (int) v2.getPosition().getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;
        int e2;

        while (true) {
            final double t = calculateT(x1, y1, x2, y2);
            final Vertex v = lerp.compute(v1, v2, t);

            zBuffer.setPixel(x1, y1, v.getPosition().getZ(), v.getColor());

            if (x1 == x2 && y1 == y2) break;

            e2 = 2 * err;

            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }

            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
        }
    }

    public double calculateT(int x1, int y1, int x2, int y2) {
        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {
            int xMin = Integer.min(x1, x2);
            int xMax = Integer.max(x1, x2);
            return (double) (x1 - xMin) / (xMax - xMin);
        }
        int yMin = Integer.min(y1, y2);
        int yMax = Integer.max(y1, y2);
        return (double) (y1 - yMin) / (yMax - yMin);
    }

}

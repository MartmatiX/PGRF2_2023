package rasterops;

import linalg.Lerp;
import object_data.Vertex;
import raster_data.Raster;
import raster_data.ZBuffer;
import transforms.Col;

import java.util.List;
import java.util.stream.Stream;

public class Triangler {

    private final Lerp lerp = new Lerp();
    private final ZBuffer zBuffer;
    private final Raster<Col> image;

    public Triangler(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
        this.image = zBuffer.getColRaster();
    }

    private List<Vertex> sorted(Vertex v1, Vertex v2, Vertex v3) {
        Vertex tmp;
        if (v2.getPosition().getY() < v1.getPosition().getY()) {
            tmp = v1;
            v1 = v2;
            v2 = tmp;
        }
        if (v3.getPosition().getY() < v1.getPosition().getY()) {
            tmp = v1;
            v1 = v3;
            v3 = tmp;
        }
        if (v3.getPosition().getY() < v2.getPosition().getY()) {
            tmp = v2;
            v2 = v3;
            v3 = tmp;
        }
        return List.of(v1, v2, v3);
    }

    private List<Vertex> sortedAlt(Vertex v1, Vertex v2, Vertex v3) {
        return Stream.of(v1, v2, v3).sorted((o1, o2) -> {
            if (o1.getPosition().getY() < o2.getPosition().getY()) return -1;
            else if (o1.getPosition().getY() > o2.getPosition().getY()) return 1;
            return 0;
        }).toList();
    }

    private void drawFirstHalf(Vertex a, Vertex b, Vertex c) {
        final int yMin = (int) a.getPosition().getY();
        final double yMax = b.getPosition().getY();
        for (int y = yMin; y < yMax; y++) {
            final double t1 = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            final double t2 = (y - a.getPosition().getY()) / (b.getPosition().getY() - a.getPosition().getY());
            final Vertex v1 = lerp.compute(a, c, t1);
            final Vertex v2 = lerp.compute(a, b, t2);
            final Vertex vMin = (v1.getPosition().getX() < v2.getPosition().getX()) ? v1 : v2;
            final Vertex vMax = (vMin == v1) ? v2 : v1;
            final int xMin = (int) vMin.getPosition().getX();
            final double xMax = vMax.getPosition().getX();
            for (int x = xMin; x < xMax; x++) {
                final double t = (x - xMin) / (xMax - xMin);
                final Vertex v = lerp.compute(vMin, vMax, t);
                zBuffer.setPixel(x, y, v.getPosition().getZ(), v.getColor());
            }
        }
    }

    private void drawSecondHalf(Vertex a, Vertex b, Vertex c) {
        final int yMin = (int) b.getPosition().getY();
        final double yMax = c.getPosition().getY();
        for (int y = yMin; y < yMax; y++) {
            final double t1 = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            final double t2 = (y - b.getPosition().getY()) / (c.getPosition().getY() - b.getPosition().getY());
            final Vertex v1 = lerp.compute(a, c, t1);
            final Vertex v2 = lerp.compute(b, c, t2);
            final Vertex vMin = (v1.getPosition().getX() < v2.getPosition().getX()) ? v1 : v2;
            final Vertex vMax = (vMin == v1) ? v2 : v1;
            final int xMin = (int) vMin.getPosition().getX();
            final double xMax = vMax.getPosition().getX();
            for (int x = xMin; x < xMax; x++) {
                final double t = (x - xMin) / (xMax - xMin);
                final Vertex v = lerp.compute(vMin, vMax, t);
                zBuffer.setPixel(x, y, v.getPosition().getZ(), v.getColor());
            }
        }
    }

    private void drawTriangle(Vertex a, Vertex b, Vertex c) {
        double yA = a.getPosition().getY();
        double yB = b.getPosition().getY();
        double yC = c.getPosition().getY();

        final double distanceYCA = yC - yA;
        final double distanceYCB = yC - yB;
        final double distanceYBA = yB - yA;

        for (int y = (int) yA; y < yB; y++) {
            final double t1 = (y - yA) / distanceYCA;
            final double t2 = (y - yA) / distanceYBA;

            final Vertex v1 = lerp.compute(a, c, t1);
            final Vertex v2 = lerp.compute(a, b, t2);

            final Vertex vMin = (v1.getPosition().getX() < v2.getPosition().getX()) ? v1 : v2;
            final Vertex vMax = (v1.getPosition().getX() < v2.getPosition().getX()) ? v2 : v1;

            int xMin = (int) vMin.getPosition().getX();
            int xMax = (int) vMax.getPosition().getX();

            for (int x = xMin; x <= xMax; x++) {
                final double t = (x - xMin) / (double) (xMax - xMin);
                final Vertex v = lerp.compute(vMin, vMax, t);
                zBuffer.setPixel(x, y, v.getPosition().getZ(), v.getColor());
            }
        }

        for (int y = (int) yB; y < yC; y++) {
            final double t1 = (y - yA) / distanceYCA;
            final double t2 = (y - yB) / distanceYCB;

            final Vertex v1 = lerp.compute(a, c, t1);
            final Vertex v2 = lerp.compute(b, c, t2);

            Vertex vMin = (v1.getPosition().getX() < v2.getPosition().getX()) ? v1 : v2;
            final Vertex vMax = (v1.getPosition().getX() < v2.getPosition().getX()) ? v2 : v1;

            int xMin = (int) vMin.getPosition().getX();
            int xMax = (int) vMax.getPosition().getX();

            for (int x = xMin; x <= xMax; x++) {
                final double t = (x - xMin) / (double) (xMax - xMin);
                final Vertex v = lerp.compute(vMin, vMax, t);
                zBuffer.setPixel(x, y, v.getPosition().getZ(), v.getColor());
            }
        }
    }

    public void draw(Vertex v1, Vertex v2, Vertex v3) {
        final List<Vertex> ordered = sorted(v1, v2, v3);
        drawTriangle(ordered.get(0), ordered.get(1), ordered.get(2));
    }

}

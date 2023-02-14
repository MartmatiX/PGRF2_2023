package rasterops;

import linalg.Lerp;
import object_data.Vertex;
import raster_data.Raster;
import transforms.Col;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Triangler {

    private final Lerp lerp = new Lerp();
    private final Raster<Col> image; // pozdeji ZBuffer

    public Triangler(Raster<Col> image) {
        this.image = image;
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
        return Stream.of(v1, v2, v3).sorted(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                if (o1.getPosition().getY() < o2.getPosition().getY()) return -1;
                else if (o1.getPosition().getY() > o2.getPosition().getY()) return 1;
                return 0;
            }
        }).toList();
    }

    private void drawFirstHalf(Vertex a, Vertex b, Vertex c) { // TODO: orezani a sprava rasterizace, od 0 ne od a.getY a ne do konce ale do hranice okna image.getHeight() i guess
        final int yMin = (int) a.getPosition().getY(); // TODO: zaroven vyresit neprosvitani hran
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
                image.setPixel(x, y, v.getColor());
            }
        }
    }

    private void drawSecondHalf(Vertex a, Vertex b, Vertex c) {
        // TODO: finish this
    }

    public void draw(Vertex v1, Vertex v2, Vertex v3) {
        final List<Vertex> ordered = sorted(v1, v2, v3);
        drawFirstHalf(ordered.get(0), ordered.get(1), ordered.get(2));
        drawSecondHalf(ordered.get(0), ordered.get(1), ordered.get(2));
    }

}

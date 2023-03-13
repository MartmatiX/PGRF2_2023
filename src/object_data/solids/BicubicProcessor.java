package object_data.solids;

import object_data.Part;
import object_data.Solid;
import object_data.Topology;
import object_data.Vertex;
import transforms.*;

import java.util.ArrayList;
import java.util.List;

public class BicubicProcessor implements Solid {

    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();
    private final List<Part> parts;

    public BicubicProcessor(Mat4 cubicType, Col color) {
        Point3D[] points = new Point3D[] {
                new Point3D(0, 1, 0),
                new Point3D(-1, -0.5, 0.3),
                new Point3D(0.3, 0.8, -0.5),
                new Point3D(0, -1, 0),
                new Point3D(1, 0, 0),
                new Point3D(-0.5, 0.3, -1),
                new Point3D(0.8, -0.5, 0.3),
                new Point3D(-1, 0, 0),
                new Point3D(-1, -0.8, 0.6),
                new Point3D(0.6, 1.2, -0.3),
                new Point3D(0.5, -0.8, -0.4),
                new Point3D(0.9, 0.5, -0.5),
                new Point3D(0.7, 0.1, -0.9),
                new Point3D(0.6, 0.7, 0.3),
                new Point3D(-0.8, -0.4, -0.6),
                new Point3D(0.7, -0.3, 0.7)
        };

        Bicubic bicubic = new Bicubic(cubicType, points);

        int ACCURACY = 30;
        for (int i = 0; i < ACCURACY; i++) {
            for (int j = 0; j < ACCURACY; j++) {
                this.vertices.add(new Vertex(new Point3D(bicubic.compute((double) i / ACCURACY, (double) j / ACCURACY)), color));
                if (j != 0) {
                    this.indices.add((j - 1) + (ACCURACY * i));
                    this.indices.add((j) + (ACCURACY * i));
                }
                if (i != 0) {
                    this.indices.add(j + (ACCURACY) * (i - 1));
                    this.indices.add(j + (ACCURACY * i));
                }
            }
        }

        this.parts = List.of(new Part(Topology.LINE_LIST, 0, 1700));
    }

    @Override
    public List<Vertex> getVertices() {
        return this.vertices;
    }

    @Override
    public List<Integer> getIndices() {
        return this.indices;
    }

    @Override
    public List<Part> getParts() {
        return this.parts;
    }
}
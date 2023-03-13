package object_data.solids;

import object_data.Part;
import object_data.Solid;
import object_data.Topology;
import object_data.Vertex;
import transforms.Col;
import transforms.Point3D;

import java.util.List;

public class Cube implements Solid {

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Part> parts;

    public Cube(boolean isWired) {
        if (isWired) {
            vertices = List.of(
                    new Vertex(new Point3D(0, 0, 0), new Col(255, 255, 255)),
                    new Vertex(new Point3D(0, 1, 0), new Col(255, 255, 255)),
                    new Vertex(new Point3D(1, 1, 0), new Col(255, 255, 255)),
                    new Vertex(new Point3D(1, 0, 0), new Col(255, 255, 255)),
                    new Vertex(new Point3D(0, 0, 1), new Col(255, 255, 255)),
                    new Vertex(new Point3D(0, 1, 1), new Col(255, 255, 255)),
                    new Vertex(new Point3D(1, 1, 1), new Col(255, 255, 255)),
                    new Vertex(new Point3D(1, 0, 0), new Col(255, 255, 255))
            );

            indices = List.of(
                    0, 1, 0, 4, 1, 2, 1, 5, 2, 3, 2, 6, 3, 0, 3, 7, 4, 5, 5, 6, 6, 7, 7, 4
            );

            parts = List.of(
                    new Part(Topology.LINE_LIST, 0, 12)
            );
        } else {
            vertices = List.of(
                    new Vertex(new Point3D(0, 0, 0), new Col(255, 255, 0)),
                    new Vertex(new Point3D(0, 1, 0), new Col(0, 255, 255)),
                    new Vertex(new Point3D(1, 0, 0), new Col(255, 0, 255)),
                    new Vertex(new Point3D(1, 1, 0), new Col(255, 255, 0)),
                    new Vertex(new Point3D(0, 0, 1), new Col(0, 255, 255)),
                    new Vertex(new Point3D(0, 1, 1), new Col(255, 0, 255)),
                    new Vertex(new Point3D(1, 0, 1), new Col(0, 255, 255)),
                    new Vertex(new Point3D(1, 1, 1), new Col(255, 0, 255))
            );
            indices = List.of(0, 2, 3, 1, 5, 4, 6, 2, 7, 3, 1, 5, 4, 6, 2, 3);
            parts = List.of(
                    new Part(Topology.TRIANGLE_FAN, 0, 8),
                    new Part(Topology.TRIANGLE_FAN, 8, 8)
            );
        }
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

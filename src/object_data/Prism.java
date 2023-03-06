package object_data;

import transforms.Col;
import transforms.Point3D;

import java.util.List;

public class Prism implements Solid {

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Part> parts;

    public Prism(boolean isWired) {
        if (isWired) {
            this.vertices = List.of(
                    new Vertex(new Point3D(0, 0, 0.1), new Col(255, 255, 255)),
                    new Vertex(new Point3D(-0.2, -0.2, -0.1), new Col(255, 255, 255)),
                    new Vertex(new Point3D(-0.2, 0.2, -0.1), new Col(255, 255, 255)),
                    new Vertex(new Point3D(0.2, 0.2, -0.1), new Col(255, 255, 255)),
                    new Vertex(new Point3D(0.2, -0.2, -0.1), new Col(255, 255, 255))
            );
            this.indices = List.of(
                    1, 2, 2, 3, 3, 4, 1, 4, 0, 1, 0, 2, 0, 3, 0, 4
            );
            this.parts = List.of(
                    new Part(Topology.LINE_LIST, 0, 8)
            );
        } else {
            this.vertices = List.of(
                    new Vertex(new Point3D(0, 0, 0.1), new Col(255, 0, 255)), // tip, 0
                    new Vertex(new Point3D(-0.2, -0.2, -0.1), new Col(255, 255, 0)), // right bottom, 1
                    new Vertex(new Point3D(-0.2, 0.2, -0.1), new Col(0, 255, 255)), // left bottom, 2
                    new Vertex(new Point3D(0.2, 0.2, -0.1), new Col(255, 255, 0)), // left top, 3
                    new Vertex(new Point3D(0.2, -0.2, -0.1), new Col(0, 255, 255)), // right top, 4
                    new Vertex(new Point3D(0, 0, -0.1), new Col(255, 0, 255)) // base center, 5
            );

            this.indices = List.of(
                    0, 1, 2, 3, 4, 1,
                    5, 1, 2, 3, 4, 1
            );

            this.parts = List.of(
                    new Part(Topology.TRIANGLE_FAN, 0, 6), // shell
                    new Part(Topology.TRIANGLE_FAN, 6, 6) // base
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

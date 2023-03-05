package object_data;

import transforms.Col;
import transforms.Point3D;

import java.util.List;

public class AxisRGB implements Solid{

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Part> parts;

    public AxisRGB() {
        vertices = List.of(
                new Vertex(new Point3D(0, 0, 0), new Col(255, 0, 0)),
                new Vertex(new Point3D(0, 0, 0), new Col(0, 255, 0)),
                new Vertex(new Point3D(0, 0, 0), new Col(0, 0, 255)),

                new Vertex(new Point3D(1, 0, 0), new Col(255, 0, 0)),
                new Vertex(new Point3D(0, 1, 0), new Col(0, 255, 0)),
                new Vertex(new Point3D(0, 0, 1), new Col(0, 0, 255))
        );
        indices = List.of(
                0, 3,
                1, 4,
                2, 5

        );

        parts = List.of(
                new Part(Topology.LINE_LIST, 0, 2),
                new Part(Topology.LINE_LIST, 2, 2),
                new Part(Topology.LINE_LIST, 4, 1)
        );
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

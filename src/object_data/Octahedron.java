package object_data;

import transforms.Col;
import transforms.Point3D;

import java.util.List;

public class Octahedron implements Solid{

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Part> parts;

    public Octahedron(boolean isWired){
        if (isWired){
            this.vertices = List.of(
                    new Vertex(new Point3D(0.1, 0, 0), new Col(255, 255, 255)), // back center
                    new Vertex(new Point3D(-0.1, 0, 0), new Col(255, 255, 255)), // front center
                    new Vertex(new Point3D(0, 0.1, 0), new Col(255, 255, 255)), // left center
                    new Vertex(new Point3D(0, -0.1, 0), new Col(255, 255, 255)), // right center
                    new Vertex(new Point3D(0, 0, 0.1), new Col(255, 255, 255)), // top
                    new Vertex(new Point3D(0, 0, -0.1), new Col(255, 255, 255)) // bottom
            );

            this.indices = List.of(
                    0, 2, 2, 1, 1, 3, 3, 0, 0, 4, 1, 4, 2, 4, 3, 4, 0, 5, 1, 5, 2, 5, 3, 5
            );

            this.parts = List.of(
                    new Part(Topology.LINE_LIST, 0, 12)
            );
        } else {
            this.vertices = List.of(
                    new Vertex(new Point3D(0, 0, 0.1), new Col(255, 255, 0)), // top, 0
                    new Vertex(new Point3D(0, 0, -0.1), new Col(255, 0, 255)), // bottom, 1
                    new Vertex(new Point3D(0.1, 0, 0), new Col(255, 0, 255)), // back center, 2
                    new Vertex(new Point3D(-0.1, 0, 0), new Col(255, 255, 0)), // front center, 3
                    new Vertex(new Point3D(0, 0.1, 0), new Col(255, 255, 0)), // left center, 4
                    new Vertex(new Point3D(0, -0.1, 0), new Col(255, 0, 255)) // right center, 5
            );

            this.indices = List.of(
                    0, 2, 5, 3, 4, 2,
                    1, 2, 5, 3, 4, 2
            );

            this.parts = List.of(
                    new Part(Topology.TRIANGLE_FAN, 0, 6),
                    new Part(Topology.TRIANGLE_FAN, 6, 6)
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

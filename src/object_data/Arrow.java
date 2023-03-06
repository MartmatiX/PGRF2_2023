package object_data;

import transforms.Col;
import transforms.Point3D;

import java.util.List;

public class Arrow implements Solid {

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Part> parts;

    public Arrow(boolean isWired) {
        if (isWired) {
            // TODO: 06.03.2023 finish this
            this.vertices = List.of(

            );
            this.indices = List.of(

            );
            this.parts = List.of(

            );
        } else {
            this.vertices = List.of(
                    new Vertex(new Point3D(0, 0, 0), new Col(255, 0, 255)), // 0
                    new Vertex(new Point3D(0.8, 0, 0), new Col(0, 255, 255)), // 1
                    new Vertex(new Point3D(1, 0, 0), new Col(255, 255, 0)), // 2

                    new Vertex(new Point3D(0.8, -0.2, 0.2), new Col(0, 255, 255)), // 3
                    new Vertex(new Point3D(0.8, 0.2, 0.2), new Col(255, 255, 0)), // 4
                    new Vertex(new Point3D(0.8, 0.2, -0.2), new Col(0, 255, 255)), // 5
                    new Vertex(new Point3D(0.8, -0.2, -0.2), new Col(255, 0, 255)) // 6

            );
            this.indices = List.of(
                    0, 1, // arrow
                    1, 3, 4, 5, 6, 3, // base
                    2, 3, 4, 5, 6, 3 // shell
            );
            this.parts = List.of(
                    new Part(Topology.LINE_LIST, 0, 1),
                    new Part(Topology.TRIANGLE_FAN, 2, 4),
                    new Part(Topology.TRIANGLE_FAN, 8, 4)
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

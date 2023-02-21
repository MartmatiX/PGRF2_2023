package object_data;

import java.util.List;

public interface Solid {

    List<Vertex> getVertices();
    List<Integer> getIndices();
    List<Part> getParts();

}

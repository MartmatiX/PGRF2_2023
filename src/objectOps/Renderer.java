package objectOps;

import object_data.Part;
import object_data.Scene;
import object_data.Solid;
import object_data.Vertex;
import raster_data.ZBuffer;
import rasterops.Liner;
import transforms.Mat4;

import java.util.List;

public class Renderer {

    private final Liner liner = new Liner();
    private final ZBuffer zBuffer = new ZBuffer();

    public void drawScene(Scene scene, Mat4 viewMat, Mat4 projectionMat) {
        final List<Solid> solids = scene.getSolids();
        final List<Mat4> modelMats = scene.getModelMats();
        final Mat4 viewProjection = viewMat.mul(projectionMat);
        for (int i = 0; i < solids.size(); i++) {
            Solid solid = solids.get(i);
            Mat4 modelMat = modelMats.get(i);
            final Mat4 transformation = modelMat.mul(viewProjection);
            drawSolid(solid, transformation);
        }
    }

    public void drawSolid(Solid solid, Mat4 transformation) {
        final List<Vertex> vertices = solid.getVertices().stream().map(v -> v.transformed(transformation)).toList();
        final List<Integer> indices = solid.getIndices();
        for (Part part : solid.getParts()){
            switch (part.getTopology()){
                case LINE_LIST -> {
                    // for all lines
                    for (int i = part.getOffset(); i < part.getOffset() + part.getCount() * 2; i += 2) {
                        final Vertex v1 = vertices.get(indices.get(i));
                        final Vertex v2 = vertices.get(indices.get(i + 1));
                        if (!isOutOfViewSpace(List.of(v1, v2))){
                            List<Vertex> clippedZ = clipZ(v1, v2);
                            liner.draw(v1.dehomog().toViewPort(zBuffer.getWidth), v2.dehomog().toViewPort(zBuffer.getWidth)); // TODO: 21.02.2023 Finish this, add methods
                        }
                    }
                }
                case TRIANGLE_FAN -> {
                    // TODO: 21.02.2023 Might finish this as well
                }
            }
        }
    }

    private boolean isOutOfViewSpace(List<Vertex> vertices){
        final boolean allTooLeft = vertices.stream().allMatch(v -> v.getPosition().getX() < -v.getPosition().getW());
        final boolean allTooRight = vertices.stream().allMatch(v -> v.getPosition().getX() > v.getPosition().getW());
        // TODO: 21.02.2023 Finish this
        final boolean allTooUp = false;
        final boolean allTooDown = false;
        final boolean allTooClose = false;
        final boolean allTooFar = false;
        return false;
    }

    private List<Vertex> clipZ(Vertex v1, Vertex v2){
        // TODO: 21.02.2023 Rewrite and finish this
        return List.of(v1, v2);
    }

    private List<Vertex> clipZ(Vertex v1, Vertex v2, Vertex v3){ // TODO: 21.02.2023 Will be done next class
        return List.of(v1, v2, v3);
    }

}

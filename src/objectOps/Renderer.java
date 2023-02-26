package objectOps;

import object_data.Part;
import object_data.Scene;
import object_data.Solid;
import object_data.Vertex;
import raster_data.ZBuffer;
import rasterops.Liner;
import rasterops.Triangler;
import transforms.Mat4;

import java.util.List;
import java.util.stream.Collectors;

public class Renderer {

    private Liner liner;
    private ZBuffer zBuffer;
    private Triangler triangler;

    public Renderer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
        this.liner = new Liner(zBuffer);
        this.triangler = new Triangler(this.zBuffer);
    }

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
        for (Part part : solid.getParts()) {
            switch (part.getTopology()) {
                case LINE_LIST -> {
                    // for all lines
                    for (int i = part.getOffset(); i < part.getOffset() + part.getCount() * 2; i += 2) {
                        final Vertex v1 = vertices.get(indices.get(i));
                        final Vertex v2 = vertices.get(indices.get(i + 1));
                        if (!isOutOfViewSpace(List.of(v1, v2))) {
                            List<Vertex> clippedZ = clipZ(v1, v2);
                            liner.draw(clippedZ.get(0).dehomog().toViewPort(zBuffer.getColRaster().getWidth(), zBuffer.getColRaster().getHeight()), clippedZ.get(1).dehomog().toViewPort(zBuffer.getColRaster().getWidth(), zBuffer.getColRaster().getHeight()));
                        }
                    }
                }
                case TRIANGLE_FAN -> {
                    Vertex start = vertices.get(indices.get(part.getOffset()));
                    Vertex end = vertices.get(indices.get(part.getOffset() + 1));
                    int i = part.getOffset() + 2;
                    for (; i < part.getOffset() + part.getCount(); i++) {
                        Vertex current = vertices.get(indices.get(i));
                        if (!isOutOfViewSpace(List.of(start, current)) || !isOutOfViewSpace(List.of(current, end))) {
                            List<Vertex> clippedZ = clipZ(start, end, current);
                            triangler.draw(clippedZ.get(0).dehomog().toViewPort(zBuffer.getColRaster().getWidth(), zBuffer.getColRaster().getHeight()), clippedZ.get(1).dehomog().toViewPort(zBuffer.getColRaster().getWidth(), zBuffer.getColRaster().getHeight()), clippedZ.get(2).dehomog().toViewPort(zBuffer.getColRaster().getWidth(), zBuffer.getColRaster().getHeight()));
                            end = current;
                        }
                    }
                }
            }
        }
    }

    private boolean isOutOfViewSpace(List<Vertex> vertices) {
        final boolean allTooLeft = vertices.stream().allMatch(v -> v.getPosition().getX() < -v.getPosition().getW());
        final boolean allTooRight = vertices.stream().allMatch(v -> v.getPosition().getX() > v.getPosition().getW());
        final boolean allTooUp = vertices.stream().allMatch(v -> v.getPosition().getY() < -v.getPosition().getW());
        final boolean allTooDown = vertices.stream().allMatch(v -> v.getPosition().getY() > v.getPosition().getW());
        final boolean allTooClose = vertices.stream().allMatch(v -> v.getPosition().getZ() < 0);
        final boolean allTooFar = vertices.stream().allMatch(v -> v.getPosition().getZ() > v.getPosition().getW());
        return allTooLeft || allTooRight || allTooUp || allTooDown || allTooClose || allTooFar;
    }

    private List<Vertex> clipZ(Vertex v1, Vertex v2) {
        return List.of(v1, v2);
    }

    private List<Vertex> clipZ(Vertex v1, Vertex v2, Vertex v3) {
        return List.of(v1, v2, v3);
    }

}

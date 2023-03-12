package objectOps;

import linalg.Lerp;
import object_data.Part;
import object_data.Scene;
import object_data.Solid;
import object_data.Vertex;
import raster_data.ZBuffer;
import rasterops.Liner;
import rasterops.Triangler;
import transforms.Mat4;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Renderer {

    private Liner liner;
    private ZBuffer zBuffer;
    private Triangler triangler;
    private Lerp lerp = new Lerp();

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
                    final Vertex start = vertices.get(indices.get(part.getOffset()));
                    Vertex end = vertices.get(indices.get(part.getOffset() + 1));
                    for (int i = part.getOffset() + 2; i < part.getOffset() + part.getCount(); i++) {
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
        final Vertex min = v1.getPosition().getZ() < v2.getPosition().getZ() ? v1 : v2;
        final Vertex max = min == v1 ? v2 : v1;
        if (min.getPosition().getZ() < 0) {
            final double t = (0 - min.getPosition().getZ()) / (max.getPosition().getZ() - min.getPosition().getZ());
            final Vertex v = lerp.compute(min, max, t);
            return List.of(v, max);
        }
        return List.of(v1, v2);
    }

    private List<Vertex> clipZ(Vertex v1, Vertex v2, Vertex v3) {
        List<Vertex> sorted = Stream.of(v1, v2, v3).sorted(Comparator.comparingDouble(value -> value.getPosition().getZ())).toList();

        final double t1;
        final double t2;
        final Vertex v1R;
        final Vertex v2R;

        switch ((int) sorted.stream().filter(vertex -> vertex.getPosition().getZ() < 0).count()) {
            case 1 -> {
                Vertex out = sorted.get(0);
                t1 = -out.getPosition().getZ() / (sorted.get(1).getPosition().getZ() - out.getPosition().getZ());
                t2 = -out.getPosition().getZ() / (sorted.get(2).getPosition().getZ() - out.getPosition().getZ());
                v1R = lerp.compute(out, sorted.get(1), t1);
                v2R = lerp.compute(out, sorted.get(2), t2);
                return List.of(v1R, sorted.get(1), sorted.get(2), v1R, v2R, sorted.get(2));
            }
            case 2 -> {
                Vertex in = sorted.get(2);
                t1 = -sorted.get(0).getPosition().getZ() / (in.getPosition().getZ() - sorted.get(0).getPosition().getZ());
                t2 = -sorted.get(1).getPosition().getZ() / (in.getPosition().getZ() - sorted.get(1).getPosition().getZ());
                v1R = lerp.compute(sorted.get(0), in, t1);
                v2R = lerp.compute(sorted.get(1), in, t2);
                return List.of(v1R, v2R, sorted.get(2));
            }
        }

        return List.of(v1, v2, v3);
    }

}

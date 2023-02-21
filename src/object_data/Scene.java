package object_data;

import transforms.Mat4;

import java.util.List;

public class Scene {

    private List<Solid> solids;

    private List<Mat4> modelMats;

    public Scene() {

    }

    public List<Solid> getSolids() {
        return solids;
    }

    public List<Mat4> getModelMats() {
        return modelMats;
    }

    // TODO: 21.02.2023 metody pro dodelani solidu
}

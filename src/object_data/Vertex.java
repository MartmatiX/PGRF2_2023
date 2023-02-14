package object_data;

import linalg.Vectorizable;
import transforms.Col;
import transforms.Vec3D;

public class Vertex implements Vectorizable<Vertex> {

    private final Vec3D position;
    private final Col color;

    public Vertex(Vec3D position, Col color) {
        this.position = position;
        this.color = color;
    }

    public Vec3D getPosition() {
        return position;
    }

    public Col getColor() {
        return color;
    }

    @Override
    public Vertex mul(double t) {
        return new Vertex(position.mul(t), color.mul(t));
    }

    @Override
    public Vertex add(Vertex other) {
        return new Vertex(position.add(other.position), color.add(other.color));
    }
}

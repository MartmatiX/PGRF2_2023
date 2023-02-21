package object_data;

import linalg.Vectorizable;
import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

public class Vertex implements Vectorizable<Vertex>, Transformable<Vertex>{

    private final Point3D position;
    private final Col color;

    public Vertex(Point3D position, Col color) { // TODO: 21.02.2023 Change to Point3D -> fix all dependencies
        this.position = position;
        this.color = color;
    }

    public Point3D getPosition() {
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

    @Override
    public Vertex transformed(Mat4 transformation) {
        return this; // TODO: 21.02.2023 Finish this
    }

    @Override
    public Vertex dehomog() {
        return null; // TODO: 21.02.2023 Finish this
    }

    @Override
    public Vertex toViewPort(int width, int height) {
        return null; // TODO: 21.02.2023 Finish this, transformace z -1 do 1 do souradnic x, y
    }
}

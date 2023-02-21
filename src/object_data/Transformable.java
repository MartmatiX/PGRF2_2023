package object_data;

import transforms.Mat4;

public interface Transformable<V> {

    V transformed(Mat4 transformation);
    V dehomog();
    V toViewPort(int width, int height);

}

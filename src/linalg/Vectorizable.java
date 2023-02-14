package linalg;

public interface Vectorizable<V> {

    V mul(double t);

    V add(V other);

}

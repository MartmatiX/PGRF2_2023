package linalg;

public class Lerp {

    public <V extends Vectorizable<V>> V compute(V first, V second, double t) {
        return first.mul(1 - t).add(second.mul(t));
    }

}

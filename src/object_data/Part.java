package object_data;

public class Part {

    private final Topology topology;
    private final int offset;
    private final int count;

    public Part(Topology topology, int offset, int count) {
        this.topology = topology;
        this.offset = offset;
        this.count = count;
    }

    public Topology getTopology() {
        return topology;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }
}

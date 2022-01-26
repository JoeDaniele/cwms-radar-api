package cwms.radar.api.graph.pg.dto;

public class NamedPgGraphData {
    private final String name;
    private final PgGraphData graph;

    /**
     * Create a graph that has a name.
     * @param name name of the graph
     * @param graphData the actual graph
     */
    public NamedPgGraphData(String name, PgGraphData graphData) {
        this.name = name;
        this.graph = graphData;
    }

    public String getName() {
        return name;
    }

    public PgGraphData getGraph() {
        return graph;
    }
}

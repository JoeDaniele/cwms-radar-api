package cwms.radar.api.graph.pg.dto;

import cwms.radar.api.graph.pg.properties.PgProperties;

public class PgEdgeData {
    private final String from;
    private final String to;
    private final String[] labels;
    private final boolean undirected;
    private final PgProperties properties;

    /**
     * Create a edge between nodes.
     * @param from name of starting node
     * @param to name of ending node
     * @param labels labels for categorizing this edge
     * @param undirected is this a directional node (indicating flow or something like that.)
     * @param properties additional properties describing then particular edge.
     */
    public PgEdgeData(String from, String to, String[] labels,
                      boolean undirected, PgProperties properties) {
        this.from = from;
        this.to = to;
        this.labels = labels;
        this.undirected = undirected;
        this.properties = properties;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public boolean isUndirected() {
        return undirected;
    }

    public String[] getLabels() {
        return labels;
    }

    public PgProperties getProperties() {
        return properties;
    }

}

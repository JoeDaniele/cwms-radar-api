package cwms.radar.api.graph.pg.properties.basinconnectivity;

import cwms.radar.api.graph.pg.properties.PgProperties;

public class PgStreamNodeProperties implements PgProperties {
    private final String[] streamName;
    private final Double[] station;
    private final String[] bank;

    /**
     *  Create a steam node for the property graph.
     * @param streamName name of the stream.
     * @param station mile along the stream
     * @param bank left or right back
     */
    public PgStreamNodeProperties(String streamName, Double station, String bank) {
        this.streamName = new String[]{streamName};
        this.station = new Double[]{station};
        this.bank = new String[]{bank};
    }

    public String[] getStreamName() {
        return streamName;
    }

    public Double[] getStation() {
        return station;
    }

    public String[] getBank() {
        return bank;
    }
}

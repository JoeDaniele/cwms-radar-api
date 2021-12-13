package cwms.radar.formatters;

import cwms.radar.formatters.json.JsonV2;

import org.junit.jupiter.api.Test;


public class JsonV2Test extends TimeSeriesTestBase {

    @Override
    public OutputFormatter getOutputFormatter() {
        return new JsonV2();
    }

    @Test
    @Override
    public void singleTimeseriesFormat() {
        super.singleTimeseriesFormat();
    }
}

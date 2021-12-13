package cwms.radar.formatters.xml;

import cwms.radar.formatters.OutputFormatter;
import cwms.radar.formatters.TimeSeriesTestBase;
import cwms.radar.formatters.xml.XMLv2;

import org.junit.jupiter.api.Test;

public class XMLV2Test extends TimeSeriesTestBase {

    @Override
    public OutputFormatter getOutputFormatter() {
        return new XMLv2();
    }

    @Test
    @Override
    public void singleTimeseriesFormat() {
        super.singleTimeseriesFormat();
    }
}

package cwms.radar.data.dto;

import java.math.BigDecimal;

public class AssignedTimeSeries implements CwmsDTO {
    private String timeseriesId;
    private BigDecimal tsCode;

    private String aliasId;
    private String refTsId;
    private Integer attribute;

    /**
     * Timeseries assignment within a group.
     * @param timeseriesId name of the timeseries
     * @param tsCode internal identifier of the time series.
     * @param aliasId alias used
     * @param refTsId what Timeseries is this in reference
     * @param attr additional information, often used for sorting.
     */
    public AssignedTimeSeries(String timeseriesId, BigDecimal tsCode,
                              String aliasId, String refTsId, Integer attr) {
        this.timeseriesId = timeseriesId;
        // TODO: figure out how to rip tsCode out of this. That should be a hidden field.
        this.tsCode = tsCode;
        this.aliasId = aliasId;
        this.refTsId = refTsId;
        this.attribute = attr;
    }

    public String getTimeseriesId() {
        return timeseriesId;
    }

    public BigDecimal getTsCode() {
        return tsCode;
    }

    public String getAliasId() {
        return aliasId;
    }

    public String getRefTsId() {
        return refTsId;
    }

    public Integer getAttribute() {
        return attribute;
    }
}

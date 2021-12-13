package cwms.radar.formatters;

import cwms.radar.data.dto.Office;

import java.util.List;

public class OfficeFormatV1 {

    public class OfficesWrapper {
        public List<Office> offices;
    }


    public OfficesWrapper offices = new OfficesWrapper();

    public OfficeFormatV1(List<Office> offices) {
        this.offices.offices = offices;
    }
}

package cwms.radar.formatters;

import cwms.radar.data.dto.LocationCategory;
import java.util.List;

public class LocationCategoryFormatV1 {
    private final List<LocationCategory> locationCategories;

    public LocationCategoryFormatV1(List<LocationCategory> cats) {
        this.locationCategories = cats;
    }

    public List<LocationCategory> getLocationCategories() {
        return locationCategories;
    }
}

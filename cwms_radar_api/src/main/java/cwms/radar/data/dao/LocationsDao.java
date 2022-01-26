package cwms.radar.data.dao;

import cwms.radar.data.dto.Catalog;
import cwms.radar.data.dto.Location;

import java.io.IOException;
import java.util.Optional;

import org.geojson.FeatureCollection;

public interface LocationsDao {
    String getLocations(String names,String format, String units, String datum, String officeId);

    @SuppressWarnings("checkstyle:linelength")
    Location getLocation(String locationName, String unitSystem, String officeId) throws IOException;

    void deleteLocation(String locationName, String officeId) throws IOException;

    void storeLocation(Location location) throws IOException;

    void renameLocation(String oldLocationName, Location renamedLocation) throws IOException;

    FeatureCollection buildFeatureCollection(String names, String units, String officeId);

    Catalog getLocationCatalog(String cursor, int pageSize,
                               String unitSystem, Optional<String> office);

    Catalog getLocationCatalog(String cursor, int pageSize, String unitSystem,
                               Optional<String> office, String idLike,
                               String categoryLike, String groupLike);


}

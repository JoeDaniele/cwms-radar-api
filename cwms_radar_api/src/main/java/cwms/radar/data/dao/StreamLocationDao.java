package cwms.radar.data.dao;

import cwms.radar.api.enums.Unit;
import cwms.radar.api.enums.UnitSystem;
import cwms.radar.data.dto.basinconnectivity.StreamLocation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jooq.DSLContext;
import usace.cwms.db.jooq.dao.CwmsDbStreamJooq;

public class StreamLocationDao extends JooqDao<StreamLocation> {
    public StreamLocationDao(DSLContext dsl) {
        super(dsl);
    }

    /**
     * Get all of the CWMS Locations along a stream.
     * @param streamId  stream desired
     * @param unitSystem units to output any relevenat data (like elevations) in.
     * @param officeId owning office of the stream. NOTE: it is possible two different offices will
     *     refer to the same stream.
     * @return list of stream locations on stream
     */
    @SuppressWarnings("checkstyle:linelength")
    public Set<StreamLocation> getStreamLocations(String streamId, String unitSystem, String officeId) throws SQLException {
        String streamIdMaskIn = streamId == null ? "*" : streamId;
        String locationIdMaskIn = "*";
        String stationUnitIn = UnitSystem.EN.value().equalsIgnoreCase(unitSystem) ? Unit.MILE.getValue() : Unit.KILOMETER.getValue();
        String stageUnitIn = UnitSystem.EN.value().equalsIgnoreCase(unitSystem) ? Unit.FEET.getValue() : Unit.METER.getValue();
        String areaUnitIn = UnitSystem.EN.value().equalsIgnoreCase(unitSystem) ? Unit.SQUARE_MILES.getValue() : Unit.SQUARE_KILOMETERS.getValue();
        CwmsDbStreamJooq streamJooq = new CwmsDbStreamJooq();
        AtomicReference<ResultSet> resultSetRef = new AtomicReference<>();
        dsl.connection(c -> resultSetRef.set(streamJooq.catStreamLocations(c, streamIdMaskIn, locationIdMaskIn, stationUnitIn, stageUnitIn, areaUnitIn, officeId)));
        return buildStreamLocations(resultSetRef.get());
    }

    private Set<StreamLocation> buildStreamLocations(ResultSet rs) throws SQLException {
        Set<StreamLocation> retVal = new HashSet<>();
        while (rs.next()) {
            String locationId = rs.getString("LOCATION_ID");
            String officeId = rs.getString("OFFICE_ID");
            String streamId = rs.getString("STREAM_ID");
            Double station = toDouble(rs.getBigDecimal("STATION"));
            Double publishedStation = toDouble(rs.getBigDecimal("PUBLISHED_STATION"));
            Double navigationStation = toDouble(rs.getBigDecimal("NAVIGATION_STATION"));
            Double lowestMeasurableStage = toDouble(rs.getBigDecimal("LOWEST_MEASURABLE_STAGE"));
            Double totalDrainageArea = toDouble(rs.getBigDecimal("DRAINAGE_AREA"));
            Double ungagedDrainageArea = toDouble(rs.getBigDecimal("UNGAGED_DRAINAGE_AREA"));
            String bank = rs.getString("BANK");
            StreamLocation loc = new StreamLocation.Builder(locationId,
                                                            streamId,
                                                            station,
                                                            bank,
                                                            officeId)
                    .withPublishedStation(publishedStation)
                    .withNavigationStation(navigationStation)
                    .withLowestMeasurableStage(lowestMeasurableStage)
                    .withTotalDrainageArea(totalDrainageArea)
                    .withUngagedDrainageArea(ungagedDrainageArea)
                    .build();
            retVal.add(loc);
        }

        return retVal;
    }

    public Set<StreamLocation> getAllStreamLocations(String unitSystem,
                                                     String officeId) throws SQLException {
        return getStreamLocations(null, unitSystem, officeId);
    }

}

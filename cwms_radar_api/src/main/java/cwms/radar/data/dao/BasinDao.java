package cwms.radar.data.dao;

import cwms.radar.api.enums.Unit;
import cwms.radar.api.enums.UnitSystem;
import cwms.radar.data.dto.basinconnectivity.Basin;
import cwms.radar.data.dto.basinconnectivity.Stream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import usace.cwms.db.jooq.dao.CwmsDbBasinJooq;

public class BasinDao extends JooqDao<Basin> {

    public BasinDao(DSLContext dsl) {
        super(dsl);
    }

    /**
     * Retrieve all basins from the datbase.
     * @param unitSystem units for data like location elevations
     * @param officeId owning office of the Basin
     * @return the Basin object
     * @throws SQLException any errors with retrieving the data from the database.
     */
    public List<Basin> getAllBasins(String unitSystem, String officeId) throws SQLException {
        List<Basin> retval = new ArrayList<>();
        CwmsDbBasinJooq basinJooq = new CwmsDbBasinJooq();
        String areaUnitIn = UnitSystem.EN.value().equals(unitSystem)
            ? Unit.SQUARE_MILES.getValue() : Unit.SQUARE_KILOMETERS.getValue();
        try {
            dsl.connection(c -> {
                ResultSet rs = basinJooq.catBasins(c, null, null, null, areaUnitIn, officeId);
                retval.addAll(buildBasinsFromResultSet(rs, unitSystem));
            });
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
        return retval;
    }

    /**
     * Retreive basin data suitable for formatting.
     * @param basinId basin name
     * @param unitSystem units for data like location elevations
     * @param officeId owning office of the Basin
     * @return the Basin object
     * @throws SQLException any errors with retrieving the data from the database.
     */
    public Basin getBasin(String basinId, String unitSystem, String officeId) throws SQLException {
        CwmsDbBasinJooq basinJooq = new CwmsDbBasinJooq();

        String[] parentBasinId = new String[1];
        Double[] sortOrder = new Double[1];
        String[] primaryStreamId = new String[1];
        Double[] totalDrainageArea = new Double[1];
        Double[] contributingDrainageArea = new Double[1];
        String areaUnitIn = UnitSystem.EN.value().equals(unitSystem)
            ? Unit.SQUARE_MILES.getValue() : Unit.SQUARE_KILOMETERS.getValue();
        dsl.connection(c -> basinJooq.retrieveBasin(c, parentBasinId,
                                                    sortOrder,
                                                    primaryStreamId,
                                                    totalDrainageArea,
                                                    contributingDrainageArea,
                                                    basinId,
                                                    areaUnitIn,
                                                    officeId));
        Basin retval = new Basin.Builder(basinId, officeId)
                .withBasinArea(totalDrainageArea[0])
                .withContributingArea(contributingDrainageArea[0])
                .withParentBasinId(parentBasinId[0])
                .withSortOrder(sortOrder[0])
                .build();
        if (primaryStreamId[0] != null) {
            StreamDao streamDao = new StreamDao(dsl);
            Stream primaryStream = streamDao.getStream(primaryStreamId[0], unitSystem, officeId);
            retval = new Basin.Builder(retval).withPrimaryStream(primaryStream).build();
        }
        return retval;
    }

    @SuppressWarnings("checkstyle:linelength")
    private List<Basin> buildBasinsFromResultSet(ResultSet rs, String unitSystem) throws SQLException {
        List<Basin> retval = new ArrayList<>();
        while (rs.next()) {
            String officeId = rs.getString("OFFICE_ID");
            String basinId = rs.getString("BASIN_ID");
            String parentBasinId = rs.getString("PARENT_BASIN_ID");
            Double sortOrder = rs.getDouble("SORT_ORDER");
            String primaryStreamId = rs.getString("PRIMARY_STREAM_ID");
            Double basinArea = rs.getDouble("TOTAL_DRAINAGE_AREA");
            Double contributingArea = rs.getDouble("CONTRIBUTING_DRAINAGE_AREA");
            Basin basin = new Basin.Builder(basinId, officeId)
                    .withBasinArea(basinArea)
                    .withContributingArea(contributingArea)
                    .withParentBasinId(parentBasinId)
                    .withSortOrder(sortOrder)
                    .build();
            if (primaryStreamId != null) {
                StreamDao streamDao = new StreamDao(dsl);
                Stream primaryStream = streamDao.getStream(primaryStreamId, unitSystem, officeId);
                basin = new Basin.Builder(basin).withPrimaryStream(primaryStream).build();
            }
            retval.add(basin);
        }

        return retval;
    }

}

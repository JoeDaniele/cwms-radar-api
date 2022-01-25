package cwms.radar.data.dao;

import cwms.radar.api.enums.Unit;
import cwms.radar.data.dto.basinconnectivity.StreamReach;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jooq.DSLContext;
import usace.cwms.db.jooq.dao.CwmsDbStreamJooq;

public class StreamReachDao extends JooqDao<StreamReach> {
    public StreamReachDao(DSLContext dsl) {
        super(dsl);
    }

    /**
     * Get all of the reaches along a stream.
     * @param streamId stream desired
     * @param officeId owning office
     * @return list of stream reaches
     * @throws SQLException any database issue retrieving data
     */
    public Set<StreamReach> getReachesOnStream(String streamId,
                                               String officeId) throws SQLException {
        String stationUnitIn = Unit.KILOMETER.getValue();
        CwmsDbStreamJooq streamJooq = new CwmsDbStreamJooq();
        AtomicReference<ResultSet> resultSetRef = new AtomicReference<>();
        dsl.connection(
            c -> resultSetRef.set(streamJooq.catStreamReaches(c,
                                                              streamId, null, null,
                                                              null, stationUnitIn, officeId)));
        return buildReachesFromResultSet(resultSetRef.get());
    }

    private Set<StreamReach> buildReachesFromResultSet(ResultSet rs) throws SQLException {
        Set<StreamReach> retVal = new HashSet<>();

        while (rs.next()) {
            String reachId = rs.getString("REACH_LOCATION");
            if (!reachId.isEmpty()) {
                String streamId = rs.getString("STREAM_LOCATION");
                String officeId = rs.getString("OFFICE_ID");
                String upstreamLocationId = rs.getString("UPSTREAM_LOCATION");
                String downstreamLocationId = rs.getString("DOWNSTREAM_LOCATION");
                String configuration = rs.getString("CONFIGURATION");
                String comment = rs.getString("COMMENTS");
                StreamReach streamReach = new StreamReach.Builder(reachId, streamId,
                                                                  upstreamLocationId,
                                                                  downstreamLocationId,
                                                                  officeId)
                        .withComment(comment)
                        .withConfiguration(configuration)
                        .build();
                retVal.add(streamReach);
            }
        }

        return retVal;
    }

}

package cwms.radar.data.dao;

import cwms.radar.api.enums.Unit;
import cwms.radar.api.enums.UnitSystem;
import cwms.radar.data.dto.basinconnectivity.Stream;
import cwms.radar.data.dto.basinconnectivity.StreamLocation;
import cwms.radar.data.dto.basinconnectivity.StreamReach;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jooq.DSLContext;
import usace.cwms.db.dao.ifc.stream.StreamT;
import usace.cwms.db.jooq.dao.CwmsDbStreamJooq;


public class StreamDao extends JooqDao<Stream> {
    public StreamDao(DSLContext dsl) {
        super(dsl);
    }

    /**
     * Retrieve all stream information for a given stream.
     * @param streamId name of the stream desired
     * @param unitSystem what units to render data like elevation in.
     * @param officeId owning office
     * @return Stream object for further formatting
     * @throws SQLException any issues retrieving data.
     */
    @SuppressWarnings("checkstyle:linelength")
    public Stream getStream(String streamId, String unitSystem, String officeId) throws SQLException {
        String stationUnit = UnitSystem.EN.value().equals(unitSystem) ? Unit.MILE.getValue() : Unit.KILOMETER.getValue();
        CwmsDbStreamJooq streamJooq = new CwmsDbStreamJooq();
        AtomicReference<StreamT> streamResultRef = new AtomicReference<>();
        dsl.connection(c -> streamResultRef.set(streamJooq.retrieveStreamF(c, streamId, stationUnit, officeId)));
        StreamT streamResult = streamResultRef.get();
        return new Stream.Builder(streamId, streamResult.getStartsDownstream(), streamResult.getLength(), streamResult.getOfficeId())
                .withDivertingStreamId(streamResult.getDivertsFromStream())
                    .withDiversionStation(streamResult.getDivertsFromStation())
                    .withDiversionBank(streamResult.getDivertsFromBank())
                .withReceivingStreamId(streamResult.getFlowsIntoStream())
                    .withConfluenceStation(streamResult.getFlowsIntoStation())
                    .withConfluenceBank(streamResult.getFlowsIntoBank())
                .withComment(streamResult.getComments())
                .withAverageSlope(streamResult.getAverageSlope())
                .withStreamLocations(getStreamLocationsOnStream(streamId, unitSystem, officeId))
                .withTributaries(getTributaries(streamId, unitSystem, officeId))
                .withStreamReaches(getReaches(streamId, officeId))
                .build();
    }

    private Set<StreamLocation> getStreamLocationsOnStream(String streamId, String unitSystem,
                                                           String officeId) throws SQLException {
        StreamLocationDao streamLocationDao = new StreamLocationDao(dsl);
        return streamLocationDao.getStreamLocations(streamId, unitSystem, officeId);
    }

    private Set<StreamReach> getReaches(String streamId, String officeId) throws SQLException {
        StreamReachDao streamReachDao = new StreamReachDao(dsl);
        return streamReachDao.getReachesOnStream(streamId, officeId);
    }

    @SuppressWarnings("checkstyle:linelength")
    private Set<Stream> getTributaries(String streamId, String unitSystem,
                                       String officeId)  throws SQLException {
        CwmsDbStreamJooq streamJooq = new CwmsDbStreamJooq();
        Connection c = dsl.configuration().connectionProvider().acquire();
        String stationUnit = UnitSystem.EN.value().equals(unitSystem) ? Unit.MILE.getValue() : Unit.KILOMETER.getValue();
        ResultSet rs = streamJooq.catStreams(c, null, stationUnit, null, streamId, null, null, null, null, null, null, null, null, null, null, null, null, officeId);
        return buildStreamsFromResultSet(rs, streamId, unitSystem);
    }

    private Set<Stream> buildStreamsFromResultSet(ResultSet result, String parentStreamId,
                                                  String unitSystem) throws SQLException {
        Set<Stream> retVal = new HashSet<>();

        while (result.next()) {
            String officeId = result.getString("OFFICE_ID");
            String streamId = result.getString("STREAM_ID");
            String receivingStreamId = result.getString("FLOWS_INTO_STREAM");
            if (receivingStreamId != null && receivingStreamId.equals(parentStreamId)) {
                Double confluenceStation = null;
                Object confluenceObject = result.getObject("FLOWS_INTO_STATION");
                if (confluenceObject instanceof Double) {
                    confluenceStation = (Double) confluenceObject;
                }
                String confluenceBank = result.getString("FLOWS_INTO_BANK");
                String divertingStreamId = result.getString("DIVERTS_FROM_STREAM");
                Double diversionStation = null;
                Object diversionObject = result.getObject("DIVERTS_FROM_STATION");
                if (diversionObject instanceof Double) {
                    diversionStation = (Double) diversionObject;
                }
                String diversionBank = result.getString("DIVERTS_FROM_BANK");
                Double streamLength = toDouble(result.getBigDecimal("STREAM_LENGTH"));
                boolean startsDownstream = result.getBoolean("STATIONING_STARTS_DS");
                Double averageSlope = toDouble(result.getBigDecimal("AVERAGE_SLOPE"));
                String comment = result.getString("COMMENTS");
                Stream stream = new Stream.Builder(streamId, startsDownstream,
                                                   streamLength, officeId)
                        .withDivertingStreamId(divertingStreamId)
                        .withDiversionStation(diversionStation)
                        .withDiversionBank(diversionBank)
                        .withReceivingStreamId(receivingStreamId)
                        .withConfluenceStation(confluenceStation)
                        .withConfluenceBank(confluenceBank)
                        .withComment(comment)
                        .withAverageSlope(averageSlope)
                        .withStreamLocations(getStreamLocationsOnStream(streamId,
                                                                        unitSystem,
                                                                        officeId))
                        .withTributaries(getTributaries(streamId, unitSystem, officeId))
                        .withStreamReaches(getReaches(streamId, officeId))
                        .build();
                retVal.add(stream);
            }
        }

        return retVal;
    }
}

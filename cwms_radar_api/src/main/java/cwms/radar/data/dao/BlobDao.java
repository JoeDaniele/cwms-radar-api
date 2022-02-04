package cwms.radar.data.dao;

import cwms.radar.data.dto.Blob;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.ResultQuery;

public class BlobDao extends JooqDao<Blob> {
    public BlobDao(DSLContext dsl) {
        super(dsl);
    }

    // TODO: change to JOOQ query.
    /**
     * Retrieve a specific blob.
     * @param id blob desired
     * @param limitToOffice specific office. Must be specified if
     *     the same name exists for more than one office.
     */
    @SuppressWarnings("checkstyle:linelength")
    @Override
    public Optional<Blob> getByUniqueName(String id, Optional<String> limitToOffice) {
        String queryStr = "SELECT AT_BLOB.ID, AT_BLOB.DESCRIPTION, CWMS_MEDIA_TYPE.MEDIA_TYPE_ID, CWMS_OFFICE.OFFICE_ID, AT_BLOB.VALUE \n"
                + "FROM CWMS_20.AT_BLOB \n"
                + "join CWMS_20.CWMS_MEDIA_TYPE on AT_BLOB.MEDIA_TYPE_CODE = CWMS_MEDIA_TYPE.MEDIA_TYPE_CODE \n"
                + "join CWMS_20.CWMS_OFFICE on AT_BLOB.OFFICE_CODE=CWMS_OFFICE.OFFICE_CODE \n"
                + "WHERE ID = ?";
        ResultQuery<Record> query;
        if (limitToOffice.isPresent()) {
            queryStr = queryStr + " and CWMS_OFFICE.OFFICE_ID = ?";
            query = dsl.resultQuery(queryStr, id, limitToOffice.get());
        } else {
            query = dsl.resultQuery(queryStr, id);
        }

        Blob retval = query.fetchOne(r -> {
            String retrievedId = r.get("ID", String.class);
            String office = r.get("OFFICE_ID", String.class);
            String desc = r.get("DESCRIPTION", String.class);
            String media = r.get("MEDIA_TYPE_ID", String.class);
            byte[] value = r.get("VALUE", byte[].class);
            return new Blob(office, retrievedId, desc, media, value);
        });

        return Optional.ofNullable(retval);
    }


    /**
     * Get all blobs.
     * @param limitToOffice limit to a specific office if desired.
     * @return list of Blobs
     */
    @SuppressWarnings("checkstyle:linelength")
    @Override
    public List<Blob> getAll(Optional<String> limitToOffice) {
        String queryStr = "SELECT AT_BLOB.ID, AT_BLOB.DESCRIPTION, CWMS_MEDIA_TYPE.MEDIA_TYPE_ID, CWMS_OFFICE.OFFICE_ID\n"
                + " FROM CWMS_20.AT_BLOB \n"
                + "join CWMS_20.CWMS_MEDIA_TYPE on AT_BLOB.MEDIA_TYPE_CODE = CWMS_MEDIA_TYPE.MEDIA_TYPE_CODE \n"
                + "join CWMS_20.CWMS_OFFICE on AT_BLOB.OFFICE_CODE=CWMS_OFFICE.OFFICE_CODE \n"
                ;

        ResultQuery<Record> query;
        if (limitToOffice.isPresent()) {
            queryStr = queryStr + " and upper(CWMS_OFFICE.OFFICE_ID) = upper(?)";
            query = dsl.resultQuery(queryStr, limitToOffice.get());
        } else {
            query = dsl.resultQuery(queryStr);
        }

        return query.fetch(r -> {
            String id = r.get("ID", String.class);
            String office = r.get("OFFICE_ID", String.class);
            String desc = r.get("DESCRIPTION", String.class);
            String media = r.get("MEDIA_TYPE_ID", String.class);

            return new Blob(office, id, desc, media, null);
        });
    }

    // TODO: update to jooq query
    /**
     * Retrieve all blobs matching the given regular expression.
     * @param limitToOffice limit to a single office if desired.
     * @param like regular expression matched against the blob names available.
     * @return list of Blobs
     */
    @SuppressWarnings("checkstyle:linelength")
    public List<Blob> getAll(Optional<String> limitToOffice, String like) {
        String queryStr = "SELECT AT_BLOB.ID, AT_BLOB.DESCRIPTION, CWMS_MEDIA_TYPE.MEDIA_TYPE_ID, CWMS_OFFICE.OFFICE_ID\n"
                + " FROM CWMS_20.AT_BLOB \n"
                + "join CWMS_20.CWMS_MEDIA_TYPE on AT_BLOB.MEDIA_TYPE_CODE = CWMS_MEDIA_TYPE.MEDIA_TYPE_CODE \n"
                + "join CWMS_20.CWMS_OFFICE on AT_BLOB.OFFICE_CODE=CWMS_OFFICE.OFFICE_CODE \n"
                + " where REGEXP_LIKE (upper(AT_BLOB.ID), upper(?))"
                ;


        ResultQuery<Record> query;
        if (limitToOffice.isPresent()) {
            queryStr = queryStr + " and upper(CWMS_OFFICE.OFFICE_ID) = upper(?)";
            query = dsl.resultQuery(queryStr, like, limitToOffice.get());
        } else {
            query = dsl.resultQuery(queryStr, like);
        }

        return query.fetch(r -> {
            String id = r.get("ID", String.class);
            String office = r.get("OFFICE_ID", String.class);
            String desc = r.get("DESCRIPTION", String.class);
            String media = r.get("MEDIA_TYPE_ID", String.class);

            return new Blob(office, id, desc, media, null);
        });
    }

}

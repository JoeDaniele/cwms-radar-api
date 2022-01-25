package cwms.radar.data.dao;

import cwms.radar.data.dto.Office;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record4;

import usace.cwms.db.jooq.codegen.tables.AV_OFFICE;

public class OfficeDao extends JooqDao<Office> {
    public OfficeDao(DSLContext dsl) {
        super(dsl);
    }

    /**
     * List of all offices and FOAs.
     * @return list of office objects.
     */
    public List<Office> getOffices() {
        List<Office> retval;
        AV_OFFICE view = AV_OFFICE.AV_OFFICE;
        // The .as snippets lets it map directly into the Office ctor fields.
        retval = dsl.select(view.OFFICE_ID.as("name"),
                            view.LONG_NAME,
                            view.OFFICE_TYPE.as("type"),
                            view.REPORT_TO_OFFICE_ID.as("reportsTo"))
                    .from(view)
                    .fetch()
                    .into(Office.class);

        return retval;
    }

    /**
     * Retreive information for a specific office.
     * @param officeId initialism of the office desired
     * @return the Office object
     */
    public Optional<Office> getOfficeById(String officeId) {
        AV_OFFICE view = AV_OFFICE.AV_OFFICE;
        // The .as snippets lets it map directly into the Office ctor fields.
        Record4<String, String, String, String> fetchOne =
                dsl.select(view.OFFICE_ID.as("name"),
                           view.LONG_NAME,
                           view.OFFICE_TYPE.as("type"),
                           view.REPORT_TO_OFFICE_ID.as("reportsTo"))
                    .from(view)
                    .where(view.OFFICE_ID.eq(officeId))
                    .fetchOne();
        return fetchOne != null
                ? Optional.of(fetchOne.into(Office.class))
                : Optional.empty();
    }


}

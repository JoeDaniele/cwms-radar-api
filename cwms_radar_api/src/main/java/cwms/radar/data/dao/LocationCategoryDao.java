package cwms.radar.data.dao;

import cwms.radar.data.dto.LocationCategory;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record3;

import usace.cwms.db.jooq.codegen.tables.AV_LOC_CAT_GRP;

public class LocationCategoryDao extends JooqDao<LocationCategory> {

    public LocationCategoryDao(DSLContext dsl) {
        super(dsl);
    }

    /**
     * Get all location categories defined.
     * @return all location categories
     */
    public List<LocationCategory> getLocationCategories() {
        AV_LOC_CAT_GRP table = AV_LOC_CAT_GRP.AV_LOC_CAT_GRP;

        return dsl.selectDistinct(
                table.CAT_DB_OFFICE_ID,
                table.LOC_CATEGORY_ID,
                table.LOC_CATEGORY_DESC)
                .from(table)
                .fetch().into(LocationCategory.class);
    }

    /**
     * Get all location categories for a given office.
     * @param officeId desired office
     * @return all locations for the above office
     */
    public List<LocationCategory> getLocationCategories(String officeId) {
        if (officeId == null || officeId.isEmpty()) {
            return getLocationCategories();
        }
        AV_LOC_CAT_GRP table = AV_LOC_CAT_GRP.AV_LOC_CAT_GRP;

        return dsl.selectDistinct(table.CAT_DB_OFFICE_ID,
                table.LOC_CATEGORY_ID, table.LOC_CATEGORY_DESC)
                .from(table)
                .where(table.CAT_DB_OFFICE_ID.eq(officeId))
                .fetch().into(LocationCategory.class);
    }

    /**
     * Retrieve specific location category for an office.
     * @param officeId owning office
     * @param categoryId category name
     * @return the category and meta data or empty
     */
    public Optional<LocationCategory> getLocationCategory(String officeId, String categoryId) {
        AV_LOC_CAT_GRP table = AV_LOC_CAT_GRP.AV_LOC_CAT_GRP;

        Record3<String, String, String> fetchOne = dsl.selectDistinct(table.CAT_DB_OFFICE_ID,
                table.LOC_CATEGORY_ID, table.LOC_CATEGORY_DESC)
                .from(table)
                .where(table.CAT_DB_OFFICE_ID.eq(officeId)
                        .and(table.LOC_CATEGORY_ID.eq(categoryId)))
                .fetchOne();
        return fetchOne != null
            ? Optional.of(fetchOne.into(LocationCategory.class)) : Optional.empty();
    }
}

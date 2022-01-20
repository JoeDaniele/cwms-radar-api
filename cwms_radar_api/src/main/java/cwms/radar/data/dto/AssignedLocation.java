package cwms.radar.data.dto;

public class AssignedLocation implements CwmsDTO {
    private String locationId;
    private String officeId;
    private String aliasId;
    private Number attribute;

    private String refLocationId;

    /**
     * Information for a location assigned to a location group.
     * @param locationId name of the location
     * @param office owning office
     * @param aliasId alias of the location
     * @param attribute extra information, often used for sorting
     * @param refLocationId location this is related to.
     */
    public AssignedLocation(String locationId, String office, String aliasId,
                            Number attribute, String refLocationId) {
        this.locationId = locationId;
        this.officeId = office;
        this.aliasId = aliasId;
        this.attribute = attribute;
        this.refLocationId = refLocationId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getOfficeId() {
        return officeId;
    }

    public String getAliasId() {
        return aliasId;
    }

    public Number getAttribute() {
        return attribute;
    }

    public String getRefLocationId() {
        return refLocationId;
    }
}

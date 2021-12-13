package cwms.radar.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

//TODO: need to change Id fields to name.
@Schema(description = "A representation of a location group")
@XmlRootElement(name = "location_group")
@XmlAccessorType(XmlAccessType.FIELD)
public class LocationGroup implements CwmsDTO {
    private final String id;
    private final LocationCategory locationCategory;
    private final String officeId;
    private final String description;

    private String sharedLocAliasId;
    private String sharedRefLocationId;
    private Number locGroupAttribute;

    private List<AssignedLocation> assignedLocations = null;

    /**
     * LocationGroup providing all information.
     * @param catDbOfficeId owning office of the category
     * @param locCategoryId name of the ctegory
     * @param locCategoryDesc description of the category
     * @param grpDbOfficeId owning office of the group
     * @param locGroupId name of the group
     * @param locGroupDesc description of the group
     * @param sharedLocAliasId shared alias to the group
     * @param sharedRefLocationId //TODO: need to better define
     * @param locGroupAttribute arbitrary attribute, primarily used for sorting.
     */
    public LocationGroup(String catDbOfficeId, String locCategoryId, String locCategoryDesc,
        String grpDbOfficeId, String locGroupId, String locGroupDesc,
        String sharedLocAliasId, String sharedRefLocationId, Number locGroupAttribute) {
        this(new LocationCategory(catDbOfficeId, locCategoryId, locCategoryDesc),
                    grpDbOfficeId, locGroupId, locGroupDesc,
                    sharedLocAliasId, sharedRefLocationId, locGroupAttribute);
    }

    /**
     * Create a location from from a category and provided group information.
     * @param cat Location category definition
     * @param grpOfficeId owning office
     * @param grpId name of the group
     * @param grpDesc description of the group
     * @param sharedLocAliasId alias to reference all
     * @param sharedRefLocationId TODO: better define difference
     * @param locGroupAttribute additional information, primarily used for sorting.
     */
    public LocationGroup(LocationCategory cat, String grpOfficeId,
                         String grpId, String grpDesc,
                         String sharedLocAliasId, String sharedRefLocationId,
                         Number locGroupAttribute) {
        this.locationCategory = cat;
        this.officeId = grpOfficeId;
        this.id = grpId;
        this.description = grpDesc;
        this.sharedLocAliasId = sharedLocAliasId;
        this.sharedRefLocationId = sharedRefLocationId;
        this.locGroupAttribute = locGroupAttribute;
    }

    /**
     * Create a location group from existing group information and a set of locations.
     * @group Location group information
     * @locs list of locations
     */
    public LocationGroup(LocationGroup group, List<AssignedLocation> locs) {
        this(group);
        if (locs != null && !locs.isEmpty()) {
            this.assignedLocations = new ArrayList<>(locs);
        }
    }

    /**
     * create an empty location group from an existing location group.
     * @group existing location group.
     */
    public LocationGroup(LocationGroup group) {
        this.locationCategory = group.getLocationCategory();
        this.officeId = group.getOfficeId();
        this.id = group.getId();
        this.description = group.getDescription();
        this.sharedLocAliasId = group.getSharedLocAliasId();
        this.sharedRefLocationId = group.getSharedRefLocationId();
        this.locGroupAttribute = group.getLocGroupAttribute();
        List<AssignedLocation> locs = group.getAssignedLocations();
        if (locs != null && !locs.isEmpty()) {
            this.assignedLocations = new ArrayList<>(locs);
        }
    }

    public String getId() {
        return id;
    }

    public LocationCategory getLocationCategory() {
        return locationCategory;
    }

    public String getOfficeId() {
        return officeId;
    }

    public String getDescription() {
        return description;
    }

    public String getSharedLocAliasId() {
        return sharedLocAliasId;
    }

    public String getSharedRefLocationId() {
        return sharedRefLocationId;
    }

    public Number getLocGroupAttribute() {
        return locGroupAttribute;
    }

    public List<AssignedLocation> getAssignedLocations() {
        return assignedLocations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LocationGroup that = (LocationGroup) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getLocationCategory() != null ? !getLocationCategory().equals(
                that.getLocationCategory()) : that.getLocationCategory() != null) {
            return false;
        }
        if (getOfficeId() != null ? !getOfficeId().equals(
                that.getOfficeId()) : that.getOfficeId() != null) {
            return false;
        }
        if (getSharedLocAliasId() != null ? !getSharedLocAliasId().equals(
                that.getSharedLocAliasId()) : that.getSharedLocAliasId() != null) {
            return false;
        }
        if (getSharedRefLocationId() != null ? !getSharedRefLocationId().equals(
                that.getSharedRefLocationId()) : that.getSharedRefLocationId() != null) {
            return false;
        }
        if (getLocGroupAttribute() != null ? !getLocGroupAttribute().equals(
                that.getLocGroupAttribute()) : that.getLocGroupAttribute() != null) {
            return false;
        }
        return getAssignedLocations() != null ? getAssignedLocations().equals(
                that.getAssignedLocations()) : that.getAssignedLocations() == null;
    }

    @SuppressWarnings("checkstyle:linelength")
    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getLocationCategory() != null ? getLocationCategory().hashCode() : 0);
        result = 31 * result + (getOfficeId() != null ? getOfficeId().hashCode() : 0);
        result = 31 * result + (getSharedLocAliasId() != null ? getSharedLocAliasId().hashCode() : 0);
        result = 31 * result + (getSharedRefLocationId() != null ? getSharedRefLocationId().hashCode() : 0);
        result = 31 * result + (getLocGroupAttribute() != null ? getLocGroupAttribute().hashCode() : 0);
        result = 31 * result + (getAssignedLocations() != null ? getAssignedLocations().hashCode() : 0);
        return result;
    }
}

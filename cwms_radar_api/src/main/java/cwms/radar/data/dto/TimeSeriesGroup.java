package cwms.radar.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Schema(description = "A representation of a timeseries group")
@XmlRootElement(name = "timeseries-group")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeSeriesGroup implements CwmsDTO {
    private final String id;
    private final TimeSeriesCategory timeSeriesCategory;
    private final String officeId;
    private final String description;

    private String sharedAliasId;
    private String sharedRefTsId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AssignedTimeSeries> assignedTimeSeries = null;

    /**
     * Create a new TimeSeriesGroup providing all base information.
     * @param catDbOfficeId owning office of the category
     * @param tsCategoryId category name
     * @param tsCategoryDesc description of the category
     * @param grpDbOfficeId owning office of the group
     * @param tsGroupId group name
     * @param tsGroupDesc description of the group
     * @param sharedTsAliasId // TODO needs good explination
     * @param sharedRefTsId // TODO needs good explination
     */
    public TimeSeriesGroup(String catDbOfficeId, String tsCategoryId, String tsCategoryDesc,
                           String grpDbOfficeId, String tsGroupId, String tsGroupDesc,
                           String sharedTsAliasId, String sharedRefTsId) {
        this(new TimeSeriesCategory(catDbOfficeId, tsCategoryId, tsCategoryDesc),
                grpDbOfficeId, tsGroupId, tsGroupDesc,
                sharedTsAliasId, sharedRefTsId);
    }

    /**
     * Create a new group.
     * @param cat group gategory information
     * @param grpOfficeId owning office
     * @param grpId name of group
     * @param grpDesc description of the group
     * @param sharedTsAliasId // TODO needs good explination
     * @param sharedRefTsId // TODO needs good explination
     */
    public TimeSeriesGroup(TimeSeriesCategory cat, String grpOfficeId, String grpId, String grpDesc,
                           String sharedTsAliasId, String sharedRefTsId) {
        this.timeSeriesCategory = new TimeSeriesCategory(cat);
        this.officeId = grpOfficeId;
        this.id = grpId;
        this.description = grpDesc;
        this.sharedAliasId = sharedTsAliasId;
        this.sharedRefTsId = sharedRefTsId;

    }

    /**
     * copy an existing group.
     * @param group the group information to copy
     */
    public TimeSeriesGroup(TimeSeriesGroup group) {
        this.timeSeriesCategory = new TimeSeriesCategory(group.timeSeriesCategory);
        this.officeId = group.officeId;
        this.id = group.id;
        this.description = group.description;
        this.sharedAliasId = group.sharedAliasId;
        this.sharedRefTsId = group.sharedRefTsId;
    }

    /**
     * create a new group from existing group information.
     * @param group existing group information
     * @param timeSeries timeseries that are part of the group
     */
    public TimeSeriesGroup(TimeSeriesGroup group, List<AssignedTimeSeries> timeSeries) {
        this(group);
        if (timeSeries != null && !timeSeries.isEmpty()) {
            this.assignedTimeSeries = new ArrayList<>(timeSeries);
        }
    }

    public String getId() {
        return id;
    }

    public TimeSeriesCategory getTimeSeriesCategory() {
        return timeSeriesCategory;
    }


    public String getDescription() {
        return description;
    }

    public String getOfficeId() {
        return officeId;
    }

    public String getSharedAliasId() {
        return sharedAliasId;
    }

    public String getSharedRefTsId() {
        return sharedRefTsId;
    }

    public List<AssignedTimeSeries> getAssignedTimeSeries() {
        return assignedTimeSeries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TimeSeriesGroup that = (TimeSeriesGroup) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null)  {
            return false;
        }
        if (getTimeSeriesCategory() != null ? !getTimeSeriesCategory().equals(
                that.getTimeSeriesCategory()) : that.getTimeSeriesCategory() != null) {
            return false;
        }
        if (getOfficeId() != null
            ? !getOfficeId().equals(that.getOfficeId())
            : that.getOfficeId() != null) {
            return false;
        }
        if (getSharedAliasId() != null ? !getSharedAliasId().equals(
                that.getSharedAliasId()) : that.getSharedAliasId() != null) {
            return false;
        }
        if (getSharedRefTsId() != null ? !getSharedRefTsId().equals(
                that.getSharedRefTsId()) : that.getSharedRefTsId() != null) {
            return false;
        }
        return assignedTimeSeries != null ? assignedTimeSeries.equals(
                that.assignedTimeSeries) : that.assignedTimeSeries == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getTimeSeriesCategory() != null
                                ? getTimeSeriesCategory().hashCode()
                                : 0);
        result = 31 * result + (getOfficeId() != null ? getOfficeId().hashCode() : 0);
        result = 31 * result + (getSharedAliasId() != null ? getSharedAliasId().hashCode() : 0);
        result = 31 * result + (getSharedRefTsId() != null ? getSharedRefTsId().hashCode() : 0);
        result = 31 * result + (assignedTimeSeries != null ? assignedTimeSeries.hashCode() : 0);
        return result;
    }
}

package cwms.radar.data.dto.catalog;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.*;

public class LocationCatalogEntry extends CatalogEntry {
    private String name;
    private String nearestCity;
    private String publicName;
    private String longName;
    private String description;
    private String kind;
    private String type;
    private String timeZone;
    private Double latitude;
    private Double longitude;
    private Double publishedLatitude;
    private Double publishedLongitude;
    private String horizontalDatum;
    private Double elevation;
    private String unit;
    private String verticalDatum;
    private String nation;
    private String state;
    private String county;
    private String boundingOffice;
    private String mapLabel;
    private boolean active;
    @XmlElementWrapper(name = "aliases")
    @XmlElement(name = "alias")
    private List<LocationAlias> aliases;

    private LocationCatalogEntry() {
        super(null);
    }

    /**
     * Create a new entry for the catalog.
     * @param office The office that owns this location
     * @param name Location name (includes the CWMS Base and sub location)
     * @param nearestCity closest city to this location
     * @param publicName Friendly name to render in certain interfaces
     * @param longName A full name when neccessary. Often much more descriptive
     * @param description Description of the purpose or conditions
     * @param kind representation of purpose
     * @param type representation of purpose
     * @param timeZone timeZone in which the station is located. (used by Local Regular Timeseries.)
     * @param latitude location
     * @param longitude location
     * @param publishedLatitude location published
     * @param publishedLongitude location published
     * @param horizontalDatum reference information for the lat/long
     * @param elevation height above vertical datum reference
     * @param unit units of the elevation (usually ft or m)
     * @param verticalDatum reference information for the location elevation
     * @param nation What nation this location resides in (usually US, Canada, or Mexico)
     * @param state State/Province that this location is in
     * @param county the count the location is in
     * @param boundingOffice What USACE district has primary responsibility or need.
     * @param mapLabel name to use for map based interfaces
     * @param active is data actively collected for this site?
     * @param aliases additional names that can be used in all other interfaces
     */
    public LocationCatalogEntry(String office,
                                String name,
                                String nearestCity,
                                String publicName,
                                String longName,
                                String description,
                                String kind,
                                String type,
                                String timeZone,
                                Double latitude,
                                Double longitude,
                                Double publishedLatitude,
                                Double publishedLongitude,
                                String horizontalDatum,
                                Double elevation,
                                String unit,
                                String verticalDatum,
                                String nation,
                                String state,
                                String county,
                                String boundingOffice,
                                String mapLabel,
                                boolean active,
                                List<LocationAlias> aliases) {
        super(office);
        Objects.requireNonNull(aliases,
                              "aliases provided must be an actual list, empty list is okay");
        this.name = name;
        this.nearestCity = nearestCity;
        this.publicName = publicName;
        this.longName = longName;
        this.description = description;
        this.kind = kind;
        this.type = type;
        this.timeZone = timeZone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.publishedLatitude = publishedLatitude;
        this.publishedLongitude = publishedLongitude;
        this.horizontalDatum = horizontalDatum;
        this.elevation = elevation;
        this.unit = unit;
        this.verticalDatum = verticalDatum;
        this.nation = nation;
        this.state = state;
        this.county = county;
        this.boundingOffice = boundingOffice;
        this.mapLabel = mapLabel;
        this.active = active;
        this.aliases = aliases;
    }

    public String getPublicName() {
        return this.publicName;
    }

    public String getLongName() {
        return this.longName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getKind() {
        return this.kind;
    }

    public String getType() {
        return this.type;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getPublishedLatitude() {
        return this.publishedLatitude;
    }

    public Double getPublishedLongitude() {
        return this.publishedLongitude;
    }

    public String getHorizontalDatum() {
        return this.horizontalDatum;
    }

    public Double getElevation() {
        return this.elevation;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getVerticalDatum() {
        return this.verticalDatum;
    }

    public String getNation() {
        return this.nation;
    }

    public String getState() {
        return this.state;
    }

    public String getCounty() {
        return this.county;
    }

    public String getBoundingOffice() {
        return this.boundingOffice;
    }

    public String getMapLabel() {
        return this.mapLabel;
    }

    public boolean getActive() {
        return this.active;
    }

    public boolean isActive() {
        return this.active;
    }

    public List<LocationAlias> getAliases() {
        return this.aliases;
    }


    public String getName() {
        return name;
    }

    public String getNearestCity() {
        return nearestCity;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getOffice())
               .append("/")
               .append(name)
               .append(";nearestCity=")
               .append(nearestCity);
        for (LocationAlias alias: aliases) {
            builder.append(";alias=").append(alias.toString());
        }
        return builder.toString();
    }
}

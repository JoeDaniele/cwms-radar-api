package cwms.radar.data.dto;

import cwms.radar.data.dto.catalog.CatalogEntry;
import cwms.radar.data.dto.catalog.LocationCatalogEntry;
import cwms.radar.data.dto.catalog.TimeseriesCatalogEntry;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "catalog")
@XmlAccessorType(XmlAccessType.FIELD)
public class Catalog extends CwmsDTOPaginated {
    @Schema(
        oneOf = {
            LocationCatalogEntry.class,
            TimeseriesCatalogEntry.class
        }
    )
    @XmlElementWrapper(name = "entries")
    @XmlElement(name = "entry")
    private List<? extends CatalogEntry> entries;

    @SuppressWarnings("unused") // required so JAXB can initialize and marshal
    private Catalog(){

    }

    /**
     * Create a new catalog with the given information.
     * @param page what page of the catalog we're on (an opaque cursor)
     * @param total how many elements there are total
     * @param pageSize how many elements per return
     * @param entries the actual catalog data
     */
    public Catalog(String page, int total, int pageSize, List<? extends CatalogEntry> entries) {
        super(page, pageSize, total);

        Objects.requireNonNull(entries,
             "List of catalog entries must be a valid list, even if empty");
        this.entries = entries;
        if (entries.size() == pageSize) {
            nextPage = encodeCursor("|||",
                                    entries.get(entries.size() - 1).toString().toUpperCase(),total);
        } else {
            nextPage = null;
        }
    }

    /**
     * Get the elements of this catalog.
     * @return List<? extends CatalogEntry> return the entries
     */
    public List<? extends CatalogEntry> getEntries() {
        return entries;
    }

}

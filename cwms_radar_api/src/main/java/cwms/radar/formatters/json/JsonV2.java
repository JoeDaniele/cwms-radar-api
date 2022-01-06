package cwms.radar.formatters.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cwms.radar.data.dto.Blobs;
import cwms.radar.data.dto.Catalog;
import cwms.radar.data.dto.Clob;
import cwms.radar.data.dto.Clobs;
import cwms.radar.data.dto.CwmsDTO;
import cwms.radar.data.dto.Location;
import cwms.radar.data.dto.Office;
import cwms.radar.data.dto.Pool;
import cwms.radar.data.dto.Pools;
import cwms.radar.data.dto.TimeSeries;
import cwms.radar.formatters.Formats;
import cwms.radar.formatters.FormattingException;
import cwms.radar.formatters.OutputFormatter;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import service.annotations.FormatService;

@FormatService(contentType = Formats.JSONV2, dataTypes = {
    Office.class,
    Location.class,
    Catalog.class,
    TimeSeries.class,
    Clob.class,
    Clobs.class,
    Pool.class,
    Pools.class,
    Blobs.class
})
public class JsonV2 implements OutputFormatter {

    private final ObjectMapper om;

    public JsonV2() {
        this(new ObjectMapper());
    }

    public JsonV2(ObjectMapper om) {
        this.om = buildObjectMapper(om);
    }

    @NotNull
    public static ObjectMapper buildObjectMapper() {
        return buildObjectMapper(new ObjectMapper());
    }

    /**
     * Create an object mapper with appropriate defaults for this format.
     * @param om existing object mapper.
     * @return new object mapper with original settings plus new settings.
     */
    @NotNull
    public static ObjectMapper buildObjectMapper(ObjectMapper om) {
        ObjectMapper retval = om.copy();

        retval.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        retval.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        retval.registerModule(new JavaTimeModule());
        return retval;
    }

    @Override
    public String getContentType() {
        return Formats.JSONV2;
    }

    @Override
    public String format(CwmsDTO dto) {
        try {
            return om.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new FormattingException("Could not format :" + dto, e);
        }
    }

    @Override
    public String format(List<? extends CwmsDTO> dtoList) {
        try {
            return om.writeValueAsString(dtoList);
        } catch (JsonProcessingException e) {
            throw new FormattingException("Could not format :" + dtoList, e);
        }
    }

}

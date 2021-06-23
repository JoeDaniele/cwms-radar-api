package cwms.radar.data.dto;

import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;

/***
 * Provides a framework for a paginated result set.
 * Implementation details are subject to inheritors.
 * @author Daniel Osborne
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public abstract class CwmsDTOPaginated implements CwmsDTO {
    public static final String DTO_PAGE_DESCRIPTION = "The cursor to the current page of data";
    public static final String DTO_NEXTPAGE_DESCRIPTION = "The cursor to the next page of data; null if there is no more data";
    public static final String DTO_PAGESIZE_DESCRIPTION = "The number of records fetched per-page; this may be larger than the number of records actually retrieved";

    @Schema(description = DTO_PAGE_DESCRIPTION)
    protected String page;
    @Schema(description = DTO_NEXTPAGE_DESCRIPTION)
    protected String nextPage;
    @JsonInclude(value = Include.NON_NULL)
    @Schema(description = "The total number of records retrieved; null or not present if not supported or unknown")
    protected Integer total;
    @Schema(description = DTO_PAGESIZE_DESCRIPTION)
    protected int pageSize;

    static final Encoder encoder = Base64.getEncoder();
    static final Decoder decoder = Base64.getDecoder();
    static final String delimiter = "||";

    @SuppressWarnings("unused") // required so JAXB can initialize and marshal
    protected CwmsDTOPaginated() { }

    protected CwmsDTOPaginated(String encodedPage) {
        String[] decoded = decodeCursor(encodedPage);

        // Last item is pageSize
        this.pageSize = Integer.parseInt(decoded[decoded.length - 1]);

        // Build a new list and strip off the pageSize
        List<String> list = Arrays.asList(decoded);
        list.remove(list.size() - 1);

        // Build a new cursor/page marker
        this.page = encodeCursor(String.join(delimiter, list), pageSize);
    }

    protected CwmsDTOPaginated(String page, int pageSize) {
        this.pageSize = pageSize;
        this.page = encodeCursor(page, pageSize);
    }

    protected CwmsDTOPaginated(String page, int pageSize, Integer total) {
        this.pageSize = pageSize;
        this.total = total;
        this.page = encodeCursor(page, pageSize, total);
    }

    /**
     * @return String return the page
     */
    public String getPage() {
        return page;
    }

    /**
     * @return String return the nextPage
     */
    public String getNextPage() {
        return nextPage;
    }

    /**
     * @return Integer return the total, null if not supported or unknown
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * @return int return the elements per page
     */
    public int getPageSize() {
        return pageSize;
    }

    public static String[] decodeCursor(String cursor) {
        return decodeCursor(cursor, CwmsDTOPaginated.delimiter);
    }

    public static String[] decodeCursor(String cursor, String delimiter) {
        if(cursor != null && !cursor.isEmpty()) {
            String _cursor = new String(decoder.decode(cursor));
            String parts[] = _cursor.split(Pattern.quote(delimiter));
            return parts;
        }

        // Return empty array
        return new String[0];
    }

    public static String encodeCursor(String page, int pageSize) {
        return encodeCursor(CwmsDTOPaginated.delimiter, page, pageSize);
    }

    public static String encodeCursor(String page, int pageSize, Integer total) {
        return encodeCursor(CwmsDTOPaginated.delimiter, page, total, pageSize);     // pageSize should be last
    }

    public static String encodeCursor(Object ... parts) {
        return encodeCursor(CwmsDTOPaginated.delimiter, parts);
    }

    public static String encodeCursor(String delimiter, Object ... parts)
    {
        return (parts.length == 0 || parts[0] == null || parts[0].equals("*")) ? null :
            encoder.encodeToString(Arrays.stream(parts).map(o -> String.valueOf(o)).collect(Collectors.joining(delimiter)).getBytes());
    }
}

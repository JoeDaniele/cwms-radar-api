package cwms.radar.formatters;

import cwms.radar.data.dto.CwmsDTO;
import cwms.radar.helpers.ResourceHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Formats {
    public static final Logger logger = Logger.getLogger(Formats.class.getName());
    // Only used as a constant, not for any data mapping
    public static final String PLAIN = "text/plain";
    public static final String JSON = "application/json";
    public static final String XML = "application/xml";
    public static final String XMLV2 = "application/xml;version=2";
    public static final String WML2 = "application/vnd.opengis.waterml+xml";
    public static final String JSONV2 = "application/json;version=2";
    public static final String TAB = "text/tab-separated-values";
    public static final String CSV = "text/csv";
    public static final String GEOJSON = "application/geo+json";
    public static final String PGJSON = "application/vnd.pg+json";
    public static final String NAMED_PGJSON = "application/vnd.named+pg+json";


    private static List<ContentType> contentTypeList = new ArrayList<>();

    static {
        contentTypeList.addAll(
            Arrays.asList(JSON,XML, XMLV2, WML2,JSONV2,TAB,CSV, GEOJSON, PGJSON, NAMED_PGJSON)
            .stream().map(ct -> new ContentType(ct)).collect(Collectors.toList()));
    }

    private static Map<String,String> typeMap = new LinkedHashMap<>();

    static {
        typeMap.put("json",Formats.JSON);
        typeMap.put("xml",Formats.XML);
        typeMap.put("wml2",Formats.WML2);
        typeMap.put("tab",Formats.TAB);
        typeMap.put("csv",Formats.CSV);
        typeMap.put("geojson",Formats.GEOJSON);
        typeMap.put("pgjson", Formats.PGJSON);
        typeMap.put("named-pgjson", Formats.NAMED_PGJSON);
    }


    private Map<ContentType, Map<Class<CwmsDTO>,OutputFormatter>> formatters = null;

    private static Formats formats = null;

    private Formats() throws IOException {
        formatters = new LinkedHashMap<>();
        InputStream formatList = ResourceHelper
                                    .getResourceAsStream("/formats.list", this.getClass());
        BufferedReader br = new BufferedReader(new InputStreamReader(formatList));
        while (br.ready()) {
            String line = br.readLine();
            logger.finest(line);
            String[] typeFormatterClasses = line.split(":");

            ContentType type = new ContentType(typeFormatterClasses[0]);
            logger.finest("Adding links for content-type: " + type.toString());

            try {
                @SuppressWarnings("unchecked")
                Class<OutputFormatter> formatter =
                    (Class<OutputFormatter>) Class.forName(typeFormatterClasses[1]);
                OutputFormatter formatterInstance;
                logger.finest("Formatter class: " + typeFormatterClasses[1]);
                formatterInstance = formatter.getDeclaredConstructor().newInstance();
                Map<Class<CwmsDTO>,OutputFormatter> tmp = new HashMap<>();

                for (String clazz: typeFormatterClasses[2].split(";")) {
                    logger.finest("\tFor Class: " + clazz);

                    @SuppressWarnings("unchecked")
                    Class<CwmsDTO> formatForClass = (Class<CwmsDTO>)Class.forName(clazz);
                    tmp.put(formatForClass, formatterInstance);
                }

                formatters.put(type,tmp);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new IOException("Failed to load format list, formatter for "
                                     + typeFormatterClasses[0]
                                     + " point to a class with an invalid constructor",e);
            } catch (ClassNotFoundException e) {
                throw new IOException(
                    "Failed to find class referenced for formatter " + typeFormatterClasses[0],e);
            }
        }

    }

    private OutputFormatter getOutputFormatter(ContentType type, Class<? extends CwmsDTO> klass) {
        OutputFormatter outputFormatter = null;
        Map<Class<CwmsDTO>, OutputFormatter> contentFormatters = formatters.get(type);
        if (contentFormatters != null) {
            outputFormatter = contentFormatters.get(klass);
        }
        return outputFormatter;
    }

    private String getFormatted(ContentType type, CwmsDTO toFormat) throws FormattingException {
        Objects.requireNonNull(toFormat,"Object to be formatted should not be null");
        for (ContentType key: formatters.keySet()) {
            logger.fine(key.toString());
        }

        OutputFormatter outputFormatter = getOutputFormatter(type, toFormat.getClass());

        if (outputFormatter != null) {
            return outputFormatter.format(toFormat);
        } else {
            throw new FormattingException(
                "No Format for this content-type and data-type : (" + type.toString()
                + ", " + toFormat.getClass().getName() + ")");
        }

    }

    private String getFormatted(ContentType type,
                                List<? extends CwmsDTO> dtos,
                                Class<? extends CwmsDTO> rootType) throws FormattingException {
        for (ContentType key: formatters.keySet()) {
            logger.finest(key.toString());
        }

        Class<? extends CwmsDTO> klass = rootType; //dtos.get(0).getClass();
        OutputFormatter outputFormatter = getOutputFormatter(type, klass);

        if (outputFormatter != null) {
            return outputFormatter.format(dtos);
        } else {
            throw new FormattingException(
                        "No Format for this content-type and data type : (" + type.toString()
                         + ", " + dtos.get(0).getClass().getName() + ")");
        }
    }

    private static void init() {
        if (formats == null) {
            logger.finest("creating instance");
            try {
                formats = new Formats();
            } catch (IOException err) {
                throw new FormattingException("Failed to load format map", err);
            }
        }
    }

    /**
     * Actually formats data an appropriate.
     * @param type http content type requested
     * @param toFormat data set to format
     * @return the formatted data
     * @throws FormattingException If the content type isn't availble for the provided dataset
     */
    public static String format(ContentType type, CwmsDTO toFormat) throws FormattingException {
        logger.finest("formats");
        init();
        return formats.getFormatted(type,toFormat);
    }

    /**
     * Actually formats data an appropriate.
     * @param type http content type requested
     * @param toFormat data set to format
     * @param rootType class type that reprents the elements to be formatted. Used with content type
     * @return formatted data
     * @throws FormattingException for the content-type is not available for the rootType.
     */
    public static String format(ContentType type,
                                List<? extends CwmsDTO> toFormat,
                                Class<? extends CwmsDTO> rootType) throws FormattingException {
        logger.finest("format list");
        init();
        return formats.getFormatted(type,toFormat,rootType);
    }


    /**
     * Given the history of RADAR, this function allows the old way to mix with the new way.
     * @param header Accept header value
     * @param queryParam format query parameter value
     * @return an appropriate standard mimetype for lookup
     */
    public static ContentType parseHeaderAndQueryParm(String header, String queryParam) {
        if (queryParam != null && !queryParam.isEmpty()) {
            String val = typeMap.get(queryParam);
            if (val != null) {
                return new ContentType(val);
            } else {
                throw new FormattingException("content-type " + queryParam + " is not implemented");
            }
        } else if (header == null) {
            throw new FormattingException("no content type or format specified");
        } else {
            ContentType ct = parseHeader(header);
            if (ct != null) {
                return ct;
            }
        }
        throw new FormattingException("Content-Type " + header + " is not available");
    }

    /**
     * Find best match from user header.
     * @param header accept header that may included multiple types
     * @return the first Matching content-type
     */
    public static ContentType parseHeader(String header) {
        String[] all = header.split(",");
        ArrayList<ContentType> contentTypes = new ArrayList<>();
        logger.finest("Finding handlers " + all.length);
        for (String ct: all) {
            logger.finest(ct);
            contentTypes.add(new ContentType(ct));
        }
        Collections.sort(contentTypes);
        logger.finest("have " + contentTypes.size());
        for (ContentType ct: contentTypes) {
            logger.finest("checking " + ct.toString());
            if (contentTypeList.contains(ct)) {
                return ct;
            }
        }
        for (ContentType ct: contentTypes) {
            if (ct.getType().equals("*/*")) {
                return new ContentType(Formats.JSON);
            }
        }
        return null;
    }

}

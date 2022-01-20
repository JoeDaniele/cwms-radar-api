package cwms.radar.data.dto;

public class Blob implements CwmsDTO {
    private String office;
    private String id;
    private String description;
    private String mediaTypeId;
    private byte[] value;

    /**
     * Create a new Blob will all parameters.
     * @param office owning Office
     * @param id name of Blob
     * @param description what the blog is
     * @param type content-type or other string
     * @param value blob contents
     */
    public Blob(String office, String id, String description, String type, byte[] value) {
        this.office = office;
        this.id = id;
        this.description = description;
        this.mediaTypeId = type;
        this.value = value;
    }

    public String getOffice() {
        return office;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getValue() {
        return value;
    }

    public String getMediaTypeId() {
        return mediaTypeId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getOffice())
               .append("/")
               .append(id)
               .append(";description=")
               .append(description);
        return builder.toString();
    }
}

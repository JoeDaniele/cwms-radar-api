package cwms.radar.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import cwms.radar.api.errors.FieldException;

public class Blob implements CwmsDTO
{
	@JsonProperty(required=true)
	private String office;
	@JsonProperty(required=true)
	private String id;
	private String description;
	private String mediaTypeId;
	private byte[] value;

	public Blob(String office, String id, String description, String type, byte[] value)
	{
		this.office = office;
		this.id = id;
		this.description = description;
		this.mediaTypeId = type;
		this.value = value;
	}

	public String getOffice()
	{
		return office;
	}

	public String getId()
	{
		return id;
	}

	public String getDescription()
	{
		return description;
	}

	public byte[] getValue()
	{
		return value;
	}

	public String getMediaTypeId()
	{
		return mediaTypeId;
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(getOffice()).append("/").append(id).append(";description=").append(description);
		return builder.toString();
	}

	@Override
	public void validate() throws FieldException {
	}
}

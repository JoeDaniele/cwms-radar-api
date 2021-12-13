package cwms.radar.formatters.xml;

import cwms.radar.data.dto.Office;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "offices")
@XmlAccessorType (XmlAccessType.FIELD)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class XMLv1Office {
    @XmlElementWrapper(name = "offices")
    @XmlElement(name = "office")
    List<Office> offices;

    public XMLv1Office() {

    }

    public XMLv1Office(List<Office> offices) {
        this.offices = offices;
    }
}

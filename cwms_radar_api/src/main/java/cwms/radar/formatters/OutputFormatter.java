package cwms.radar.formatters;

import cwms.radar.data.dto.CwmsDTO;

import java.util.List;

public interface OutputFormatter {

    public String getContentType();

    public String format(CwmsDTO dto);

    public String format(List<? extends CwmsDTO> dtoList);
}

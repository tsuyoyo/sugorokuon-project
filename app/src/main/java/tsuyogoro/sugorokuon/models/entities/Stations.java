package tsuyogoro.sugorokuon.models.entities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

//<stations area_id="JP13" area_name="TOKYO JAPAN">
@Root
public class Stations {

    @ElementList(inline = true)
    public List<Station> stationList;

    @Attribute
    public String area_id;

    @Attribute
    public String area_name;

}

/**
 * Copyright (c)
 * 2018 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.radiko.api.response;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@Root(strict = false)
public class StationResponse {

    @ElementList(name = "stationList", inline = true)
    public List<Station> stationList;

    @Attribute(name = "area_id")
    public String areaId;

    @Attribute(name = "area_name")
    public String areaName;

    @Root(name = "station", strict = false)
    public static class Station {

        @Element(name = "id")
        public String id;

        @Element(name = "name")
        public String name;

        @Element(name = "ascii_name")
        public String ascii_name;

        @Element(name = "href")
        public String webSite;

        @Element(name = "areafree")
        public int areafree;

        @Element(name = "timefree")
        public int timefree;

        @ElementList(inline = true)
        public List<Logo> logos;

        @Root(name = "logo", strict = false)
        public static class Logo {
            @Attribute
            public int width;

            @Attribute
            public int height;

            @Text
            public String url;
        }

        @Element
        public String banner;
    }

}

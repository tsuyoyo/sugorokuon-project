package tsuyogoro.sugorokuon.v3.api.response;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Calendar;
import java.util.List;

@Root(name = "radiko", strict = false)
public class TimeTableResponse {

    @ElementList(name = "stations")
    public List<Station> stations;

    @Root(strict = false)
    public static class Station {
        @Attribute(name = "id")
        public String id;

        @Element(name = "name")
        public String name;

//        @ElementList(name = "progs", type = Program.class)
//        public List<Program> programs;

        @Element(name = "progs")
        public TimeTable timeTable;
    }

    @Root(strict = false)
    public static class TimeTable {

        @ElementList(inline = true)
        public List<Program> programs;
    }

    @Root(name = "prog", strict = false)
    public static class Program {
        // Formatted yyyyMMddHHmmss
        @Attribute(name = "ft", required = false)
        public Calendar start;

        @Attribute(name = "to", required = false)
        public Calendar end;

        @Element(name = "title", required = false)
        public String title;

        @Element(name = "url", required = false)
        public String url;

        @Element(name = "desc", required = false)
        public String description;

        @Element(name = "info", required = false)
        public String info;

        @Element(name = "pfm", required = false)
        public String perfonality;

        @Element(name = "img", required = false)
        public String image;

        @ElementList(name = "metas", required = false)
        public List<Meta> metaList;
    }

    @Root(name = "meta", strict = false)
    public static class Meta {
        @Attribute
        public String name;

        @Attribute
        public String value;
    }
}

package tsuyogoro.sugorokuon.v3.api.response;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Calendar;
import java.util.List;

@Root(name = "feed", strict = false)
public class FeedResponse {

    @ElementList(name = "noa", required = false)
    public List<Song> songs;

    @Root(strict = false)
    static public class Song {
        @Attribute(name = "title", required = false)
        public String title;

        @Attribute(name = "artist", required = false)
        public String artist;

        @Attribute(name = "stamp", required = false)
        public Calendar stamp;

        @Attribute(name = "img", required = false)
        public String image;

        @Attribute(name = "img_large", required = false)
        public String imageLarge;

        @Attribute(name = "program_title", required = false)
        public String programTitle;

        @Attribute(name = "amazon", required = false)
        public String amazon;

        @Attribute(name = "itunes", required = false)
        public String iTunes;

        @Attribute(name = "recochoku", required = false)
        public String recochoku;
    }

}
/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tsuyogoro.sugorokuon.constants.SugorokuonConst;
import tsuyogoro.sugorokuon.models.entities.Feed;
import tsuyogoro.sugorokuon.models.entities.OnAirSong;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

class FeedResponseParser extends ResponseParserBase {

    private static class TagName {
        static String STATION = "station";
        static String ITEMS = "items";
        static String ITEM = "item";
        static String CM = "cm";
        static String BANNER = "banner";
        static String EXTRA = "extra";
    }

    private static class AttributeName {
        static String ID = "id";
        static String TYPE = "type";
        static String STAMP = "stamp";
        static String ARTIST = "artist";
        static String TITLE = "title";
        static String ITEMID = "itemid";
    }

    /**
     * Constructor.
     *
     * @param source        Data source.
     * @param inputEncoding Basically "UTF-8".
     */
    public FeedResponseParser(InputStream source, String inputEncoding) {
        super(source, inputEncoding);
    }

    /**
     * FeedのxmlデータをparseしてFeedインスタンスを作る。
     *
     * @return 失敗したらnullが返る。
     */
    public Feed parse() {
        /* <feed>
		 * <station id="xxx">
		 * ...
		 * </station>
		 * </feed>
		 */
        List<OnAirSong> onAirSongs = null;

        if (null == getParser()) {
            Log.e(SugorokuonConst.LOGTAG, "FeedParser constructor has been failed.");
            throw new IllegalStateException();
        }
        try {
            for (int e = getParser().getEventType();
                 e != XmlPullParser.END_DOCUMENT; e = getParser().next()) {
                if (XmlPullParser.START_TAG == e) {
                    String tagName = getParser().getName();
                    if (tagName.equals(TagName.STATION)) {
                        String stationId = getParser().getAttributeValue(null, AttributeName.ID);
                        onAirSongs = parseStation(stationId);
                    }
                }
            }
        } catch (XmlPullParserException e) {
            Log.e(SugorokuonConst.LOGTAG,
                    "Failed parsing : XmlPullParserException : " + e.getMessage());
        } catch (IOException e) {
            Log.e(SugorokuonConst.LOGTAG,
                    "Failed parsing : IOException : " + e.getMessage());
        }

        if (null != onAirSongs) {
            return new Feed(onAirSongs);
        } else {
            return null;
        }
    }

    /*
     * <station id="FMT">
     *   <items>...</items>
     *   <cm>...</cm>
     *   <banner>...</banner>
     *   <extra>...</extra>
     * </station>
     */
    private List<OnAirSong> parseStation(String stationId)
            throws XmlPullParserException, IOException {

        // <station>の次へ進める
        getParser().next();

        List<OnAirSong> items = null;

        for (int e = getParser().getEventType();
             e != XmlPullParser.END_TAG; e = getParser().next()) {
            if (XmlPullParser.START_TAG == e) {
                String tagName = getParser().getName();

                // OnAir曲情報をparse。
                if (tagName.equals(TagName.ITEMS)) {
                    items = parseItems(stationId);
                } else if (tagName.equals(TagName.CM)) {
                    parseCM();
                } else if (tagName.equals(TagName.BANNER)) {
                    parseBanner();
                } else if (tagName.equals(TagName.EXTRA)) {
                    // 何もしなくてよいはず。
                }
            }
        }

        return items;
    }

    /*
     * <items>
     *   <item artist="bent Fabric"
     *   	   evid=""
     *   	   href=""
     *   	   img_210x170="http://radiko.jp/v2/static/feed-icon/210x170/music.png"
     *   	   itemid="50f3b79fbbb11f52"
     *   	   stamp="2013-01-14T16:44:32"
     *   	   title="Shake"
     *         type="music"/>
     *  </item>
     */
    private List<OnAirSong> parseItems(String stationId)
            throws XmlPullParserException, IOException {
        // <items>の次へ進める
        getParser().next();

        List<OnAirSong> items = new ArrayList<OnAirSong>();

        for (int e = getParser().getEventType(); e != XmlPullParser.END_TAG;
             e = getParser().next()) {

            if (XmlPullParser.START_TAG == e) {

                String tagName = getParser().getName();
                if (tagName.equals(TagName.ITEM)) {
                    // musicのtypeが、最近のonAir曲のitem。
                    if (getParser().getAttributeValue(null, AttributeName.TYPE)
                            .equals("music")) {
                        String artist = getParser().getAttributeValue(null, AttributeName.ARTIST);
                        String title = getParser().getAttributeValue(null, AttributeName.TITLE);
                        String itemId = getParser().getAttributeValue(null, AttributeName.ITEMID);
                        String stamp = getParser().getAttributeValue(null, AttributeName.STAMP);
                        Calendar date = parseStamp(stamp);

                        if (null != date) {
                            items.add(new OnAirSong(stationId,
                                    AlphabetNormalizer.zenkakuToHankaku(artist),
                                    AlphabetNormalizer.zenkakuToHankaku(title), date, itemId));
                        }
                    }
                    getParser().next();
                }
            }
        }

        return items;
    }

    /*
     * "stamp="2013-01-14T16:44:32""
     * このフォーマットをparseする。
     */
    private Calendar parseStamp(String stamp) {

        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss");

        Date d = null;
        try {
            d = formatter.parse(stamp);
        } catch (ParseException e) {
            SugorokuonLog.e("parseStamp failed : " + e.getMessage());
        }

        Calendar c = null;
        if (null != d) {
            c = Calendar.getInstance();
            c.setTime(d);
            c.set(Calendar.MILLISECOND, 0);
        }

        return c;
    }

    /*
     * <cm>
     *   <item .../>
     *   <item .../>
       *   <item .../>
       * </cm>
     */
    private void parseCM() throws XmlPullParserException, IOException {
        for (int e = getParser().getEventType();
             e != XmlPullParser.END_TAG; e = getParser().next()) {
            // ひたすら読み飛ばす。
        }
    }


    /*
     * <banner>
     *   <item .../>
     * </banner>
     */
    private void parseBanner() throws XmlPullParserException, IOException {
        for (int e = getParser().getEventType();
             e != XmlPullParser.END_TAG; e = getParser().next()) {
            // ひたすら読み飛ばす。
        }
    }

}

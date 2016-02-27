/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.entities;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tsuyogoro.sugorokuon.models.apis.ProgramApi;
import tsuyogoro.sugorokuon.models.apis.StationApi;

/**
 * 1番組分のデータ。
 *
 */
public class Program {

    public final String stationId;
    public final Calendar startTime;
    public final Calendar endTime;
    public final String title;
    public final String subtitle;
    public final String personalities;
    public final String description;
    public final String info;
    public final String url;

    public List<String> recommendKeyword;

    private boolean mIsRecommend;

    private Program(String stationId,
                    Calendar startTime,
                    Calendar endTime,
                    String title,
                    String subtitle,
                    String personalities,
                    String desc,
                    String info,
                    String url) {
        this.stationId = stationId;
        this.title = title;
        this.subtitle = subtitle;
        this.personalities = personalities;
        this.description = desc;
        this.info = info;
        this.url = url;
        this.startTime = startTime;
        this.endTime = endTime;

        this.mIsRecommend = false;

        this.recommendKeyword = new ArrayList<String>();
    }

    public static class Builder {
        private Program mInstance;

        public String stationId;

        public Calendar startTime;
        public Calendar endTime;
        public String title;
        public String subtitle;
        public String personalities;
        public String description;
        public String info;
        public String url;

        public Builder() {
        }

        public Program create() {
            mInstance = new Program(stationId, startTime, endTime, title,
                    subtitle, personalities, description, info, url);
            return mInstance;
        }

    }

    public void setRecommend(boolean isRecommend) {
        mIsRecommend = isRecommend;
    }

    public boolean recommend() {
        return mIsRecommend;
    }

    /**
     * TimeTableなどのリストで使う、番組の画像ファイルを取得。
     * キャッシュにあればそこから、無ければインターネットから取ってくる。
     * 通信を行う事があるため、このメソッドはUIスレッドから呼んではダメ (Exceptionが起きる)
     *
     * @param context
     * @return 存在しなければnullを返す
     */
    public Bitmap getSymbolIcon(Context context) {
        List<String> imgUrls = findSymbolIcon();

        // 番組のdescription or infoに記載された画像で、一番最初に出てきたものを取得する
        // (これで良いかどうかは別途考える)
        Bitmap icon = null;

        if (!imgUrls.isEmpty()) {
            String url = imgUrls.get(0);

            ProgramApi programApi = new ProgramApi(context);

            icon = programApi.loadCachedIcon(url);
            if (null == icon) {
                icon = programApi.cacheIcon(url);
            }
        }

        // 番組のアイコンが無ければ、局のアイコンを代わりに使う
        if (null == icon) {
            StationApi stationApi = new StationApi(context);
            icon = stationApi.load(stationId).loadLogo(context);
        }

        return icon;
    }

    /**
     * TimeTableなどのリストで使う、番組の画像ファイルパス (URL) を取得。
     *
     * @return 存在しなければnullを返す
     */
    public String getSymbolIconPath(Context context) {
        List<String> imgUrls = findSymbolIcon();

        // 番組のdescription or infoに記載された画像で、一番最初に出てきたものを取得する
        // (これで良いかどうかは別途考える)
        if (!imgUrls.isEmpty()) {
            return imgUrls.get(0);
        } else {
            // 番組のアイコンが無ければ、局のアイコンを代わりに使う
            StationApi stationApi = new StationApi(context);
            return stationApi.load(stationId).getLogoCachePath();
        }
    }

    private List<String> findSymbolIcon() {
        List<String> imgUrls = new ArrayList<String>();

        String programDesc = (null != description) ? description : "";
        programDesc += (null != info) ? info : "";

        // メモ :
        // "|" (or) を使って2つ条件を書くと後者が上手く検出されないのでやむを得ず2つに分けた
        Matcher m1 = Pattern.compile("<img src=\'([^\'\\n]*)\'").matcher(programDesc);
        while (m1.find()) {
            imgUrls.add(m1.group(1));
        }
        Matcher m2 = Pattern.compile("<img src=\"([^\"\\n]*)\"").matcher(programDesc);
        while (m2.find()) {
            imgUrls.add(m2.group(1));
        }

        return imgUrls;
    }

}

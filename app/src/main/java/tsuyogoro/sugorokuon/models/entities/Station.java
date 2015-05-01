/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tsuyogoro.sugorokuon.utils.FileHandleUtil;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class Station {

    public final String id;

    public final String name;

    public final String ascii_name;

    public final String siteUrl;

    public final String logoUrl;

    public final String bannerUrl;

    private String mLogoCachePath;

    private boolean mIsOnAirSongsAvailable = false;

    private Station(String id, String name, String ascii_name,
                    String siteUrl, String logoUrl, String bannerUrl, String logoCachePath) {
        this.id = id;
        this.name = name;
        this.ascii_name = ascii_name;
        this.siteUrl = siteUrl;
        this.logoUrl = logoUrl;
        this.bannerUrl = bannerUrl;
        this.mLogoCachePath = logoCachePath;
    }

    public static class Builder {
        public String id;
        public String name;
        public String ascii_name;
        public String siteUrl;
        public String logoUrl;
        public String bannerUrl;
        public String logoCache;
        public String logoCachePath;

        public Station create() {
            return new Station(id, name, ascii_name, siteUrl,
                    logoUrl, bannerUrl, logoCachePath);
        }
    }

    public void setOnAirSongsAvailable(boolean isAvailable) {
        mIsOnAirSongsAvailable = isAvailable;
    }

    /**
     * Logoファイルをダウンロードした先のパスをセット
     * これをセットした後は、{@link Station#loadLogo(android.content.Context)} で
     * ファイルのloadを行えるようになる
     *
     * @param path
     */
    public void setLogoCachePath(String path) {
        mLogoCachePath = path;
    }

    public String getLogoCachePath() {
        return mLogoCachePath;
    }

    /**
     * Feedの中にtype=musicの情報が含まれているかどうか
     * (on air曲の情報)
     *
     * @return
     */
    public boolean isOnAirSongsAvailable() {
        return mIsOnAirSongsAvailable;
    }

    /**
     * {@link Station#setLogoCachePath(String)} でsetしたパスから
     * Logoファイルをから読み込む (cacheをメモリに持っている場合はそれを返す)
     *
     * @param context
     * @return ファイルがなかったらnull
     */
    public Bitmap loadLogo(Context context) {
        Bitmap logo = sCache.get(id);

        if (null == logo) {
            logo = readAndCache(this, context);
        }

        return logo;
    }

    /**
     * logoのCacheを取り除く (ファイルを消す)
     *
     */
    public void abandonLogoCache() {
        sCache.remove(id);
        if (null != mLogoCachePath && 0 < mLogoCachePath.length()) {
            FileHandleUtil.delete(mLogoCachePath);
        }
    }

    private static Map<String, Bitmap> sCache = new HashMap<String, Bitmap>();

    private static Bitmap readAndCache(Station station, Context context) {

        File logoFile = new File(station.mLogoCachePath);
        Bitmap logo = null;

        if (logoFile.exists()) {
            try {
                FileInputStream is = new FileInputStream(logoFile);

                logo = BitmapFactory.decodeStream(is);
                sCache.put(station.id, logo);

                is.close();
            } catch (IOException e) {
                SugorokuonLog.e("IOException happens at closing IO : " + e.getMessage());
            }
        } else {
            SugorokuonLog.w("Logo file (" + station.mLogoCachePath + ") does not exist");
        }

        return logo;
    }

}

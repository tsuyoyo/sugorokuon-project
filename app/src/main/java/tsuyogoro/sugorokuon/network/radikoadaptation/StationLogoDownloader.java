/**
 * Copyright (c)
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.utils.FileHandleUtil;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

class StationLogoDownloader {

    // Logoファイルのcacheを置くディレクトリのパス。
    // 隠しフォルダに作成して、media scannerに引っ掛からないようにする。
    private static final String LOGO_CACHE_DIR =
            "radiconcierge" + File.separator + ".stationlogo" + File.separator;

    private StationLogoDownloader() {
        // インスタンス作らない
    }

    /**
     * ラジオ局のlogoイメージをdownloadし、filePathを返却する
     *
     * @param station
     * @return 失敗した場合はnull
     */
    public static String download(Station station) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(station.logoUrl).build();

        InputStream is;
        String path = null;

        try {
            Response response = client.newCall(request).execute();

            if (response.code() <= 400) {
                is = response.body().byteStream();
                path = FileHandleUtil.saveDataToFile(is, cacheDirectory(),
                        logoFileName(station.id));
                is.close();
            }
        } catch (IOException e) {
            SugorokuonLog.e("Fail to get (Logo url = " + station.logoUrl +
                    ") : "  + e.getMessage());
        }

        return path;
    }

    private static String logoFileName(String stationId) {
        Calendar now = Calendar.getInstance();

        return stationId + "_" + now.getTimeInMillis() + ".png";
    }

    private static String cacheDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + LOGO_CACHE_DIR;
    }

}

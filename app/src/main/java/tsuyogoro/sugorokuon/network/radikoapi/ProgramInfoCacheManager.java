/**
 * Copyright (c)
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

import android.os.Environment;

/**
 * 番組のinfo(htmlのフォーマットでサーバから落ちてくる番組情報）のキャッシュの管理。
 * 番組のinfoはDBには登録せず、SDにhtmlとして保存していく。
 *
 * @author Tsuyoyo
 *
 */
class ProgramInfoCacheManager {

    private static final String CACHE_DIR = "radiconcierge" +
            File.separator + "infocache" + File.separator;

    /**
     * 指定したProgramの、infoのcacheファイル（html）を生成し、pathを返却
     *
     * @param p
     * @return 失敗したら空文字が返る。
     */
    public String createInfoCache(Program p) {
        String fullPath = null;
        try {
            // フォルダの有無を確認して、無かったら作る。
            File dir = new File(getCacheDirectory());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = createFileName(p.stationId,
                    Long.toString(p.startTime.getTimeInMillis()));
            fullPath = getCacheDirectory() + fileName;

            File cacheFile = new File(fullPath);
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(cacheFile);
            fos.write(changeInfoToHtmldata(p.info).getBytes());
            fos.close();
        } catch (IOException e) {
            SugorokuonLog.w("createInfoCache : " + e.getMessage());
            fullPath = "";
        }

        return fullPath;
    }

    private String createFileName(String stationId, String startTime) {
        return stationId + "_" + startTime + ".html";
    }

    private String getCacheDirectory() {
        File ext = Environment.getExternalStorageDirectory();
        return ext.getAbsolutePath() + File.separator + CACHE_DIR;
    }

    private String changeInfoToHtmldata(String info) {
        return "<html><head><meta http-equiv=\"content-type\""
                + "content=\"text/html;charset=UTF-8\"></head>"
                + info + "</html>";
    }

    /**
     * InfoのCacheデータを全て削除。
     *
     */
    public void clearInfoCache() {
        File dir = new File(getCacheDirectory());
        if (dir.exists()) {
            deleteFiles(dir);
        }
    }

    private void deleteFiles(File dir) {
        if (dir.isDirectory()) {
            String[] files = dir.list();
            for (String f : files) {
                deleteFiles(new File(f));
            }
            dir.delete();
        } else if (dir.isFile()) {
            dir.delete();
        }
        return;
    }


    /**
     * infoのキャッシュデータを、stationIdとstartTimeをキーにして読み込む。
     *
     * @param stationId
     * @param startTime CalendarクラスのgetTimeInMillisの値
     * @return
     */
    public String readInfoCache(String stationId, String startTime) {
        String fullPath = getCacheDirectory() + createFileName(stationId, startTime);
        return readInfoCache(fullPath);
    }

    /**
     * infoのキャッシュデータを、ファイルパス指定で読み込む。
     *
     * @param cacheFileName
     * @return
     */
    public String readInfoCache(String cacheFileName) {
        String data = "";
        File cacheFile = new File(cacheFileName);
        try {
            FileReader reader = new FileReader(cacheFile);
            BufferedReader b = new BufferedReader(reader);
            String s;
            while ((s = b.readLine()) != null) {
                data += s;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            data = "";
            SugorokuonLog.w("readInfoCache : " + e.getMessage());
        } catch (IOException e) {
            data = "";
            SugorokuonLog.w("readInfoCache : " + e.getMessage());
        }

        return data;
    }

}

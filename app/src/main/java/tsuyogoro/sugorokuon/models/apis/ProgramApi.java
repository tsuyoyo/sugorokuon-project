/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

import tsuyogoro.sugorokuon.utils.FileHandleUtil;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class ProgramApi {

    // Logoファイルのcacheを置くディレクトリのパス。
    // 隠しフォルダに作成して、media scannerに引っ掛からないようにする。
    private static final String LOGO_CACHE_DIR =
            "radiconcierge" + File.separator + ".program_icons" + File.separator;

    private ProgramDbOpenHelper mOpenHelper;

    public ProgramApi(Context context) {
        mOpenHelper = new ProgramDbOpenHelper(context);
    }

    private static String cacheDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + LOGO_CACHE_DIR;
    }

    /**
     * Cacheをクリアする
     *
     */
    public void clearCachedIcons() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.delete(ProgramIconCacheTableDefiner.TABLE_NAME, null, null);

        // キャッシュファイルをすべて消す
        FileHandleUtil.removeAllFileInFolder(cacheDirectory());
    }

    /**
     * 番組のiconのキャッシュを読み込む。
     *
     * @param originalUrl
     * @return cacheがなかったらnull
     */
    public Bitmap loadCachedIcon(String originalUrl) {
        SQLiteDatabase readableDb = mOpenHelper.getReadableDatabase();

        Cursor c;

        String where = ProgramIconCacheTableDefiner.TableColumns.ORIGINAL_URL.columnName() + "= ? ";
        String[] whereArgs = new String[] { originalUrl };

        c = readableDb.query(ProgramIconCacheTableDefiner.TABLE_NAME,
                new String[] {
                        ProgramIconCacheTableDefiner.TableColumns.CACHE_PATH.columnName()
                }, where, whereArgs, null, null, null);

        Bitmap icon = null;

        if (0 < c.getCount()) {
            c.moveToFirst();

            String iconPath = c.getString(c.getColumnIndex(
                    ProgramIconCacheTableDefiner.TableColumns.CACHE_PATH.columnName()));

            File iconFile = new File(iconPath);

            if (iconFile.exists()) {
                try {
                    FileInputStream is = new FileInputStream(iconFile);
                    icon = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    SugorokuonLog.e("IOException happens at closing IO : " + e.getMessage());
                }
            }
        }

        c.close();
        readableDb.close();

        return icon;
    }

    /**
     * 指定したURLからファイルをDownloadしてきてキャッシュを登録
     *
     * @param originalUrl
     * @return DLに失敗した場合はnull
     */
    public Bitmap cacheIcon(String originalUrl) {

        Bitmap icon = null;

        try {
            HttpGet httpGet = new HttpGet(originalUrl);

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 3000);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpResponse res = httpClient.execute(httpGet);

            // ファイルをDLして、新しいキャッシュをストレージに作成
            if (res.getStatusLine().getStatusCode() < 400) {
                String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                        res.getEntity().getContentType().getValue());

                String fileName = Long.toString(Calendar.getInstance().getTimeInMillis())
                        + "." + extension;

                String cachedFilePath = FileHandleUtil.saveDataToFile(
                        res.getEntity().getContent(), cacheDirectory(), fileName);

                // キャッシュからBitmapを生成
                FileInputStream is = new FileInputStream(cachedFilePath);
                icon = BitmapFactory.decodeStream(is);
                is.close();

                // DBにfilePathとoriginalUrlの組み合わせを書き込む
                insert(originalUrl, cachedFilePath);
            } else {
                SugorokuonLog.w("Failed to download program icon - status : "
                        + res.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            SugorokuonLog.e("Failed to fetch program icon : "
                    + originalUrl + " - " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            SugorokuonLog.e("Something happened");
        }

        return icon;
    }

    private long insert(String originalUrl, String cachedFilePath) {
        ContentValues cv = new ContentValues();
        cv.put(ProgramIconCacheTableDefiner.TableColumns.ORIGINAL_URL.columnName(),
                originalUrl);
        cv.put(ProgramIconCacheTableDefiner.TableColumns.CACHE_PATH.columnName(),
                cachedFilePath);

        SQLiteDatabase writableDb = mOpenHelper.getWritableDatabase();
        long newRowId = writableDb.insert(ProgramIconCacheTableDefiner.TABLE_NAME, null, cv);
        writableDb.close();

        return newRowId;
    }

}

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS).build();

            Request request = new Request.Builder().url(originalUrl).build();

            Response response = client.newCall(request).execute();

            // ファイルをDLして、新しいキャッシュをストレージに作成
            if (response.code() == 200) {
                MediaType mediaType = response.body().contentType();

                String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                        mediaType.type() + "/" + mediaType.subtype());

                String fileName = Long.toString(Calendar.getInstance().getTimeInMillis())
                        + "." + extension;

                String cachedFilePath = FileHandleUtil.saveDataToFile(
                        response.body().byteStream(), cacheDirectory(), fileName);

                // キャッシュからBitmapを生成
                FileInputStream is = new FileInputStream(cachedFilePath);
                icon = BitmapFactory.decodeStream(is);
                is.close();

                // DBにfilePathとoriginalUrlの組み合わせを書き込む
                insert(originalUrl, cachedFilePath);
            } else {
                SugorokuonLog.w("Failed to download program icon - status : " + response.code());
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

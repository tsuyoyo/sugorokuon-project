/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHandleUtil {

    /**
     * 指定したパスに、InputStreamから取れるデータを書き込む。
     * 書き込みが成功した場合、ファイルのfullパスが返る。失敗したら空文字。
     *
     * @param data
     * @param dirName /sdcard/tmp/ ←最後の/までつけること。
     * @param fileName
     * @return
     */
    public static String saveDataToFile(InputStream data,
                                        String dirName, String fileName) {
        String fullPath;
        try {
            // フォルダの有無を確認して、無かったら作る。
            File dir = new File(dirName);
            if(!dir.exists()) {
                dir.mkdirs();
            }

            // 空のファイルを作る
            fullPath = dirName + fileName;
            File dataFile = new File(fullPath);
            if(!dataFile.exists()) {
                dataFile.createNewFile();
            }

            // データを流し込む
            writeDataToFile(dataFile, data);

        } catch(IOException e) {
            SugorokuonLog.e("Fail to create : " + e.getMessage());
            fullPath = "";
        }

        return fullPath;
    }

    private static void writeDataToFile(File dest, InputStream is) {
        byte[] buffer = new byte[1024];
        int length = 0;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dest);
            while ((length = is.read(buffer)) >= 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            fos = null;
        } catch(Exception e) {
            SugorokuonLog.e(e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch(IOException e) {
                    SugorokuonLog.e(e.getMessage());
                }
            }
        }
    }

    public static void delete(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.delete()) {
            SugorokuonLog.d("Delete file is OK : " + filePath);
        } else {
            SugorokuonLog.w("Failed to delete file : " + filePath);
        }
    }

    /**
     *
     *
     * @param dirName
     */
    public static void removeAllFileInFolder(String dirName) {
        File[] objFiles = (new File(dirName)).listFiles();
        if (null != objFiles) {
            for(int i=0; i< objFiles.length; i++ ) {
                objFiles[i].delete();
            }
        }
    }

}
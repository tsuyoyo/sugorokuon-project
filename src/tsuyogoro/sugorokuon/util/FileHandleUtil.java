/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import android.util.Log;

public class FileHandleUtil {

	// TODO : フォルダの中身を全部消すメソッドが欲しい。
	
	
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
			Log.e(SugorokuonConst.LOGTAG, "Fail to create : " + e.getMessage());
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
	    	Log.e(SugorokuonConst.LOGTAG, e.getMessage());
	    } finally {
	        if (fos != null) {  
	            try {  
	            	fos.close();  
	            } catch(IOException e) {
	            	Log.e(SugorokuonConst.LOGTAG, e.getMessage());
	            }
	        }
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

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

	// TODO : �t�H���_�̒��g��S���������\�b�h���~�����B
	
	
	/**
	 * �w�肵���p�X�ɁAInputStream�������f�[�^���������ށB
	 * �������݂����������ꍇ�A�t�@�C����full�p�X���Ԃ�B���s������󕶎��B
	 * 
	 * @param data
	 * @param dirName /sdcard/tmp/ ���Ō��/�܂ł��邱�ƁB
	 * @param fileName
	 * @return
	 */
	public static String saveDataToFile(InputStream data, 
			String dirName, String fileName) {
    	String fullPath;
		try {
			// �t�H���_�̗L�����m�F���āA������������B
	    	File dir = new File(dirName);
	    	if(!dir.exists()) {
	    		dir.mkdirs();
	    	}
	    	
	    	// ��̃t�@�C�������
			fullPath = dirName + fileName;
	    	File dataFile = new File(fullPath);
	    	if(!dataFile.exists()) {
	    		dataFile.createNewFile();
	    	}
	    	
	    	// �f�[�^�𗬂�����
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

/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Program;
import android.os.Environment;
import android.util.Log;

/**
 * �ԑg��info(html�̃t�H�[�}�b�g�ŃT�[�o���痎���Ă���ԑg���j�̃L���b�V���̊Ǘ��B
 * �ԑg��info��DB�ɂ͓o�^�����ASD��html�Ƃ��ĕۑ����Ă����B
 * 
 * @author Tsuyoyo
 *
 */
class ProgramInfoCacheManager {
	
	private static final String CACHE_DIR = "radiconcierge" + 
		File.separator + "infocache" + File.separator;
	
	/**
	 * �w�肵��Program�́Ainfo��cache�t�@�C���ihtml�j�𐶐����Apath��ԋp
	 * 
	 * @param p
	 * @return ���s������󕶎����Ԃ�B
	 */
	public String createInfoCache(Program p) {
    	String fullPath;
		try {
			// �t�H���_�̗L�����m�F���āA������������B
	    	File dir = new File(getCacheDirectory());
	    	if(!dir.exists()) {
	    		dir.mkdirs();
	    	}
	    	
	    	// �t�@�C�������
			String fileName = createFileName(p.stationId, p.start);
			fullPath = getCacheDirectory() + fileName;
	    	File cacheFile = new File(fullPath);
	    	if(!cacheFile.exists()) {
	    		cacheFile.createNewFile();
	    	}
	    	FileOutputStream fos = new FileOutputStream(cacheFile);
	        fos.write(changeInfoToHtmldata(p.info).getBytes());
	        fos.close();
		} catch(IOException e) {
			Log.e(SugorokuonConst.LOGTAG, "createInfoCache : " + e.getMessage());
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
	 * Info��Cache�f�[�^��S�č폜�B
	 * 
	 */
	public void clearInfoCache() {
		// �t�H���_�̗L�����m�F���āA������������B
    	File dir = new File(getCacheDirectory());
    	if(dir.exists()) {
    		deleteFiles(dir);
    	}
	}
	
    private void deleteFiles(File dir) {
    	if(dir.isDirectory()) {
    		String[] files = dir.list();
    		for(String f : files) {
    			deleteFiles(new File(f));
    		}
    		dir.delete();
    	} else if(dir.isFile()){
    		dir.delete();
    	}
    	return;
    }
    

    /**
     * info�̃L���b�V���f�[�^���AstationId��startTime���L�[�ɂ��ēǂݍ��ށB
     * 
     * @param stationId
     * @param startTime
     * @return
     */
    public String readInfoCache(String stationId, String startTime) {
    	String fullPath = getCacheDirectory() + createFileName(stationId, startTime);
    	return readInfoCache(fullPath);
    }
    
    /**
     * info�̃L���b�V���f�[�^���A�t�@�C���p�X�w��œǂݍ��ށB
     * 
     * @param cacheFileName
     * @return
     */
    public String readInfoCache(String cacheFileName) {
    	String data = "";
    	File cacheFile = new File(cacheFileName);
        try{
        	FileReader reader = new FileReader(cacheFile);
            BufferedReader b = new BufferedReader(reader);
            String s;
            while((s = b.readLine())!=null){
            	data += s;
            }
            reader.close();
        }catch(FileNotFoundException e){
        	data = "";
        	Log.e(SugorokuonConst.LOGTAG, "readInfoCache : " + e.getMessage());
        }catch(IOException e){
        	data = "";
        	Log.e(SugorokuonConst.LOGTAG, "readInfoCache : " + e.getMessage());
        }
        
    	return data;
    }
    
}

/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.util.List;

import tsuyogoro.sugorokuon.datatype.Program;

/**
 * �ԑg����Parse���鎞�ɁA�I�X�X���ԑg���ǂ�����filter��������N���X�B
 *
 */
public class RecommendProgramFilter {
	private List<String> mKeyWords;
	
	/**
	 * Constructor.
	 * 
	 */
	public RecommendProgramFilter(List<String> keyWords) {
		mKeyWords = keyWords;
	}
	
	/**
	 * Recommend�̔ԑg���ǂ����𔻒�B 
	 * Recommend�������ꍇ�Ap�ɂ̓I�X�X���L�[���[�h�����܂���B
	 * 
	 * @param target ���̔ԑg���
	 */
	public boolean isRecommend(Program target) {
		return checkRecommendAndSaveKeyword(target);
	}
	
	private boolean checkRecommendAndSaveKeyword(Program target) {
		boolean res = false;
		for(String keyword : mKeyWords) {
			if(isKeywordInclueded(target.title, keyword) || 
			   isKeywordInclueded(target.personalities, keyword) ||
			   isKeywordInclueded(target.description, keyword) ||
			   isKeywordInclueded(target.info, keyword)) {
				// �Ђ����������L�[���[�h��target�C���X�^���X�ɂ��܂��B
				res = true;
				target.recommendKeyword.add(keyword);
			}
		}
		return res;
	}
	
	// keyword�ɑ΂��āA�S�p�Ɣ��p�A�����Ō�����������
	// (AKB48�Ɠ���āA���ۂ̃T�C�g�ɂ͑S�p��AKB48�Ə����Ă��邱�Ƃ�����̂Łj
	private boolean isKeywordInclueded(String target, String keyword) {
		if(target.matches(".*" + keyword + ".*")) {
			return true;
		}
		if(target.matches(".*" + hankakuToZenkaku(keyword) + ".*")) {
			return true;
		}
		return false;
	}
	
	// ���p��keyword��S�p�ɂ���B
	private static String hankakuToZenkaku(String value) {
	    StringBuilder sb = new StringBuilder(value);
	    for (int i = 0; i < sb.length(); i++) {
	        int c = (int) sb.charAt(i);
	        if ((c >= 0x30 && c <= 0x39) || (c >= 0x41 && c <= 0x5A) || (c >= 0x61 && c <= 0x7A)) {
	            sb.setCharAt(i, (char) (c + 0xFEE0));
	        }
	    }
	    value = sb.toString();
	    return value;
	}
}

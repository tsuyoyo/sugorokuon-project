/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

import java.util.List;

import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.model.ProgramDatabaseAccessor;
import tsuyogoro.sugorokuon.settings.preference.RecommendWordPreference;
import android.content.Context;

/**
 * ProgramDatabaseAccessor��DB�֔ԑg�f�[�^�����܂��ۂɁA
 * ���̔ԑg���I�X�X�����ǂ����𒲂ׂ邽�߂Ɏg����N���X�B
 * 
 * @author Tsuyoyo
 *
 */
class ProgramRecommender implements ProgramDatabaseAccessor.IRecommender {
	
	private Context mContext;

	public ProgramRecommender(Context context) {
		mContext = context;
	}
	
	/**
	 * Recommend�̔ԑg���ǂ����𔻒�B 
	 * Recommend�������ꍇ�Ap�ɂ̓I�X�X���L�[���[�h�����܂���B
	 * 
	 * @param target ���̔ԑg���
	 */
	public boolean isRecommend(Program target) {
		List<String> recommendKeyWords = RecommendWordPreference.getKeyWord(mContext);
		return checkRecommendAndSaveKeyword(target, recommendKeyWords);
	}
	
	private boolean checkRecommendAndSaveKeyword(Program target, 
			List<String> recommendKeyWords) {
		boolean res = false;
		for(String keyword : recommendKeyWords) {
			if(isKeywordInclueded(target.title, keyword) || 
			   isKeywordInclueded(target.personalities, keyword) ||
			   isKeywordInclueded(target.description, keyword) ||
			   isKeywordInclueded(target.info, keyword)) {
				res = true;
				
				// �Ђ����������L�[���[�h��target�C���X�^���X�ɂ��܂��B
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

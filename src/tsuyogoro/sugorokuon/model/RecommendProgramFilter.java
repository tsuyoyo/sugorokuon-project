/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.util.List;

import tsuyogoro.sugorokuon.datatype.Program;

/**
 * 番組情報をParseする時に、オススメ番組かどうかでfilterをかけるクラス。
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
	 * Recommendの番組かどうかを判定。 
	 * Recommendだった場合、pにはオススメキーワードがしまわれる。
	 * 
	 * @param target 候補の番組情報
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
				// ひっかかったキーワードはtargetインスタンスにしまう。
				res = true;
				target.recommendKeyword.add(keyword);
			}
		}
		return res;
	}
	
	// keywordに対して、全角と半角、両方で検索をかける
	// (AKB48と入れて、実際のサイトには全角でAKB48と書いてあることもあるので）
	private boolean isKeywordInclueded(String target, String keyword) {
		if(target.matches(".*" + keyword + ".*")) {
			return true;
		}
		if(target.matches(".*" + hankakuToZenkaku(keyword) + ".*")) {
			return true;
		}
		return false;
	}
	
	// 半角のkeywordを全角にする。
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

/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

public interface IViewFlowListener {
		
	/**
	 * RecommendProgramInfoViewFlow�����Event����M����interface.
	 * 
	 * @param event
	 */
	public abstract void onViewFlowEvent(ViewFlowEvent event);
	
	
	/**
	 * ������Progress���󂯎��B
	 * 
	 * @param whatsRunning �eViewFlow�N���X�ɂĒ�`����B
	 * @param progress
	 * @param max
	 */
	public abstract void onProgress(int whatsRunning, int progress, int max);
}

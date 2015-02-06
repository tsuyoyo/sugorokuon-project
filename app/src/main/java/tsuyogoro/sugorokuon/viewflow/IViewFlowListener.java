/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

public interface IViewFlowListener {

    /**
     * RecommendProgramInfoViewFlowからのEventを受信するinterface.
     *
     * @param event
     */
    public abstract void onViewFlowEvent(ViewFlowEvent event);


    /**
     * 処理のProgressを受け取る。
     *
     * @param whatsRunning 各ViewFlowクラスにて定義する。
     * @param progress
     * @param max
     */
    public abstract void onProgress(int whatsRunning, int progress, int max);
}
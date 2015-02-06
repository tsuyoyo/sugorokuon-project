/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

import java.util.ArrayList;
import java.util.List;

abstract class ViewFlowBase {

    // ここに登録されているListenerに対して通知を行う
    private List<IViewFlowListener> mListeners;

    protected ViewFlowBase() {
        mListeners = new ArrayList<IViewFlowListener>();
    }

    public void register(IViewFlowListener listener) {
        if(!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void unregister(IViewFlowListener listener) {
        mListeners.remove(listener);
    }

    protected List<IViewFlowListener> getListeners() {
        return mListeners;
    }

    protected void notifyEvent(ViewFlowEvent event) {
        // onViewFlowEventの中でunregisterされても大丈夫なように、copyを作って通知。
        List<IViewFlowListener> listeners = mListeners;
        for(IViewFlowListener listener : listeners) {
            listener.onViewFlowEvent(event);
        }
    }

    protected void notifyProgress(int whatsRunning, int progress, int max) {
        // onProgressの中でunregisterされても大丈夫なように、copyを作って通知。
        List<IViewFlowListener> listeners = mListeners;
        for(IViewFlowListener listener : listeners) {
            listener.onProgress(whatsRunning, progress, max);
        }
    }

}
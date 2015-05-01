/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.gtm;

import android.content.Context;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;

import java.util.concurrent.TimeUnit;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class ContainerHolderLoader {

    static private final int TIME_OUT_SEC = 2;

    public interface OnLoadListener {
        /**
         * これが呼ばれた後、
         * ContainerHolderSingleton#getContentHolderが使えるようになる
         */
        public void onContainerHolderAvailable();
    }

    /**
     * ContainerHolderをloadし、onLoadListenerへcallbackする
     * callbackはmainスレッドに返ってくる
     *
     * @param context
     * @param onLoadListener Must not be null
     */
    static public void load(Context context, final OnLoadListener onLoadListener) {

        TagManager tagManager = TagManager.getInstance(context.getApplicationContext());
        tagManager.setVerboseLoggingEnabled(true);

        PendingResult<ContainerHolder> pending =
                tagManager.loadContainerPreferNonDefault(context.getString(R.string.gtm_container_id),
                        R.raw.gtm_default_container);

        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {

                ContainerHolderSingleton.setContainerHolder(containerHolder);

                Container container = containerHolder.getContainer();

                if (!containerHolder.getStatus().isSuccess()) {
                    SugorokuonLog.e("failure loading container");
                    return;
                }

                ContainerHolderSingleton.setContainerHolder(containerHolder);

                // Sets a listener that will be called when a new container becomes available だそう
                containerHolder.setContainerAvailableListener(new ContainerHolder.ContainerAvailableListener() {
                    @Override
                    public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
                        ContainerHolderSingleton.setContainerHolder(containerHolder);
                        SugorokuonLog.d("New container is available : " + containerVersion);
                    }
                });

                onLoadListener.onContainerHolderAvailable();

            }
        }, TIME_OUT_SEC, TimeUnit.SECONDS);
    }
}

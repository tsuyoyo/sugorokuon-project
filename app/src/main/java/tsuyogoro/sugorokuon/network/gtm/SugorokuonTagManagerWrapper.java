/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.gtm;

import com.google.android.gms.tagmanager.Container;

public class SugorokuonTagManagerWrapper {

    private static final String KEY_SERVER_URL = "distributionServerUrl";

    /**
     * 番組表配信サーバのURLをcontainerから取り出す。
     *
     * @param container
     * @return
     */
    public static final String getDistributionServerUrl(Container container) {
        return container.getString(KEY_SERVER_URL);
    }

}

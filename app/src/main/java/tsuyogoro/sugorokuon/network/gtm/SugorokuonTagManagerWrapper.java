/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.gtm;

import com.google.android.gms.tagmanager.Container;

public class SugorokuonTagManagerWrapper {

    private static final String KEY_SERVER_URL = "distributionServerUrl";

    private static final String KEY_SERVER_AVAILABILITY = "distributionServerAvailability";

    private static final String KEY_RADIKO_AD_FREQUENCY = "radikoAdFrequency";

    private static final String KEY_NHK_AD_FREQUENCY = "hnkAdFrequency";

    private static final int DEFAULT_FREQUENCY_RADIKO_AD = 10;

    private static final int DEFAULT_FREQUENCY_NHK_AD = 10;

    /**
     * 番組表配信サーバのURLをcontainerから取り出す。
     *
     * @return
     */
    public static String getDistributionServerUrl() {
        Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
        return container.getString(KEY_SERVER_URL);
    }

    /**
     * 番組配信サーバが利用可能かどうか
     *
     * @return
     */
    public static boolean getDistributionServerAvailable() {
        Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
        return container.getBoolean(KEY_SERVER_AVAILABILITY);
    }

    /**
     * Radiko timetableに広告を表示する頻度
     *
     * @return
     */
    public static int getRadikoTimetableAdFrequency() {
        Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
        Integer frequency = Integer.valueOf(container.getString(KEY_RADIKO_AD_FREQUENCY));

        return frequency != null ? frequency : DEFAULT_FREQUENCY_RADIKO_AD;
    }

    /**
     * NHK timetableに広告を表示する頻度
     *
     * @return
     */
    public static int getNhkTimetableAdFrequency() {
        Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
        Integer frequency = Integer.valueOf(container.getString(KEY_NHK_AD_FREQUENCY));

        return frequency != null ? frequency : DEFAULT_FREQUENCY_NHK_AD;
    }

}

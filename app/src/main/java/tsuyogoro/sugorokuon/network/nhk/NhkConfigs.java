package tsuyogoro.sugorokuon.network.nhk;

import com.google.android.gms.tagmanager.Container;

import tsuyogoro.sugorokuon.network.gtm.ContainerHolderSingleton;
import tsuyogoro.sugorokuon.network.gtm.SugorokuonTagManagerWrapper;

public class NhkConfigs {

    public static String getServerUrl() {
//        return "http://192.168.0.10:8080";
        Container container = ContainerHolderSingleton.getContainerHolder().getContainer();
        return SugorokuonTagManagerWrapper.getDistributionServerUrl(container);
    }

}

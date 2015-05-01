package tsuyogoro.sugorokuon.network.gtm;

import com.google.android.gms.tagmanager.ContainerHolder;

/**
 * Referred following snippet code
 * https://developers.google.com/tag-manager/android/v4/#container-singleton
 */
public class ContainerHolderSingleton {

    private static ContainerHolder containerHolder;

    private ContainerHolderSingleton() {
    }

    public static ContainerHolder getContainerHolder() {
        return containerHolder;
    }

    public static void setContainerHolder(ContainerHolder c) {
        containerHolder = c;
    }

}

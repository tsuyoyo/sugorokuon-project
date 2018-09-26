package tsuyogoro.sugorokuon.appstate;


import com.rejasupotaro.android.kvs.annotations.Key;
import com.rejasupotaro.android.kvs.annotations.Table;

@Table(name = "app")
class AppPrefsSchema {

    /**
     * Flag to present if tutorial has been done on v3.0 app.
     *
     */
    @Key(name = "tutorial_v3_done")
    boolean tutorial3Done;

}
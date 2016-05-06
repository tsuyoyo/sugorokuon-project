package tsuyogoro.sugorokuon.fragments.settings;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.models.prefs.NhkAreaSettingsPreference;
import tsuyogoro.sugorokuon.models.prefs.NhkAreaSettingsPreference.NhkArea;
import tsuyogoro.sugorokuon.network.nhk.NhkConfigs;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class NhkAreaSettingsFragment extends PreferenceFragment {

    private static final String API_NHK_AREA = "area/nhk/";

    public NhkAreaSettingsFragment() {
        super();
    }

    private AsyncTask<Void, Void, List<NhkArea>> mFetchTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFetchTask = new AsyncTask<Void, Void, List<NhkArea>>() {

            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setMessage(getString(R.string.data_loading));
                mProgressDialog.show();
            }

            @Override
            protected List<NhkArea> doInBackground(Void... params) {
                return fetchNhkArea();
            }

            @Override
            protected void onCancelled(List<NhkArea> nhkAreas) {
                super.onCancelled(nhkAreas);
                mProgressDialog.dismiss();
            }

            @Override
            protected void onPostExecute(List<NhkArea> nhkAreas) {
                super.onPostExecute(nhkAreas);

                if (isCancelled()) {
                    return;
                }

                mProgressDialog.dismiss();

                if (nhkAreas != null) {
                    PreferenceScreen screen =
                            getPreferenceManager().createPreferenceScreen(getActivity());
                    NhkAreaSettingsPreference.addPreferenceTo(screen, nhkAreas);
                    setPreferenceScreen(screen);
                } else {
                    Toast.makeText(getActivity(),
                            getString(R.string.settings_nhk_failed_to_fetch_area),
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFetchTask.cancel(false);
    }

    private List<NhkArea> fetchNhkArea() {

        // エリア情報の読み込みを開始
        String serverUrl = NhkConfigs.getServerUrl();
        if (!serverUrl.endsWith("/")) {
            serverUrl += "/";
        }
        serverUrl += API_NHK_AREA;

        Request request = new Request.Builder().url(serverUrl).get().build();
        OkHttpClient client = new OkHttpClient();

        List<NhkArea> areas = null;
        try {
            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                return null;
            }

            Gson gson = new Gson();
            areas = gson.fromJson(response.body().string(),
                    new TypeToken<List<NhkArea>>() {}.getType());

        } catch (IOException e) {
            SugorokuonLog.e("IOException at fetching station data : " + e.getMessage());
        }

        return areas;
    }

}

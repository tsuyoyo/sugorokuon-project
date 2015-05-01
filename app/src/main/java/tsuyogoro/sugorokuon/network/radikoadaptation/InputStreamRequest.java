package tsuyogoro.sugorokuon.network.radikoadaptation;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

// Note : This is to use Vollery library
class InputStreamRequest extends Request<InputStream> {

    private Response.Listener<InputStream> mListener;

    public InputStreamRequest(String url,
                              Response.Listener<InputStream> listener,
                              Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<InputStream> parseNetworkResponse(NetworkResponse response) {
        InputStream is = new ByteArrayInputStream(response.data);
        return Response.success(is, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(InputStream response) {
        mListener.onResponse(response);
    }
}

package come.example.kakaopay2;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyNetwork {
    private static VolleyNetwork instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    String serverUrl = "";

    private VolleyNetwork(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleyNetwork getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyNetwork(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    //==============================================================================================

    // sample password 변경 코드. 인터페이스 보고 추후 변경.
    public void serverDataRequest(String command,
                                  final Map<String, String> params,
                                  final OnResponseListener onResponseListener) {
        String url = command;

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        if (onResponseListener != null) {
                            onResponseListener.onResponseSuccessListener(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        if (onResponseListener != null) {
                            onResponseListener.onResponseFailListener(error);
                        }
                    }
                }) {
            @Override //response를 UTF8로 변경해주는 소스코드
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    // log error
                    return Response.error(new ParseError(e));
                } catch (Exception e) {
                    // log error
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> hparams = new HashMap<String, String>();
                String strKey = "KakaoAK " + "a5df511d73bd3b5e6592acf927ee945e";
                hparams.put("Authorization", strKey);
                hparams.put("Content-Type", "application/x-www-form-urlencoded");

                return hparams;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }
        };

        stringRequest.setShouldCache(false);
//        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
//                20000 ,
//                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Access the RequestQueue through your singleton class.
        VolleyNetwork.getInstance(this.ctx).addToRequestQueue(stringRequest);
    }

    //==============================================================================================

    public interface OnResponseListener {
        public void onResponseSuccessListener(String it);
        public void onResponseFailListener(VolleyError it);
    }
}

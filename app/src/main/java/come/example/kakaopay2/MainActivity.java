package come.example.kakaopay2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import static android.view.View.GONE;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<String>,
        View.OnClickListener {

    TextView tvRespon;
    Button btnPay1, btnPayLong, btnLongPayStart, btnLongPayCancle, btnLongPaySearch, btnPaySearch, btnCancelPay;
    WebView wbKakaoPay;
    ConstraintLayout natConst, webConst;
    private String tid, pg_token, sid;
    boolean paypageon = false, paypagelongon = false;

    int buttonSelect = 0;

    PayResult payResult;
    PayComplete payComplete;
    PayComplete.PayLongCancel payLongCancel;
    PayComplete.PayLongSearch payLongSearch;

    ErrorJsonList errorJsonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvRespon = (TextView) findViewById(R.id.response);
        btnPay1 = (Button) findViewById(R.id.btnPay);
        btnPayLong = (Button) findViewById(R.id.btnLongPay);
        btnLongPayStart = (Button) findViewById(R.id.btnLongPayStart);
        btnLongPayCancle = (Button) findViewById(R.id.btnLongPayCancle);
        btnLongPaySearch = (Button) findViewById(R.id.btnLongPaySearch);
        btnPaySearch = (Button) findViewById(R.id.btnPaySearch);
        btnCancelPay = (Button) findViewById(R.id.btnCancelPay);
        wbKakaoPay = (WebView) findViewById(R.id.payReadyWebView);
        natConst = (ConstraintLayout) findViewById(R.id.natConst);
        webConst = (ConstraintLayout) findViewById(R.id.webConst);

        btnPay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarRentInfoList();
                paypageon = true;
                buttonSelect = 0;
            }
        });
        btnPayLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarLongRentInfoList();
                paypagelongon = true;
                buttonSelect = 0;
            }
        });

        btnLongPayStart.setOnClickListener(this);
        btnLongPayCancle.setOnClickListener(this);
        btnLongPaySearch.setOnClickListener(this);
        btnPaySearch.setOnClickListener(this);
        btnCancelPay.setOnClickListener(this);

        wbKakaoPay.setWebViewClient(new MyWebClient());
        wbKakaoPay.setWebChromeClient(new MyWebChromeClient());
        WebSettings webSettings = wbKakaoPay.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLongPayStart:
                CarLongStartRentInfoList();
                buttonSelect = 0;
                break;
            case R.id.btnLongPayCancle:
                CarLongCancelRentInfoList();
                buttonSelect = 1;
                break;
            case R.id.btnLongPaySearch:
                CarLongSearchRentInfoList();
                buttonSelect = 2;
                break;
            case R.id.btnPaySearch:
                break;
            case R.id.btnCancelPay:
                break;
            default:
                break;
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        ProgressBar pb_item01 = (ProgressBar) findViewById(R.id.pb_item01);

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb_item01.setProgress(newProgress);

            if (newProgress == 100) {
                pb_item01.setVisibility(GONE);
            } else {
                pb_item01.setVisibility(View.VISIBLE);
            }
        }
        //MyWebChromeClient 끝
    }

    class MyWebClient extends WebViewClient {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            Log.v("webview", "webview display : url is => :" + url);
            if (url.startsWith("intent://kakaopay/pg")) {

                try {
                    Intent intent = Intent.parseUri(request.getUrl().toString(), Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null)
                        view.getContext().startActivity(intent);
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            //결재 하기 위한 등록된 웹페이지
            else if (url.startsWith("http://makemebad")) {

                final int customPG_TokenIndex = url.indexOf("pg_token=");
                pg_token = url.substring(customPG_TokenIndex + 9);
                wbKakaoPay.loadUrl(url);
                return true;
            }
            return false;
        }
    }

    //카카오페이 단건 결제
    private void CarRentInfoList() {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("cid", "TC0ONETIME");
        postParams.put("partner_order_id", "partner_order_id");
        postParams.put("partner_user_id", "partner_user_id");
        postParams.put("item_name", "신차로");
        postParams.put("quantity", "1");
        postParams.put("total_amount", "5500");
        postParams.put("vat_amount", "500");
        postParams.put("tax_free_amount", "0");
        postParams.put("approval_url", "http://makemebad.dothome.co.kr");
        postParams.put("fail_url", "http://makemebad.dothome.co.kr");
        postParams.put("cancel_url", "http://makemebad.dothome.co.kr");

        VolleyNetwork.getInstance(this).serverDataRequest("https://kapi.kakao.com/v1/payment/ready", postParams, onResponseListener);
        natConst.setVisibility(View.INVISIBLE);
        webConst.setVisibility(View.VISIBLE);
    }

    //카카오페이 정기 결제 최초
    private void CarLongRentInfoList() {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("cid", "TCSUBSCRIP");
        postParams.put("partner_order_id", "subscription_order_id_1");
        postParams.put("partner_user_id", "subscription_user_id_1");
        postParams.put("item_name", "신차로렌터카 정기");
        postParams.put("quantity", "1");
        postParams.put("total_amount", "10000");
        postParams.put("vat_amount", "1000");
        postParams.put("tax_free_amount", "0");
        postParams.put("approval_url", "http://makemebad.dothome.co.kr");
        postParams.put("fail_url", "http://makemebad.dothome.co.kr");
        postParams.put("cancel_url", "http://makemebad.dothome.co.kr");

        natConst.setVisibility(View.INVISIBLE);
        webConst.setVisibility(View.VISIBLE);

        VolleyNetwork.getInstance(this).serverDataRequest("https://kapi.kakao.com/v1/payment/ready", postParams, onResponseListener);
    }

    //카카오페이 정기 결제 요청(2회차부터)
    private void CarLongStartRentInfoList() {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("cid", "TCSUBSCRIP");
        postParams.put("sid", sid);
        postParams.put("partner_order_id", "subscription_order_id_1");
        postParams.put("partner_user_id", "subscription_user_id_1");
        postParams.put("item_name", "신차로렌터카 정기");
        postParams.put("quantity", "1");
        postParams.put("total_amount", "10000");
        postParams.put("vat_amount", "1000");
        postParams.put("tax_free_amount", "0");

        VolleyNetwork.getInstance(this).serverDataRequest("https://kapi.kakao.com/v1/payment/subscription", postParams, onResponseListener);
    }

    //정기 결제 비활성화
    private void CarLongCancelRentInfoList() {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("cid", "TCSUBSCRIP");
        postParams.put("sid", sid);

        VolleyNetwork.getInstance(this).serverDataRequest("https://kapi.kakao.com/v1/payment/manage/subscription/inactive", postParams, onResponseListener);
    }

    //정기 결제 상태 조회
    private void CarLongSearchRentInfoList() {
        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("cid", "TCSUBSCRIP");
        postParams.put("sid", sid);

        VolleyNetwork.getInstance(this).serverDataRequest("https://kapi.kakao.com/v1/payment/manage/subscription/status", postParams, onResponseListener);
    }

    VolleyNetwork.OnResponseListener onResponseListener = new VolleyNetwork.OnResponseListener() {
        @Override
        public void onResponseSuccessListener(String it) {
            Gson gson = new Gson();
            try {
                JSONObject json = new JSONObject(it);
                switch (buttonSelect) {
                    case 0:
                        if (!json.isNull("sid")) {
                            payComplete = gson.fromJson(it, PayComplete.class);
                            sid = payComplete.sid;
                        } else if (json.isNull("aid")) {
                            payResult = gson.fromJson(it, PayResult.class);
                            tid = payResult.tid;
                            wbKakaoPay.loadUrl(payResult.next_redirect_app_url);
                        } else {
                            payComplete = gson.fromJson(it, PayComplete.class);
                        }
                        break;
                    case 1:
                        payLongCancel = gson.fromJson(it, PayComplete.PayLongCancel.class);
                        tvRespon.setText("정기 결제 비활성화. \n" +
                                "정기 결제 상태:" + payLongCancel.status + "\n" +
                                "마지막 결제승인 시각:" + payLongCancel.last_approved_at + "\n" +
                                "정기 결제 비활성화 시각:" + payLongCancel.inactivated_at);
                        break;
                    case 2:
                        payLongSearch = gson.fromJson(it, PayComplete.PayLongSearch.class);
                        tvRespon.setText("정기 활성화 상태:" + payLongSearch.status + "\n" +
                                "사용 가능 여부:" + payLongSearch.available);
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResponseFailListener(VolleyError it) {
            Log.d("VolleyError", "onErrorResponse : " + String.valueOf(it));
            Log.d("VolleyError", "onErrorResponse : " + String.valueOf(it.networkResponse.statusCode));
            NetworkResponse networkResponse = it.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                String jsonError = new String(networkResponse.data);
                Gson gson = new Gson();
                errorJsonList = gson.fromJson(jsonError, ErrorJsonList.class);
            }

            Log.d("VolleyError", "msg : " + errorJsonList.msg);
            Log.d("VolleyError", "code : " + errorJsonList.code);
            Log.d("VolleyError", "extras(method_result_code :" + errorJsonList.extras.method_result_code + ")");
            Log.d("VolleyError", "extras(method_result_message :" + errorJsonList.extras.method_result_message + ")");
        }
    };

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        //비동기 처리를 수행하는 Loader를 생성
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        //비동기 처리가 끝나면 호출

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public void onBackPressed() {
        if (paypageon) {
            HashMap<String, String> postParams = new HashMap<String, String>();
            postParams.put("cid", "TC0ONETIME");
            postParams.put("tid", tid);
            postParams.put("partner_order_id", "partner_order_id");
            postParams.put("partner_user_id", "partner_user_id");
            postParams.put("pg_token", pg_token);
            VolleyNetwork.getInstance(this).serverDataRequest("https://kapi.kakao.com/v1/payment/approve", postParams, onResponseListener);
            wbKakaoPay.clearHistory();
            wbKakaoPay.removeAllViews();
            wbKakaoPay.destroy();
            natConst.setVisibility(View.VISIBLE);
            webConst.setVisibility(View.INVISIBLE);
            paypageon = false;
        } else if (paypagelongon) {
            HashMap<String, String> postParams = new HashMap<String, String>();
            postParams.put("cid", "TCSUBSCRIP");
            postParams.put("tid", tid);
            postParams.put("partner_order_id", "subscription_order_id_1");
            postParams.put("partner_user_id", "subscription_user_id_1");
            postParams.put("pg_token", pg_token);
            VolleyNetwork.getInstance(this).serverDataRequest("https://kapi.kakao.com/v1/payment/approve", postParams, onResponseListener);
            wbKakaoPay.clearHistory();
            wbKakaoPay.removeAllViews();
            wbKakaoPay.destroy();
            natConst.setVisibility(View.VISIBLE);
            webConst.setVisibility(View.INVISIBLE);
            paypagelongon = false;
        } else
            super.onBackPressed();
    }
}

class ErrorJsonList {

    String code;
    String msg;
    Extras extras;

    class Extras {
        String method_result_code;
        String method_result_message;
    }
}
package come.example.kakaopay2;

import org.json.JSONObject;

public class PayResult {
    public  String tid;
    public String next_redirect_app_url;
    public String next_redirect_mobile_url;
    public String next_redirect_pc_url;
    public String android_app_scheme;
    public String ios_app_scheme;
    public String created_at;
    private PayResult(String tid, String next_redirect_app_url, String next_redirect_mobile_url,
                     String next_redirect_pc_url, String android_app_scheme, String ios_app_scheme,
                     String created_at) {
        this.tid = tid;
        this.next_redirect_app_url = next_redirect_app_url;
        this.next_redirect_mobile_url = next_redirect_mobile_url;
        this.next_redirect_pc_url = next_redirect_pc_url;
        this.android_app_scheme = android_app_scheme;
        this.ios_app_scheme = ios_app_scheme;
        this.created_at = created_at;
    }
}

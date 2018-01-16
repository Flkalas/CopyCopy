package com.mindlink.flkalas.copycopy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends AppCompatActivity {

    public void returnCookie(String cookie){
        Bundle extra = new Bundle();
        Intent intent = new Intent();

        extra.putString("Cookie", cookie);
        intent.putExtras(extra);

        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CookieManager cm = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                    Log.d("DELETE", "" + value);
                }
            });
        }else{
            cm.removeAllCookie();
        }

        String url = "https://twitter.com/login";
        WebView wv = (WebView) findViewById(R.id.loginPage);

        wv.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    CookieManager inCooMa = CookieManager.getInstance();
                    String ck = inCooMa.getCookie(url);
                    if(ck != null) {
                        if(ck.contains("auth_token=")){
                            returnCookie(ck);
                        }
                    }
                    return true;
                }
            }
        );

        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(url);
    }
}

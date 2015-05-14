package pl.szalach.krzysztof.trellocardsmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import pl.szalach.krzysztof.trellocardsmanager.R;
import pl.szalach.krzysztof.trellocardsmanager.api.client.Constants;
import pl.szalach.krzysztof.trellocardsmanager.api.client.TokenExtractor;
import pl.szalach.krzysztof.trellocardsmanager.api.client.TokenExtractorListener;
import pl.szalach.krzysztof.trellocardsmanager.helpers.IntentKeys;


public class LoginActivity extends ActionBarActivity implements TokenExtractorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new TokenExtractor(this), "TokenExtractor");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if ("https://trello.com/1/token/approve".equals(url)) {
                    ((WebView) findViewById(R.id.webView)).loadUrl("javascript:window.TokenExtractor.extractToken" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                ((WebView) findViewById(R.id.webView)).loadUrl(url);
                return true;
            }
        });
        webView.loadUrl("https://trello.com/1/authorize?response_type=token&key=" + Constants.API_KEY + "&return_url=https%3A%2F%2Ftrello.com&callback_method=postMessage&scope=read,write&expiration=never&name=Trello+Cards+Manager");
    }

    @Override
    public void tokenExtracted(String token) {
        if (!TextUtils.isEmpty(token)) {
            Intent result = new Intent();
            result.putExtra(IntentKeys.TOKEN, token);
            setResult(RESULT_OK, result);
        }
        finish();
    }
}


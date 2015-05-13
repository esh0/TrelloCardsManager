package pl.szalach.krzysztof.trellocardsmanager.api.client;

import android.webkit.JavascriptInterface;

public class TokenExtractor {

    private final TokenExtractorListener mListener;

    public TokenExtractor(TokenExtractorListener listener) {
        mListener = listener;
    }

    @JavascriptInterface
    public void extractToken(String html) {
        int firstApostrophe = html.indexOf('\"') + 1;
        int secondApostrophe = html.indexOf('\"', firstApostrophe);
        String token = html.substring(firstApostrophe, secondApostrophe);
        if (mListener != null)
            mListener.tokenExtracted(token);
    }
}

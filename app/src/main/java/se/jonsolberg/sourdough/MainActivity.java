package se.jonsolberg.sourdough;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Hosts the bundled, fully offline calculator (assets/index.html) in a WebView.
 * The app has no INTERNET permission; nothing is loaded from the network.
 */
public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        web.setBackgroundColor(getColor(R.color.bg));
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // saved recipes + settings (localStorage)
        s.setAllowContentAccess(false);
        s.setBlockNetworkLoads(true);          // belt and braces: never touch the network
        s.setTextZoom(100);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                // Only the bundled page may load inside the app.
                return !"file".equals(u.getScheme());
            }
        });
        web.addJavascriptInterface(new Bridge(), "Android");

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl("file:///android_asset/index.html");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();   // closes kitchen mode
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }

    private void applyBars(boolean dark) {
        int color = getColor(dark ? R.color.bg : R.color.bg_light);
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);
        web.setBackgroundColor(color);
        WindowInsetsController c = getWindow().getInsetsController();
        if (c != null) {
            int mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
            c.setSystemBarsAppearance(dark ? 0 : mask, mask);
        } else {
            View d = getWindow().getDecorView();
            int f = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            d.setSystemUiVisibility(dark ? (d.getSystemUiVisibility() & ~f) : (d.getSystemUiVisibility() | f));
        }
    }

    /** Called from JavaScript as window.Android.* */
    private class Bridge {
        @JavascriptInterface
        public void share(String text, String title) {
            runOnUiThread(() -> {
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_SUBJECT, title);
                send.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(send, getString(R.string.share_title)));
            });
        }

        @JavascriptInterface
        public void keepAwake(boolean on) {
            runOnUiThread(() -> {
                if (on) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            });
        }

        @JavascriptInterface
        public void setDark(boolean dark) {
            runOnUiThread(() -> applyBars(dark));
        }

        /** Starts a countdown in the phone's clock app so it rings even if this app is closed. */
        @JavascriptInterface
        public void startTimer(String label, int seconds) {
            runOnUiThread(() -> {
                Intent i = new Intent(AlarmClock.ACTION_SET_TIMER)
                        .putExtra(AlarmClock.EXTRA_LENGTH, Math.max(1, seconds))
                        .putExtra(AlarmClock.EXTRA_MESSAGE, label)
                        .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, R.string.no_timer_app, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

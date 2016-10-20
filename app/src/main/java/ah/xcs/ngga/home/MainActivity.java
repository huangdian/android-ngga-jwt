package ah.xcs.ngga.home;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;


public class MainActivity extends Activity implements DownloadListener {
    private WebView webview;
    private DownloadManager dm;
    private ProgressBar myProgressBar;
    AsyncHttpClient client=new AsyncHttpClient();
    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            System.out.println(intent);
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Uri uri = dm.getUriForDownloadedFile(id);
                String mimeType = dm.getMimeTypeForDownloadedFile(id);
                Intent t = new Intent(Intent.ACTION_VIEW);
                t.setDataAndType(uri, mimeType);
                t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        webview = (WebView) findViewById(R.id.webView1);
//		ProxyUtil.setProxy(webview, "127.0.0.1", 7001);
//      client.setProxy("127.0.0.1",7001);

        webview.setWebViewClient(new NggaWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        // webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setLayoutAlgorithm(
                WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);

        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        webview.setDownloadListener(this);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        String userAgentString = webview.getSettings().getUserAgentString();
        webview.clearCache(true);
        // webview.getSettings().setAppCacheEnabled(false);

        // IntentFilter filter = new IntentFilter(Proxy.PROXY_CHANGE_ACTION);
        // registerReceiver(new ProxyChangeReceiver(), filter);

        // String url = "http://10.128.148.33:8000/telbook/tel/query!duty";
        // String url = "http://www.ng.xcs.ah";
        String url = "http://mail.qq.com";
//        String url = "http://192.168.118.127:8080/";
        webview.loadUrl(url);
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress < 100) {
                if (myProgressBar.getVisibility() == View.INVISIBLE) {
                    myProgressBar.setVisibility(View.VISIBLE);
                }
                myProgressBar.setProgress(newProgress);
            } else {
                myProgressBar.setProgress(100);
                myProgressBar.setVisibility(View.INVISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }


    }


    @Override
    public void onBackPressed() {
        webview.goBack();
        // super.onBackPressed();
    }

    private class NggaWebViewClient extends WebViewClient {

    }


    @Override
    public void onDownloadStart(String url, String userAgent,
                                String contentDisposition, String mimeType,
                                long contentLength) {
        downloadFile(url);
//        DownloadManager.Request request = new DownloadManager.Request(
//                Uri.parse(url));
//        request.setMimeType(mimeType);
//        String cookies = CookieManager.getInstance().getCookie(url);
//        request.addRequestHeader("cookie", cookies);
//        request.addRequestHeader("User-Agent", userAgent);
//        request.setDescription("Downloading file...");
//        request.setTitle(URLUtil.guessFileName(url, contentDisposition,
//                mimeType));
//        //request.allowScanningByMediaScanner();
//        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(
//                Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
//                        url, contentDisposition, mimeType));
//        long id = dm.enqueue(request);
//        Toast.makeText(getApplicationContext(), "Downloading File",
//                Toast.LENGTH_LONG).show();
    }


    public void downloadFile(String url) {
        String cookies = CookieManager.getInstance().getCookie(url);
        client.addHeader("cookie",cookies);
        client.get(url,new FileAsyncHttpResponseHandler(this){
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Intent t = new Intent(Intent.ACTION_VIEW);
                String mimeType = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
                t.setDataAndType(Uri.fromFile(file), mimeType);
                t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}



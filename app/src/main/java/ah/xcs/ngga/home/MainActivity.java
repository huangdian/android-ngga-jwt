package ah.xcs.ngga.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ah.xcs.ngga.duty.R;
import ah.xcs.ngga.util.ProxyUtil;
import cz.msebera.android.httpclient.Header;


public class MainActivity extends Activity implements DownloadListener {
    private WebView webview;
    private ProgressBar myProgressBar;
    private String ref;
    AsyncHttpClient client = new AsyncHttpClient();

    @JavascriptInterface
    public void exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("确认是否退出").setNegativeButton("取消", null)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView.setWebContentsDebuggingEnabled(true);

        myProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        webview = (WebView) findViewById(R.id.webView1);
        ProxyUtil.setProxy(webview, "127.0.0.1", 7001);
        client.setProxy("127.0.0.1", 7001);

        webview.setWebViewClient(new NggaWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.addJavascriptInterface(this, "app");
        webviewSetting();
        String url = "http://10.128.148.33:8090/index.asp";
        initDeviceInfo(url);

        webview.loadUrl(url);
    }


    private void initDeviceInfo(String url) {
        //获取IMEI
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephonyManager.getSubscriberId();
        String ssn = telephonyManager.getSimSerialNumber();
        String imei = telephonyManager.getDeviceId();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, "ssn=" + ssn);
        cookieManager.setCookie(url, "imsi=" + imsi);
        cookieManager.setCookie(url, "imei=" + imei);
    }

    private void webviewSetting() {
        webview.getSettings().setJavaScriptEnabled(true);
        // webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setLayoutAlgorithm(
                WebSettings.LayoutAlgorithm.NORMAL);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//缓存
        }
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setDefaultTextEncodingName("GBK");
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webview.setDownloadListener(this);

        String userAgentString = webview.getSettings().getUserAgentString();
        webview.clearCache(true);
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            showProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }


    }

    private void showProgress(int newProgress) {
        if (newProgress < 100) {
            if (myProgressBar.getVisibility() == View.INVISIBLE) {
                myProgressBar.setVisibility(View.VISIBLE);
            }
            myProgressBar.setProgress(newProgress);
        } else {
            myProgressBar.setProgress(100);
            myProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            exit();
        }
    }

    private class NggaWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            ref = url;
            if (url.startsWith("ftp://")) {
                Toast.makeText(getApplicationContext(), "暂不支持ftp下载", Toast.LENGTH_LONG).show();
                return true;
            } else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            myProgressBar.setProgress(100);
            myProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onDownloadStart(final String url, final String userAgent,
                                final String contentDisposition, final String mimeType,
                                long contentLength) {
        //todo 对话框提示文件大小，是否下载打开,提供取消按钮
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String fileSize = "未知";
        if (contentLength != -1) {
            fileSize = contentLength < 1024 * 1024 ? (contentLength / 1024) + " KB" : contentLength / (1024 * 1024) + " MB";
        }
        builder.setTitle("打开文件").setMessage("当前文件大小" + fileSize + ",是否下载打开文件?").setPositiveButton("下载打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadFile(url, userAgent, contentDisposition, mimeType);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.create().show();
    }

    AlertDialog dialog;
    RequestHandle req = null;
    Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("filename=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

    public void downloadFile(final String url, String userAgent, String contentDisposition, String mimeType) {

        dialog = new AlertDialog.Builder(this).setTitle("文件下载").setMessage("下载中...")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        req.cancel(true);
                    }
                }).create();

        String cookies = CookieManager.getInstance().getCookie(url);
        client.addHeader("cookie", cookies);
        client.addHeader("Referer", ref);
        client.setUserAgent(userAgent);
        contentDisposition = Uri.decode(contentDisposition);
        String filename = "";
        Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
        if (m.find()) {
            filename = m.group(1);
            filename = filename.replace("filename=", "").replace("\"", "");
        }
        if ("text/plain".equals(mimeType) ||
                "application/octet-stream".equals(mimeType)) {
            String[] dot = filename.split("\\.");
            String extention = dot[dot.length - 1];
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
            if (mimeType == null) {
                mimeType = "*/*";
            }
        }
        final String mt = mimeType;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File target = new File(path.getPath(), filename);
        req = client.get(url, new FileAsyncHttpResponseHandler(target) {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                //更新进度条
                int percent = (int) ((bytesWritten * 100f) / totalSize);
                showProgress(percent);
            }

            @Override
            public void onCancel() {
                showProgress(100);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                String filename = file.getName();
                file.setReadable(true);
                Intent t = new Intent(Intent.ACTION_VIEW);
                t.setDataAndType(Uri.fromFile(file), mt);
                t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                System.out.println(t);
                try {
                    dialog.dismiss();
                    startActivity(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }
}



package ah.xcs.ngga.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.IOException;

import ah.xcs.ngga.task.Download;
import fi.iki.elonen.NanoHTTPD;


public class MainActivity extends Activity {
    private XWalkView webview;
    private ProgressBar myProgressBar;
    NanoHTTPD nanoHTTPD;
    int port = 60000;
    String wwwroot = "file:///android_asset/web";
    // variables for camera and choosing files methods
    private static final int FILECHOOSER_RESULTCODE = 1;

    // the same for Android 5.0 methods only
    private ValueCallback<Uri> mFilePathCallback;
    private String TAG = "JWT";
    private Download downloadListener;

    public int startServer(int port) {
        try {
            nanoHTTPD = new WebServer(this, port);
            nanoHTTPD.start();
            return port;
        } catch (IOException e) {
            if (port < 65535) {
                startServer(port++);
            } else {
                Log.e("err", "本地服务启动失败！");
            }
        }
        return -1;
    }

    public void stopServer() {
        if (nanoHTTPD != null)
            nanoHTTPD.stop();
    }

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

    @JavascriptInterface
    public void clearHistory() {
        webview.post(new Runnable() {
            @Override
            public void run() {
                webview.getNavigationHistory().clear();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int servPort = startServer(port);
        if (servPort == -1) {
            Toast.makeText(this, "本地服务启动失败", Toast.LENGTH_LONG).show();
            return;
        }
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        }
        webview = (XWalkView) findViewById(R.id.webView1);
//        ProxyUtil.setProxy(webview, "127.0.0.1", 7001);
//        client.setProxy("127.0.0.1", 7001);

        webview.setUIClient(new NggaWebViewClient(webview));
        webview.setResourceClient(new MyWebChromeClient(webview));
        webview.addJavascriptInterface(this, "app");
        webviewSetting();
//        webview.clearCache(true);
        // String url = "http://10.128.148.33:8000/telbook/tel/query!duty";
//        String url = "http://www.ng.xcs.ah";
//        String url = "http://www.baidu.com";
        String url = "http://200.200.200.101:8080";
//        String url = "http://127.0.0.1:7020";
        webview.load(url, null);

    }

    private void webviewSetting() {
        webview.getSettings().setJavaScriptEnabled(true);
        // webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
//        webview.getSettings().setDisplayZoomControls(false);
//        webview.getSettings().setLayoutAlgorithm(
//                WebSettings.LayoutAlgorithm.NORMAL);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setCacheMode(XWalkSettings.LOAD_DEFAULT);//缓存
        }
//        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
//        if (Build.VERSION.SDK_INT >= 21) {
//            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webview.getSettings().setBlockNetworkLoads(true);
        downloadListener = new Download(this);
        webview.setDownloadListener(downloadListener);
        String userAgentString = webview.getSettings().getUserAgentString();
        webview.getSettings().setUserAgentString(userAgentString + " SYOA_ANDROID_CLIENT[" + port + "]");
    }

    @Override
    protected void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    private class MyWebChromeClient extends XWalkResourceClient {
        public MyWebChromeClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            myProgressBar.setProgress(100);
            myProgressBar.setVisibility(View.INVISIBLE);
            super.onLoadFinished(view, url);
        }

        @Override
        public void onLoadStarted(XWalkView view, String url) {
            downloadListener.setRef(url);
            super.onLoadStarted(view, url);
        }

        @Override
        public void onProgressChanged(XWalkView view, int newProgress) {
            showProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    // return here when file selected from camera or from SD Card

    private void showProgress(int newProgress) {
        if (newProgress > 0 && newProgress < 100) {
            if (myProgressBar.getVisibility() == View.INVISIBLE || myProgressBar.getVisibility() == View.GONE) {
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
        if (webview.getNavigationHistory().canGoBack()) {
            XWalkNavigationHistory navigationHistory = webview.getNavigationHistory();
            navigationHistory.navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);
        } else {
            exit();
        }
    }


    private class NggaWebViewClient extends XWalkUIClient {
        public NggaWebViewClient(XWalkView view) {
            super(view);
        }

        //        @Override
//        public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
//
//            return super.shouldOverrideUrlLoading(view, url);
//        }

        @Override
        public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = uploadFile;
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");
            startActivityForResult(contentSelectionIntent, FILECHOOSER_RESULTCODE);
            //super.openFileChooser(view, uploadFile, acceptType, capture);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri results = null;
        // check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            String dataString = data.getDataString();
            if (dataString != null) {
                results = Uri.parse(dataString);
            }
        }
        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }


}



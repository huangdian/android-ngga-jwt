package ah.xcs.ngga.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkDownloadListener;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/11/13.
 */

public class Download extends XWalkDownloadListener {
    private static final int UPDATE_PROGRESS = 1;
    private static final int ALERT = 2;
    Context ctx;
    AsyncHttpClient client = new AsyncHttpClient();
    AlertDialog dialog;
    private String ref;
    RequestHandle req = null;

    Handler mesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    updateProgress((String) msg.obj);
                    break;
                case ALERT:
                    alert((String) msg.obj);
                    break;
            }
        }
    };

    private void alert(String obj) {
        new AlertDialog.Builder(ctx).setMessage(obj).setTitle("提示").create().show();
    }

    private void updateProgress(String text) {
        if (dialog != null) {
            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setText(text);
        }
    }

    public Download(Context context) {
        super(context);
        this.ctx = context;
    }

    @Override
    public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimeType, final long contentLength) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
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

    Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("filename=\"?([^\"]*)\"?", Pattern.CASE_INSENSITIVE);

    public void downloadFile(final String url, String userAgent, String contentDisposition, String mimeType) {

        dialog = new AlertDialog.Builder(ctx).setTitle("文件下载").setMessage("下载中...")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (req != null) {
                            req.cancel(true);
                        }
                    }
                }).create();
        dialog.show();
        XWalkCookieManager manager = new XWalkCookieManager();
        String cookies = manager.getCookie(url);

        client.addHeader("cookie", cookies);
        client.addHeader("Referer", ref);
        client.setUserAgent(userAgent);
        contentDisposition = Uri.decode(contentDisposition);
        String filename = "";
        Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
        if (m.find()) {
            filename = m.group(1);
            filename = filename.replace("filename=", "").replace("\"", "");
        } else {
            filename = URLUtil.guessFileName(url, contentDisposition, mimeType);
        }


        if ("text/plain".equals(mimeType) ||
                "application/octet-stream".equals(mimeType) || "".equals(mimeType)) {
            String[] dot = filename.split("\\.");
            String extention = dot[dot.length - 1];
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
            mimeType = (mimeType == null || "".equals(mimeType)) ? MimeTypeMap.getFileExtensionFromUrl(url) : mimeType;
            if (mimeType == null || "".equals(mimeType)) {
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
                Message message = mesHandler.obtainMessage(UPDATE_PROGRESS);
                message.obj = "下载进度:" + percent + "%";
                message.sendToTarget();
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                dialog.dismiss();
                Message message = mesHandler.obtainMessage(ALERT);
                message.obj = "下载失败";
                message.sendToTarget();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                String filename = file.getName();
                file.setReadable(true);
                Intent t = new Intent(Intent.ACTION_VIEW);
                t.setDataAndType(Uri.fromFile(file), mt);
                t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    dialog.dismiss();
                    ctx.startActivity(t);
                } catch (Exception e) {
                    Message message = mesHandler.obtainMessage(ALERT);
                    message.obj = "下载失败:";
                    message.sendToTarget();
                    e.printStackTrace();
                }
            }
        });

    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}

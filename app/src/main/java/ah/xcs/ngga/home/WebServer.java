package ah.xcs.ngga.home;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Administrator on 2016/11/8.
 */

public class WebServer extends NanoHTTPD {
    Context ctx;

    public WebServer(Context ctx, int port) {
        super(port);
        this.ctx = ctx;
    }

    public static final String MIME_JAVASCRIPT = "text/javascript";
    public static final String MIME_CSS = "text/css";
    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_PNG = "image/png";
    public static final String MIME_SVG = "image/svg+xml";
    public static final String MIME_JSON = "application/json";
    public static final String MIME_TTF = "application/x-font-ttf";

    class NanoResp extends NanoHTTPD.Response {
        protected NanoResp(IStatus status, String mimeType, InputStream data, long totalBytes) {
            super(status, mimeType, data, totalBytes);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String mime_type = NanoHTTPD.MIME_HTML;
        Method method = session.getMethod();
        String uri = session.getUri();
        long bytes = 0;
        System.out.println(method + " '" + uri + "' ");
        InputStream descriptor = null;
        if (method.toString().equalsIgnoreCase("GET")) {
            String path;
            if (uri.equals("/")) {
                path = "/index.html";
            } else {
                path = uri;
                String[] dot = path.split("\\.");
                String extention = dot[dot.length - 1];
                if (path.endsWith(".js")) {
                    mime_type = MIME_JAVASCRIPT;
                } else if (path.endsWith(".css")) {
                    mime_type = MIME_CSS;
                } else if (path.endsWith(".html")) {
                    mime_type = MIME_HTML;
                } else if (path.endsWith(".jpeg")) {
                    mime_type = MIME_JPEG;
                } else if (path.endsWith(".png")) {
                    mime_type = MIME_PNG;
                } else if (path.endsWith(".jpg")) {
                    mime_type = MIME_JPEG;
                } else if (path.endsWith(".svg")) {
                    mime_type = MIME_SVG;
                } else if (path.endsWith(".json")) {
                    mime_type = MIME_JSON;
                } else if (path.endsWith(".ttf")) {
                    mime_type = MIME_TTF;
                } else {
                    mime_type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
                }
            }

            try {
                // Open file from SD Card
                descriptor = ctx.getAssets().open("web" + path);
                bytes = descriptor.available();
            } catch (IOException ioe) {
                Log.w("Httpd", ioe.toString());
            }
        }
        WebServer.NanoResp resp = new WebServer.NanoResp(Response.Status.OK, mime_type, descriptor, bytes);
        resp.addHeader("Access-Control-Allow-Origin", "*");//资源跨域请求
        return resp;

    }
}

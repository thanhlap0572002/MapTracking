package com.pethoalpar.osmdroidexmple;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class IPCamera {

    private final String cameraStreamPath;
    private String ipAddress;
    private WebView webView;
    private boolean isConnected;

    public IPCamera(String ipAddress, String cameraStreamPath, WebView webView) {
        this.ipAddress = ipAddress;
        this.cameraStreamPath = cameraStreamPath;
        this.webView = webView;
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
    }

    public void connectCamera() {
        try {
            // Kiểm tra kết nối tới camera bằng ipAddress
            if (ipAddress != null && !ipAddress.isEmpty()) {
                isConnected = true; // Giả sử kết nối thành công
            } else {
                throw new Exception("Địa chỉ IP không hợp lệ");
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void startStreaming() {
        try {
            if (isConnected) {
                String url = "http://" + ipAddress + cameraStreamPath;
                webView.loadUrl(url);
            } else {
                handleError(new Exception("Không kết nối được tới camera"));
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void play() {
        connectCamera();

        if (webView != null) {
            startStreaming();
        } else {
            handleError(new Exception("WebView bị null"));
        }
    }

    private void handleError(Exception e) {
        e.printStackTrace();
    }
    public void release() {
        if (webView != null) {
            webView.loadUrl("about:blank");
        }
    }
}

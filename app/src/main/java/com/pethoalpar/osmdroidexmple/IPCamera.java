package com.pethoalpar.osmdroidexmple;

import android.net.Uri;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;
import android.widget.VideoView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

public class IPCamera {

    private final String cameraStreamPath;
    private String ipAddress;
    private TextureView textureView;
    private VideoView videoView;
    private boolean isConnected;

    public IPCamera(String ipAddress, String cameraStreamPath, TextureView textureView, VideoView videoView) {
        this.ipAddress = ipAddress;
        this.cameraStreamPath = cameraStreamPath;
        this.textureView = textureView;
        this.videoView = videoView;
    }

    public void connectCamera() {
        try {
            // Kiểm tra kết nối tới camera bằng ipAddress
            // Nếu thành công, đặt isConnected = true;
            // Ngược lại, xử lý lỗi và không đặt isConnected = true;
            // Ví dụ:
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
                // Sử dụng FFmpeg để xử lý luồng video
                String cmd = "-i " + "http://" + ipAddress + cameraStreamPath + " -vf format=yuv420p -c:v libx264 -preset ultrafast -tune zerolatency -b:v 500k -minrate 500k -maxrate 500k -bufsize 1000k -f mpegts http://127.0.0.1:8080/video.ffm";

                // Thực thi lệnh FFmpeg
                int rc = FFmpeg.execute(cmd);

                if (rc == Config.RETURN_CODE_SUCCESS) {
                    // Lệnh FFmpeg thực thi thành công, hiển thị video trên VideoView
                    Uri uri = Uri.parse("http://127.0.0.1:8080/video.ffm");
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();
                    videoView.start();
                } else {
                    // Lệnh FFmpeg thất bại
                    handleError(new Exception("FFmpeg execution failed with rc=" + rc));
                }
            } else {
                handleError(new Exception("Không kết nối được tới camera"));
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void play() {
        Log.d("IPCamera", "Đang kết nối tới camera...");
        connectCamera();

        if (textureView != null) {
            Log.d("IPCamera", "Bắt đầu phát trực tiếp...");
            startStreaming();
        } else {
            Log.e("IPCamera", "TextureView bị null");
        }
    }

    private void handleError(Exception e) {
        // Xử lý lỗi, ví dụ, hiển thị Toast
        Toast.makeText(videoView.getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}

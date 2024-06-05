package com.pethoalpar.osmdroidexmple;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;


public class AlertOverlay extends Overlay {
    private Context context;
    private Location location;
    private Dialog dialog;
    private Handler handler;
    private DatabaseReference mDatabase;
    private boolean isFlashing;

    private Button alertButton;
    private int backgroundColor = Color.RED;
    private boolean isInitialAlertShown = false;
    private DatabaseReference databaseReference;

    private TextureView textureView;
    private IPCamera ipCamera;




    public AlertOverlay(Context context, Location location) {
        super(context);
        this.context = context;
        this.location = location;
        handler = new Handler();
//        textureView = dialog.findViewById(R.id.textureView);
      //  ipCamera = new IPCamera("192.168.17.103", "/cam-hi.jpg", textureView); // Thay đổi địa chỉ IP tương ứn

    }


    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);

        // Kiểm tra và hiển thị cửa sổ popup khi có cảnh báo
        if (isAlertNeeded() && isInitialAlertShown) {
            showDialog();
            startFlashingScreen();
            ipCamera.play();
        } else {
            dismissDialog();
            stopFlashingScreen();
        }

        // Đặt giá trị isInitialAlertShown thành true sau lần đầu tiên vẽ overlay
        isInitialAlertShown = true;
    }

    private boolean isAlertNeeded() {
        // Kiểm tra điều kiện cảnh báo ở đây
        // Ví dụ: nếu vị trí hiện tại gần với vị trí cảnh báo
        // Nếu cửa sổ popup đang hiển thị, trả về false để không hiển thị cảnh báo mới
        if (dialog != null && dialog.isShowing()) {
            return false;
        }
        return true;
    }
    public void updatePopupText(String text) {
        if (dialog != null && dialog.isShowing()) {
            TextView textView = dialog.findViewById(R.id.sau);
            textView.setText(text);
        }
    }

    public void showDialog() {

        // Khởi tạo Dialog
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(1000, 1500);
        dialog.getWindow().setGravity(Gravity.CENTER);



        mDatabase = FirebaseDatabase.getInstance().getReference();

        textureView = dialog.findViewById(R.id.textureView);

        if (textureView != null) {
            Log.d("AlertOverlay", "TextureView found and initialized successfully");
        } else {
            Log.e("AlertOverlay", "TextureView is null or not found in the layout");
        }

        Button closeButton = dialog.findViewById(R.id.button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

//        ipCamera = new IPCamera("192.168.17.103", "/cam-hi.jpg", textureView);
//        ipCamera.play();
//        dialog.show();

    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            // Dừng phát âm thanh
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.music);
            mediaPlayer.stop();
            mediaPlayer.release();

            dialog.dismiss();
            dialog = null;
        }
    }

    public void startFlashingScreen() {
        if (!isFlashing) {
            isFlashing = true;
            handler.post(flashRunnable);
        }
    }

    private void stopFlashingScreen() {
        if (isFlashing) {
            isFlashing = false;
            handler.removeCallbacks(flashRunnable);
            resetBackgroundColor();
        }
    }

    private void resetBackgroundColor() {
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.setBackgroundColor(Color.WHITE);
            }
        });
    }

    private Runnable flashRunnable = new Runnable() {
        private int backgroundColor = Color.RED;

        @Override
        public void run() {
            Activity activity = (Activity) context;
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            Drawable background = rootView.getBackground();
            if (background instanceof ColorDrawable) {
                int currentColor = ((ColorDrawable) background).getColor();
                if (currentColor == backgroundColor) {
                    resetBackgroundColor();
                } else {
                    setBackgroundColor(backgroundColor);
                }
            }
            handler.postDelayed(this, 500);
        }

        private void setBackgroundColor(int color) {
            Activity activity = (Activity) context;
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            rootView.setBackgroundColor(color);
        }
    };
}

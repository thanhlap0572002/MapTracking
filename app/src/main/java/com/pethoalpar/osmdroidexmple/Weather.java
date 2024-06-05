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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

public class Weather extends Overlay {
    private Context context;
    private Location location;
    private Dialog dialog;
    private Handler handler;
    private DatabaseReference database;
    private boolean isFlashing;
    private Button mytButton;
    private int backgroundColor = Color.RED;
    private boolean isInitialWeatherShown = false;


    public Weather(Context context, Location location) {
        super(context);
        this.context = context;
        this.location = location;
        handler = new Handler();
    }


    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);

        // Kiểm tra và hiển thị cửa sổ popup khi có cảnh báo
        if (isAlertNeeded() && isInitialWeatherShown) {
            showDialog();
//            startFlashingScreen();
        } else {
            dismissDialog();
//            stopFlashingScreen();
        }

        // Đặt giá trị isInitialAlertShown thành true sau lần đầu tiên vẽ overlay
        isInitialWeatherShown = true;
    }

    private boolean isAlertNeeded() {
        // Kiểm tra điều kiện cảnh báo ở đây
        // Ví dụ: nếu vị trí hiện tại gần với vị trí cảnh báo
        // Nếu cửa sổ popup đang hiển thị, trả về false để không hiển thị cảnh báo mới
        if (dialog != null && dialog.isShowing()) {
            return false;
        }

        // Trả về true nếu cần hiển thị cảnh báo, ngược lại trả về false
        return dialog == null;
    }
    public void updatePopupText(String text) {
        if (dialog != null && dialog.isShowing()) {
            TextView textView = dialog.findViewById(R.id.tat);
            textView.setText(text);
        }
    }

    public void showDialog() {
        // Khởi tạo Dialog
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_weather);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(1000, 1500);
        dialog.getWindow().setGravity(Gravity.CENTER);

        database = FirebaseDatabase.getInstance().getReference();
//        //day1
//
//        database.child("Weather future").child("Dự báo thời tiết").child("0").child("Ngày").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String day1 = dataSnapshot.getValue(String.class);
//
//                TextView day1TextView = dialog.findViewById(R.id.weather2);
//                day1TextView.setText(day1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//
//        database.child("Weather future").child("Dự báo thời tiết").child("0").child("Nhiệt độ").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String temp1 = doubleValue.toString();
//                TextView temp1TextView = dialog.findViewById(R.id.temp1);
//                temp1TextView.setText(temp1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Weather future").child("Dự báo thời tiết").child("0").child("Thời tiết").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String weather1 = dataSnapshot.getValue(String.class);
//                TextView weather1TextView = dialog.findViewById(R.id.weather1);
//                weather1TextView.setText(weather1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
////day2
//        database.child("Weather future").child("Dự báo thời tiết").child("1").child("Ngày").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String day2 = dataSnapshot.getValue(String.class);
//
//
//                TextView day2TextView = dialog.findViewById(R.id.day2);
//                day2TextView.setText(day2);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Weather future").child("Dự báo thời tiết").child("1").child("Nhiệt độ").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String temp2 = doubleValue.toString();
//                TextView temp2TextView = dialog.findViewById(R.id.temp2);
//                temp2TextView.setText(temp2);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Weather future").child("Dự báo thời tiết").child("1").child("Thời tiết").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String weather2 = dataSnapshot.getValue(String.class);
//
//                TextView weather2TextView = dialog.findViewById(R.id.weather2);
//                weather2TextView.setText(weather2);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
////day3
//        database.child("Weather future").child("Dự báo thời tiết").child("2").child("Ngày").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String day3 = dataSnapshot.getValue(String.class);
//
//                TextView day3TextView = dialog.findViewById(R.id.day3);
//                day3TextView.setText(day3);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Weather future").child("Dự báo thời tiết").child("2").child("Nhiệt độ").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String temp3 = doubleValue.toString();
//
//                TextView temp3TextView = dialog.findViewById(R.id.temp3);
//                temp3TextView.setText(temp3);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Weather future").child("Dự báo thời tiết").child("2").child("Thời tiết").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String weather3 = dataSnapshot.getValue(String.class);
//                TextView weather3TextView = dialog.findViewById(R.id.weather3);
//                weather3TextView.setText(weather3);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });

        Button closeButton = dialog.findViewById(R.id.button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        dialog.show();
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



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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

public class LocationOverlay extends Overlay {

    private Context context;
    private Location location;
    private Dialog dialog;
    private Handler handler;
    private DatabaseReference database;
    private boolean isFlashing;
    private Button mytButton;
    private int backgroundColor = Color.RED;
    private boolean isInitialWeatherShown = false;


    public LocationOverlay(Context context, Location location) {
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
        dialog.setContentView(R.layout.popup_location);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(1000, 1600);
        dialog.getWindow().setGravity(Gravity.CENTER);

        database = FirebaseDatabase.getInstance().getReference();


        //day1
//
//
//        database.child("Save Way Current_location").child("09:06:57_27-07-2023").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Integer doubleValue = dataSnapshot.getValue(Integer.class);
////                String day1 = doubleValue.toString();
//                String day1 = dataSnapshot.getValue(String.class);
//
//                TextView day1TextView = dialog.findViewById(R.id.day01);
//                day1TextView.setText(day1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//
//        database.child("Save Way Current_location").child("09:06:57_27-07-2023").child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String temp1 = doubleValue.toString();
//
//
//                TextView temp1TextView = dialog.findViewById(R.id.lat1);
//                temp1TextView.setText(temp1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("09:06:57_27-07-2023").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String weather1 = dataSnapshot.getValue(String.class);
//                TextView weather1TextView = dialog.findViewById(R.id.lon1);
//                weather1TextView.setText(weather1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("09:09:43_27-07-2023").child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Integer doubleValue = dataSnapshot.getValue(Integer.class);
////                String day2 = doubleValue.toString();
//                String day2 = dataSnapshot.getValue(String.class);
//
//
//                TextView day2TextView = dialog.findViewById(R.id.day02);
//                day2TextView.setText(day2);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("09:09:43_27-07-2023").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String temp2 = doubleValue.toString();
//
//                TextView temp2TextView = dialog.findViewById(R.id.lat2);
//                temp2TextView.setText(temp2);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("09:09:53_27-07-2023").child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String weather2 = dataSnapshot.getValue(String.class);
//
//                TextView weather2TextView = dialog.findViewById(R.id.lon2);
//                weather2TextView.setText(weather2);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
////day3
//        database.child("Save Way Current_location").child("09:09:53_27-07-2023").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Integer doubleValue = dataSnapshot.getValue(Integer.class);
////                String day3 = doubleValue.toString();
//                String day3 = dataSnapshot.getValue(String.class);
//
//                TextView day3TextView = dialog.findViewById(R.id.day03);
//                day3TextView.setText(day3);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("09:11:49_27-07-2023").child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String temp3 = doubleValue.toString();
//
//                TextView temp3TextView = dialog.findViewById(R.id.lat3);
//                temp3TextView.setText(temp3);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("09:11:49_27-07-2023").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String weather3 = dataSnapshot.getValue(String.class);
//                TextView weather3TextView = dialog.findViewById(R.id.lon3);
//                weather3TextView.setText(weather3);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });

//
//        database.child("Save Way Current_location").child("1").child("Time").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Integer doubleValue = dataSnapshot.getValue(Integer.class);
////                String day1 = doubleValue.toString();
//                String day01 = dataSnapshot.getValue(String.class);
//
//                TextView day01TextView = dialog.findViewById(R.id.day01);
//                day01TextView.setText(day01);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//
//        database.child("Save Way Current_location").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String lat1 = doubleValue.toString();
//
//
//                TextView lat1TextView = dialog.findViewById(R.id.lat1);
//                lat1TextView.setText(lat1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("09:06:57_27-07-2023").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String lon1 = dataSnapshot.getValue(String.class);
//                TextView lon1TextView = dialog.findViewById(R.id.lon1);
//                lon1TextView.setText(lon1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
////day2
//        database.child("Save Way Current_location").child("2").child("Time").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Integer doubleValue = dataSnapshot.getValue(Integer.class);
////                String day2 = doubleValue.toString();
//                String day02 = dataSnapshot.getValue(String.class);
//
//
//                TextView day02TextView = dialog.findViewById(R.id.day02);
//                day02TextView.setText(day02);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("2").child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String lat2 = doubleValue.toString();
//
//                TextView lat2TextView = dialog.findViewById(R.id.lat2);
//                lat2TextView.setText(lat2);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("2").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String lon2 = dataSnapshot.getValue(String.class);
//
//                TextView lon2TextView = dialog.findViewById(R.id.lon2);
//                lon2TextView.setText(lon2);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
////day3
//        database.child("Save Way Current_location").child("3").child("Time ").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Integer doubleValue = dataSnapshot.getValue(Integer.class);
////                String day3 = doubleValue.toString();
//                String day03 = dataSnapshot.getValue(String.class);
//
//                TextView day03TextView = dialog.findViewById(R.id.day03);
//                day03TextView.setText(day03);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("3").child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String lat3 = doubleValue.toString();
//
//                TextView lat3TextView = dialog.findViewById(R.id.lat3);
//                lat3TextView.setText(lat3);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("3").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String lon3 = dataSnapshot.getValue(String.class);
//                TextView lon3TextView = dialog.findViewById(R.id.lon3);
//                lon3TextView.setText(lon3);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("4").child("Time ").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Integer doubleValue = dataSnapshot.getValue(Integer.class);
////                String day3 = doubleValue.toString();
//                String day04 = dataSnapshot.getValue(String.class);
//
//                TextView day04TextView = dialog.findViewById(R.id.day04);
//                day04TextView.setText(day04);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("4").child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String lat4 = doubleValue.toString();
//
//                TextView lat4TextView = dialog.findViewById(R.id.lat4);
//                lat4TextView.setText(lat4);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("4").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String lon4 = dataSnapshot.getValue(String.class);
//                TextView lon4TextView = dialog.findViewById(R.id.lon4);
//                lon4TextView.setText(lon4);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("5").child("Time ").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Integer doubleValue = dataSnapshot.getValue(Integer.class);
////                String day3 = doubleValue.toString();
//                String day05 = dataSnapshot.getValue(String.class);
//
//                TextView day05TextView = dialog.findViewById(R.id.day05);
//                day05TextView.setText(day05);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("5").child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer doubleValue = dataSnapshot.getValue(Integer.class);
//                String lat5 = doubleValue.toString();
//
//                TextView lat5TextView = dialog.findViewById(R.id.lat5);
//                lat5TextView.setText(lat5);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });
//
//        database.child("Save Way Current_location").child("5").child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String lon5 = dataSnapshot.getValue(String.class);
//                TextView lon5TextView = dialog.findViewById(R.id.lon5);
//                lon5TextView.setText(lon5);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//            }
//        });


        Button closeButton = dialog.findViewById(R.id.button_location);
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



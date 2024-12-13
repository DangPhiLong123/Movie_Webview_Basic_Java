package com.example.movie.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movie.R;
import com.example.movie.constant.AboutUsConfig;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Yêu cầu tắt tiêu đề cho activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Đặt chế độ toàn màn hình cho cửa sổ hiện tại (ẩn thanh trạng thái)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Thiết lập giao diện cho activity từ file XML activity_splash
        setContentView(R.layout.activity_splash);

        // Gọi phương thức khởi tạo giao diện người dùng
        initUi();
        // Gọi phương thức để bắt đầu MainActivity sau một khoảng thời gian nhất định
        startMainActivity();
    }

    // Hàm để khởi tạo các thành phần giao diện của màn hình Splash
    private void initUi() {
        // Tìm TextView với ID tv_about_us_title và tv_about_us_slogan trong layout
        TextView tvAboutUsTitle = findViewById(R.id.tv_about_us_title);
        TextView tvAboutUsSlogan = findViewById(R.id.tv_about_us_slogan);
        // Thiết lập tiêu đề và slogan từ class AboutUsConfig
        tvAboutUsTitle.setText(AboutUsConfig.ABOUT_US_TITLE);
        tvAboutUsSlogan.setText(AboutUsConfig.ABOUT_US_SLOGAN);
    }

    // Hàm để khởi chạy MainActivity sau một khoảng thời gian chờ (delay)
    private void startMainActivity() {
        // Tạo một đối tượng Handler để xử lý việc delay
        Handler handler = new Handler();
        // Sử dụng postDelayed để thực hiện hành động sau 1.5 giây (1500 ms)
        handler.postDelayed(() -> {
            // Tạo intent để chuyển từ SplashActivity sang MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            // Đặt cờ để xóa tất cả các activity cũ và tạo activity mới
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Khởi chạy MainActivity
            startActivity(intent);
            // Kết thúc SplashActivity để không quay lại màn hình này khi nhấn nút back
            finish();
        }, 1500); // Delay trong 1500ms (1.5 giây)
    }
}

package com.example.movie.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class Utils { // Khai báo class Utils chứa các phương thức tiện ích có thể dùng chung.

    // Phương thức static 'hideSoftKeyboard' giúp ẩn bàn phím ảo trong một Activity.
    public static void hideSoftKeyboard(Activity activity) {
        try {
            // Lấy InputMethodManager từ hệ thống, đối tượng này điều khiển các tác vụ liên quan đến bàn phím.
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);

            // Sử dụng inputMethodManager để ẩn bàn phím ảo. 'getCurrentFocus()' trả về View hiện tại đang có focus trong Activity.
            // 'getWindowToken()' trả về mã thông báo (token) liên quan đến cửa sổ mà View đó thuộc về, cần thiết để yêu cầu ẩn bàn phím.
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            // Bắt ngoại lệ NullPointerException trong trường hợp không có View nào có focus, hoặc getCurrentFocus() trả về null.
            ex.printStackTrace(); // In chi tiết ngoại lệ ra console để hỗ trợ trong việc debug.
        }
    }
}

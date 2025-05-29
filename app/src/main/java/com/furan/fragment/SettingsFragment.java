package com.furan.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.furan.R;
import com.furan.activity.LoginActivity;
import com.furan.database.UserDatabaseHelper;
import com.furan.model.UserSession;

public class SettingsFragment extends Fragment {
    private static final int REQUEST_CODE_OPEN_DIRECTORY = 3001;
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    public SettingsFragment() {
        super(R.layout.fragment_setting);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 返回按钮
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        Button btnChooseFolder = view.findViewById(R.id.btn_choose_music_folder);
        Button btnUserManagement = view.findViewById(R.id.btn_user_management);
        LinearLayout layoutUserInfo = view.findViewById(R.id.layout_user_info);
        TextView tvUserName = view.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = view.findViewById(R.id.tv_user_email);
        TextView tvLoginTime = view.findViewById(R.id.tv_login_time);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        Switch switchTheme = view.findViewById(R.id.switch_theme);

        // 读取主题偏好，初始化开关状态并应用主题
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        switchTheme.setChecked(isDarkMode);
        applyTheme(isDarkMode);

        // 监听切换主题开关
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            applyTheme(isChecked);
            // 重启 Activity 以刷新主题
            requireActivity().recreate();
        });

        btnChooseFolder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
        });

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
        Cursor cursor = dbHelper.getLoggedInUser();

        if (cursor != null && cursor.moveToFirst()) {
            // 已登录：显示用户信息，隐藏“用户管理”按钮
            layoutUserInfo.setVisibility(View.VISIBLE);
            btnUserManagement.setVisibility(View.GONE);

            tvUserName.setText("用户名: " + cursor.getString(cursor.getColumnIndexOrThrow("name")));
            UserSession.setCurrentUserName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            tvUserEmail.setText("邮箱: " + cursor.getString(cursor.getColumnIndexOrThrow("email")));
            tvLoginTime.setText("登录时间: " + cursor.getString(cursor.getColumnIndexOrThrow("loginTime")));
            cursor.close();
        } else {
            // 未登录：隐藏用户信息，显示“用户管理”按钮
            layoutUserInfo.setVisibility(View.GONE);
            btnUserManagement.setVisibility(View.VISIBLE);
        }

        btnUserManagement.setOnClickListener(v -> {
            Cursor c = dbHelper.getLoggedInUser();
            if (c != null && c.moveToFirst()) {
                layoutUserInfo.setVisibility(View.VISIBLE);
                tvUserName.setText("用户名: " + c.getString(c.getColumnIndexOrThrow("name")));
                tvUserEmail.setText("邮箱: " + c.getString(c.getColumnIndexOrThrow("email")));
                tvLoginTime.setText("登录时间: " + c.getString(c.getColumnIndexOrThrow("loginTime")));
                c.close();
            } else {
                layoutUserInfo.setVisibility(View.GONE);
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        });

        btnLogout.setOnClickListener(v -> {
            dbHelper.logout();
            layoutUserInfo.setVisibility(View.GONE);
            btnUserManagement.setVisibility(View.VISIBLE);
            UserSession.setCurrentUserName("unknown");
            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        });
    }

    // 应用暗黑或浅色主题
    private void applyTheme(boolean darkMode) {
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri treeUri = data.getData();
                if (treeUri != null) {
                    requireContext().getContentResolver().takePersistableUriPermission(treeUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    requireContext().getSharedPreferences("music_prefs", Activity.MODE_PRIVATE)
                            .edit().putString("music_folder_uri", treeUri.toString()).apply();
                    Toast.makeText(getContext(), "音乐文件夹设置成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

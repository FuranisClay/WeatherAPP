package com.furan.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.furan.R;
import com.furan.activity.LoginActivity;
import com.furan.database.UserDatabaseHelper;

public class SettingsFragment extends Fragment {
    private static final int REQUEST_CODE_OPEN_DIRECTORY = 3001;

    public SettingsFragment() {
        super(R.layout.fragment_setting);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnChooseFolder = view.findViewById(R.id.btn_choose_music_folder);
        Button btnUserManagement = view.findViewById(R.id.btn_user_management);
        LinearLayout layoutUserInfo = view.findViewById(R.id.layout_user_info);
        TextView tvUserName = view.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = view.findViewById(R.id.tv_user_email);
        TextView tvLoginTime = view.findViewById(R.id.tv_login_time);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        btnChooseFolder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
        });

        btnUserManagement.setOnClickListener(v -> {
            UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
            Cursor cursor = dbHelper.getLoggedInUser();
            if (cursor != null && cursor.moveToFirst()) {
                layoutUserInfo.setVisibility(View.VISIBLE);
                tvUserName.setText("用户名: " + cursor.getString(cursor.getColumnIndexOrThrow("name")));
                tvUserEmail.setText("邮箱: " + cursor.getString(cursor.getColumnIndexOrThrow("email")));
                tvLoginTime.setText("登录时间: " + cursor.getString(cursor.getColumnIndexOrThrow("loginTime")));
                cursor.close();
            } else {
                layoutUserInfo.setVisibility(View.GONE);
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        });

        btnLogout.setOnClickListener(v -> {
            new UserDatabaseHelper(requireContext()).logout();
            layoutUserInfo.setVisibility(View.GONE);
            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        });

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
        Cursor cursor = dbHelper.getLoggedInUser();
        if (cursor != null && cursor.moveToFirst()) {
            layoutUserInfo.setVisibility(View.VISIBLE);
            tvUserName.setText("用户名: " + cursor.getString(cursor.getColumnIndexOrThrow("name")));
            tvUserEmail.setText("邮箱: " + cursor.getString(cursor.getColumnIndexOrThrow("email")));
            tvLoginTime.setText("登录时间: " + cursor.getString(cursor.getColumnIndexOrThrow("loginTime")));
            cursor.close();
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
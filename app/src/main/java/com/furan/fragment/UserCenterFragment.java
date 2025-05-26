package com.furan.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.furan.R;
import com.furan.activity.LoginActivity;
import com.furan.database.UserManager;

public class UserCenterFragment extends Fragment {

    private TextView tvUserInfo;
    private Button btnLogin, btnSwitchUser, btnLogout, btnChooseFolder;

    private static final int REQUEST_LOGIN = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvUserInfo = view.findViewById(R.id.tv_user_info);
        btnLogin = view.findViewById(R.id.btn_login);
        btnSwitchUser = view.findViewById(R.id.btn_switch_user);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnChooseFolder = view.findViewById(R.id.btn_choose_music_folder);

        updateUI();

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        });

        btnSwitchUser.setOnClickListener(v -> {
            UserManager.getInstance(getActivity()).logout();
            updateUI();
        });

        btnLogout.setOnClickListener(v -> {
            UserManager.getInstance(getActivity()).logout();
            updateUI();
        });

        btnChooseFolder.setOnClickListener(v -> {
            // 选择目录功能
        });
    }

    private void updateUI() {
        if (UserManager.getInstance(getActivity()).isLoggedIn()) {
            tvUserInfo.setText("当前用户：" + UserManager.getInstance(getActivity()).getCurrentUser());
            btnLogin.setVisibility(View.GONE);
            btnSwitchUser.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvUserInfo.setText("当前未登录");
            btnLogin.setVisibility(View.VISIBLE);
            btnSwitchUser.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            updateUI();
        }
    }
}

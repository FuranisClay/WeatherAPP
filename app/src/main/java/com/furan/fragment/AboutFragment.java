package com.furan.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.furan.R;

public class AboutFragment extends Fragment {

    private TextView appNameText, versionText, developerText, contactText, descriptionText;

    public AboutFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appNameText = view.findViewById(R.id.appNameText);
        versionText = view.findViewById(R.id.versionText);
        developerText = view.findViewById(R.id.developerText);
        contactText = view.findViewById(R.id.contactText);
        descriptionText = view.findViewById(R.id.descriptionText);

        appNameText.setText(getString(R.string.app_name));

        // 获取版本号
        try {
            PackageInfo pInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            versionText.setText("版本号: " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            versionText.setText("版本号: 未知");
        }

        developerText.setText("开发者: ClaYtoN");
        contactText.setText("联系方式: 18904183367");
        descriptionText.setText("应用简介: 这是一款融合天气、音乐、日记功能的生活助手 App，简洁美观，操作便捷，助你更好管理日常生活。");
    }
}

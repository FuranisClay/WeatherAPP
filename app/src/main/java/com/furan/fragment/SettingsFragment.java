package com.furan.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.furan.R;

public class SettingsFragment extends Fragment {

    private static final int REQUEST_CODE_OPEN_DIRECTORY = 3001;

    public SettingsFragment() {
        super(R.layout.fragment_setting);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnChooseFolder = view.findViewById(R.id.btn_choose_music_folder);
        btnChooseFolder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
        });

        // 提示当前已设置路径
        String currentUri = requireContext()
                .getSharedPreferences("music_prefs", Activity.MODE_PRIVATE)
                .getString("music_folder_uri", null);
        if (currentUri != null) {
            Toast.makeText(getContext(), "当前音乐路径已设置", Toast.LENGTH_SHORT).show();
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
                            .edit()
                            .putString("music_folder_uri", treeUri.toString())
                            .apply();

                    Toast.makeText(getContext(), "音乐文件夹设置成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

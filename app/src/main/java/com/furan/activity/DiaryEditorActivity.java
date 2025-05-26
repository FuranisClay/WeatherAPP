package com.furan.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.furan.R;
import com.furan.database.DiaryDatabaseHelper;
import com.furan.model.DiaryEntry;
import java.util.Date;

public class DiaryEditorActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etContent;
    private Button btnSave, btnCancel;
    private DiaryDatabaseHelper dbHelper;
    private long diaryId = -1;
    private long dateMillis;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_editor);

        initViews();
        setupToolbar();    // 不用 setSupportActionBar，改成自定义导航监听
        processIntent();
        setupClickListeners();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        dbHelper = new DiaryDatabaseHelper(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // 设置标题
        toolbar.setTitle("");
        // 设置导航图标（记得在你的layout文件中Toolbar里加 android:navigationIcon）
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void processIntent() {
        diaryId = getIntent().getLongExtra("diary_id", -1);
        dateMillis = getIntent().getLongExtra("date", System.currentTimeMillis());

        if (diaryId != -1) {
            isEditMode = true;
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("编辑日记");
            loadDiaryData();
        } else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("新建日记");
        }
    }

    private void loadDiaryData() {
        DiaryEntry entry = dbHelper.getDiaryEntryById(diaryId);
        if (entry != null) {
            etTitle.setText(entry.getTitle());
            etContent.setText(entry.getContent());
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveDiary());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveDiary() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("请输入标题");
            return;
        }

        if (content.isEmpty()) {
            etContent.setError("请输入内容");
            return;
        }

        DiaryEntry entry = new DiaryEntry(title, content, new Date(dateMillis));

        try {
            if (isEditMode) {
                entry.setId(diaryId);
                dbHelper.updateDiaryEntry(entry);
                Toast.makeText(this, "日记更新成功", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.insertDiaryEntry(entry);
                Toast.makeText(this, "日记保存成功", Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}

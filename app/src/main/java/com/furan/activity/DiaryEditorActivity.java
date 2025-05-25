package com.furan.activity;

import android.os.Bundle;
import android.view.MenuItem;
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
        setupToolbar();
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
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void processIntent() {
        diaryId = getIntent().getLongExtra("diary_id", -1);
        dateMillis = getIntent().getLongExtra("date", System.currentTimeMillis());

        if (diaryId != -1) {
            // 编辑模式
            isEditMode = true;
            setTitle("编辑日记");
            loadDiaryData();
        } else {
            // 新建模式
            setTitle("新建日记");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.furan.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furan.service.DiaryReminderReceiver;
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
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void processIntent() {
        diaryId = getIntent().getLongExtra("diary_id", -1);
        dateMillis = getIntent().getLongExtra("date", System.currentTimeMillis());

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (diaryId != -1) {
            isEditMode = true;
            toolbar.setTitle("编辑日记");
            loadDiaryData();
        } else {
            toolbar.setTitle("新建日记");
        }
    }

    private void loadDiaryData() {
        DiaryEntry entry = dbHelper.getDiaryEntryById(diaryId);
        if (entry != null) {
            etTitle.setText(entry.getTitle());
            etContent.setText(entry.getContent());
            dateMillis = entry.getDate().getTime(); // 确保提醒时间是日记时间
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
                setDiaryReminder(entry);
            } else {
                long newId = dbHelper.insertDiaryEntry(entry);
                entry.setId(newId);
                Toast.makeText(this, "日记保存成功", Toast.LENGTH_SHORT).show();
                setDiaryReminder(entry);
            }
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setDiaryReminder(DiaryEntry entry) {
        long reminderTime = entry.getDate().getTime();
        if (reminderTime <= System.currentTimeMillis()) {
            // 如果时间已过，不设置提醒
            return;
        }

        Intent intent = new Intent(this, DiaryReminderReceiver.class);
        intent.putExtra("diary_id", entry.getId());
        intent.putExtra("title", entry.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) entry.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }
    }
}

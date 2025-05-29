package com.furan.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furan.R;
import com.furan.database.DiaryDatabaseHelper;
import com.furan.model.DiaryEntry;
import com.furan.service.DiaryReminderReceiver;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

public class DiaryEditorActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etContent;
    private Button btnSave, btnCancel, btnPickDate, btnPickTime;
    private TextView tvSelectedTime;
    private DiaryDatabaseHelper dbHelper;
    private long diaryId = -1;
    private long dateMillis;
    private boolean isEditMode = false;

    private Calendar calendar;

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
        btnPickDate = findViewById(R.id.btn_pick_date);
        btnPickTime = findViewById(R.id.btn_pick_time);
        tvSelectedTime = findViewById(R.id.tv_selected_time); // 新增

        dbHelper = new DiaryDatabaseHelper(this);
        calendar = Calendar.getInstance();

        updateSelectedTimeText(); // 初始化时先显示当前时间
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void processIntent() {
        diaryId = getIntent().getLongExtra("diary_id", -1);
        dateMillis = getIntent().getLongExtra("date", System.currentTimeMillis());
        calendar.setTimeInMillis(dateMillis);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (diaryId != -1) {
            isEditMode = true;
            toolbar.setTitle("编辑日记");
            loadDiaryData();
        } else {
            toolbar.setTitle("新建日记");
        }

        updateSelectedTimeText(); // 更新显示选中时间
    }

    private void loadDiaryData() {
        DiaryEntry entry = dbHelper.getDiaryEntryById(diaryId);
        if (entry != null) {
            etTitle.setText(entry.getTitle());
            etContent.setText(entry.getContent());
            dateMillis = entry.getDate().getTime();
            calendar.setTimeInMillis(dateMillis);
        }
        updateSelectedTimeText(); // 加载时更新时间显示
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveDiary());
        btnCancel.setOnClickListener(v -> finish());
        btnPickDate.setOnClickListener(v -> showDatePickerDialog());
        btnPickTime.setOnClickListener(v -> showTimePickerDialog());
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

    private void showDatePickerDialog() {
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateMillis = calendar.getTimeInMillis();
                    Toast.makeText(this, "日期设为：" + (month + 1) + "月" + dayOfMonth + "日", Toast.LENGTH_SHORT).show();
                    updateSelectedTimeText();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePickerDialog() {
        new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    dateMillis = calendar.getTimeInMillis();
                    Toast.makeText(this, "时间设为：" + String.format("%02d:%02d", hourOfDay, minute), Toast.LENGTH_SHORT).show();
                    updateSelectedTimeText();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    // 显示选中的时间到 TextView
    private void updateSelectedTimeText() {
        String text = "提醒时间：" + android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", calendar);
        tvSelectedTime.setText(text);
    }
}

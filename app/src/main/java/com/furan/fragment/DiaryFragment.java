package com.furan.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furan.R;
import com.furan.activity.DiaryEditorActivity;
import com.furan.adapter.DiaryAdapter;
import com.furan.database.DiaryDatabaseHelper;
import com.furan.model.DiaryEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryFragment extends Fragment {

    private TextView tvCurrentDate;
    private ImageButton btnPrevDate, btnNextDate;
    private RecyclerView rvDiaryEntries;
    private FloatingActionButton fabAddDiary;

    private DiaryAdapter adapter;
    private DiaryDatabaseHelper dbHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DiaryDatabaseHelper(getContext());
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        updateDateDisplay();
        loadDiaryEntries();
    }

    private void initViews(View view) {
        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        btnPrevDate = view.findViewById(R.id.btn_prev_date);
        btnNextDate = view.findViewById(R.id.btn_next_date);
        rvDiaryEntries = view.findViewById(R.id.rv_diary_entries);
        fabAddDiary = view.findViewById(R.id.fab_add_diary);
    }

    private void setupRecyclerView() {
        adapter = new DiaryAdapter();
        rvDiaryEntries.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDiaryEntries.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnPrevDate.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateDateDisplay();
            loadDiaryEntries();
        });

        btnNextDate.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            updateDateDisplay();
            loadDiaryEntries();
        });

        tvCurrentDate.setOnClickListener(v -> showDatePickerDialog());

        fabAddDiary.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DiaryEditorActivity.class);
            intent.putExtra("date", calendar.getTimeInMillis());
            startActivity(intent);
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                    loadDiaryEntries();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        tvCurrentDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void loadDiaryEntries() {
        List<DiaryEntry> entries = dbHelper.getDiaryEntriesByDate(calendar.getTime());
        adapter.updateData(entries);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDiaryEntries();
    }
}

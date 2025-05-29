package com.furan.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furan.R;
import com.furan.activity.DiaryEditorActivity;
import com.furan.database.DiaryDatabaseHelper;
import com.furan.model.DiaryEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private List<DiaryEntry> diaryList = new ArrayList<>();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Context context;
    private DiaryDatabaseHelper dbHelper;

    public void updateData(List<DiaryEntry> newData) {
        diaryList.clear();
        diaryList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        dbHelper = new DiaryDatabaseHelper(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_diary_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryEntry entry = diaryList.get(position);
        holder.tvTitle.setText(entry.getTitle());
        holder.tvContent.setText(entry.getContent());
        holder.tvTime.setText(timeFormat.format(entry.getDate()));

        // 点击进入编辑页面
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DiaryEditorActivity.class);
            intent.putExtra("diary_id", entry.getId());
            context.startActivity(intent);
        });

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("确认删除")
                    .setMessage("确定要删除这条日记吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        dbHelper.deleteDiaryEntry(entry.getId());
                        diaryList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, diaryList.size());
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

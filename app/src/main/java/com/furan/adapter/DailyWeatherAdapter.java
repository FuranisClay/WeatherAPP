package com.furan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.furan.R;
import com.furan.activity.WeatherDetailActivity;
import com.furan.model.DailyWeatherData;
import java.util.ArrayList;
import java.util.List;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {

    private List<DailyWeatherData> dailyList = new ArrayList<>();

    public void updateData(List<DailyWeatherData> newData) {
        dailyList.clear();
        dailyList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyWeatherData data = dailyList.get(position);
        holder.tvDate.setText(data.getDate());
        holder.tvWeek.setText(data.getWeek());
        holder.ivWeatherIcon.setImageResource(data.getIconRes());
        holder.tvWeatherDesc.setText(data.getDescription());
        holder.tvTemperatureRange.setText(data.getTempMin() + " / " + data.getTempMax());
        // 点击箭头跳转
        // 点击箭头跳转详情
        holder.ivArrow.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, WeatherDetailActivity.class);
            intent.putExtra("date", data.getDate());
            intent.putExtra("week", data.getWeek());
            intent.putExtra("iconRes", data.getIconRes());
            intent.putExtra("description", data.getDescription());
            intent.putExtra("tempRange", data.getTempMin() + " / " + data.getTempMax());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return dailyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvWeek, tvWeatherDesc, tvTemperatureRange;
        ImageView ivWeatherIcon, ivArrow;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvWeek = itemView.findViewById(R.id.tv_week);
            tvWeatherDesc = itemView.findViewById(R.id.tv_weather_desc);
            tvTemperatureRange = itemView.findViewById(R.id.tv_temperature_range);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);
            ivArrow = itemView.findViewById(R.id.iv_arrow);
        }
    }
}
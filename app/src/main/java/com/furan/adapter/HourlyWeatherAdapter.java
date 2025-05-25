package com.furan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.furan.R;
import com.furan.model.HourlyWeatherData;
import java.util.ArrayList;
import java.util.List;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {

    private List<HourlyWeatherData> hourlyList = new ArrayList<>();

    public void updateData(List<HourlyWeatherData> newData) {
        hourlyList.clear();
        hourlyList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyWeatherData data = hourlyList.get(position);
        holder.tvTime.setText(data.getTime());
        holder.ivWeatherIcon.setImageResource(data.getIconRes());
        holder.tvTemperature.setText(data.getTemperature());
        holder.tvHumidity.setText(data.getHumidity());
    }

    @Override
    public int getItemCount() {
        return hourlyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvTemperature, tvHumidity;
        ImageView ivWeatherIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvHumidity = itemView.findViewById(R.id.tv_humidity);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);
        }
    }
}

package com.furan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.furan.R;
import com.furan.model.WeatherDetail;
import java.util.ArrayList;
import java.util.List;

public class WeatherDetailAdapter extends RecyclerView.Adapter<WeatherDetailAdapter.ViewHolder> {

    private List<WeatherDetail> detailList = new ArrayList<>();

    public void updateData(List<WeatherDetail> newData) {
        detailList.clear();
        detailList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherDetail detail = detailList.get(position);
        holder.tvName.setText(detail.getName());
        holder.tvValue.setText(detail.getValue());
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_detail_name);
            tvValue = itemView.findViewById(R.id.tv_detail_value);
        }
    }
}
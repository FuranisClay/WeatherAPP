package com.furan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.furan.R;
import com.furan.model.City;
import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<City> cityList = new ArrayList<>();
    private OnCityClickListener onCityClickListener;

    public interface OnCityClickListener {
        void onCityClick(City city);
    }

    public void setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
    }

    public void updateData(List<City> newData) {
        cityList.clear();
        cityList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.tvCityName.setText(city.getName());
        holder.tvCityEnglish.setText(city.getEnglishName());

        holder.itemView.setOnClickListener(v -> {
            if (onCityClickListener != null) {
                onCityClickListener.onCityClick(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCityName, tvCityEnglish;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tv_city_name);
            tvCityEnglish = itemView.findViewById(R.id.tv_city_english);
        }
    }
}
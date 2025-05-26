package com.furan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furan.R;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<SongParcelable> playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        recyclerView = findViewById(R.id.rv_playlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        playlist = getIntent().getParcelableArrayListExtra("playlist");
        if (playlist == null) {
            playlist = new ArrayList<>();
        }

        SongsAdapter adapter = new SongsAdapter(playlist);
        recyclerView.setAdapter(adapter);
    }

    private class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

        private final ArrayList<SongParcelable> songs;

        SongsAdapter(ArrayList<SongParcelable> songs) {
            this.songs = songs;
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_song, parent, false);
            return new SongViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            SongParcelable song = songs.get(position);
            holder.tvSongTitle.setText(song.getName());
            holder.tvArtist.setText(song.getArtist());

            holder.itemView.setOnClickListener(v -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_index", position);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            });
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        class SongViewHolder extends RecyclerView.ViewHolder {
            TextView tvSongTitle, tvArtist;

            public SongViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSongTitle = itemView.findViewById(R.id.song_title);
                tvArtist = itemView.findViewById(R.id.artist_name);
            }
        }
    }

    public static class SongParcelable implements Parcelable {
        private final String name;
        private final String artist;
        private final String filePath;

        public SongParcelable(String name, String artist, String filePath) {
            this.name = name;
            this.artist = artist;
            this.filePath = filePath;
        }

        protected SongParcelable(android.os.Parcel in) {
            name = in.readString();
            artist = in.readString();
            filePath = in.readString();
        }

        public static final Creator<SongParcelable> CREATOR = new Creator<SongParcelable>() {
            @Override
            public SongParcelable createFromParcel(android.os.Parcel in) {
                return new SongParcelable(in);
            }

            @Override
            public SongParcelable[] newArray(int size) {
                return new SongParcelable[size];
            }
        };

        public String getName() {
            return name;
        }

        public String getArtist() {
            return artist;
        }

        public String getFilePath() {
            return filePath;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull android.os.Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(artist);
            dest.writeString(filePath);
        }
    }
}

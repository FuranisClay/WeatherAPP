package com.furan.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.furan.R;
import com.furan.activity.PlaylistActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicPlayerFragment extends Fragment {

    private ImageView ivAlbumCover;
    private TextView tvSongTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private ImageButton btnPrevious, btnPlayPause, btnNext, btnPlaylist;
    private ImageButton btnFastForward, btnRewind;

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    private List<Song> playlist = new ArrayList<>();
    private int currentSongIndex = 0;
    private boolean isPlaying = false;
    private boolean isUserSeeking = false;

    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying && !isUserSeeking) {
                int currentPos = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPos);
                tvCurrentTime.setText(formatTime(currentPos));
                handler.postDelayed(this, 500);
            }
        }
    };

    // 注册启动播放列表页面的Launcher，监听返回结果
    private final ActivityResultLauncher<Intent> playlistLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    int selectedIndex = result.getData().getIntExtra("selected_index", -1);
                    if (selectedIndex >= 0 && selectedIndex < playlist.size()) {
                        currentSongIndex = selectedIndex;
                        try {
                            loadCurrentSong();
                            playMusic();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "加载歌曲失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivAlbumCover = view.findViewById(R.id.album_cover);
        tvSongTitle = view.findViewById(R.id.song_title);
        tvArtist = view.findViewById(R.id.artist_name);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        seekBar = view.findViewById(R.id.seek_bar);

        btnPrevious = view.findViewById(R.id.btn_previous);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnNext = view.findViewById(R.id.btn_next);
        btnPlaylist = view.findViewById(R.id.btn_playlist);

        btnFastForward = view.findViewById(R.id.btn_fast_forward);
        btnRewind = view.findViewById(R.id.btn_rewind);

        btnPlaylist.setOnClickListener(v -> openPlaylistActivity());

        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });

        btnNext.setOnClickListener(v -> {
            try {
                nextSong();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "切换下一首失败", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            try {
                previousSong();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "切换上一首失败", Toast.LENGTH_SHORT).show();
            }
        });

        btnFastForward.setOnClickListener(v -> fastForward());

        btnRewind.setOnClickListener(v -> rewind());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
                handler.removeCallbacks(updateSeekBarRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
                if (isPlaying) {
                    handler.post(updateSeekBarRunnable);
                }
            }
        });

        // 初始先加载默认歌曲，防止界面无歌可播
        loadDefaultSongs();
        try {
            loadCurrentSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSongsFromSettings();
    }

    private void loadSongsFromSettings() {
        String folderUriString = requireContext()
                .getSharedPreferences("music_prefs", Activity.MODE_PRIVATE)
                .getString("music_folder_uri", null);
        if (folderUriString == null) {
            Toast.makeText(getContext(), "请先在设置中选择音乐文件夹", Toast.LENGTH_SHORT).show();
            // 仍加载默认歌曲
            loadDefaultSongs();
            try {
                loadCurrentSong();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        Uri folderUri = Uri.parse(folderUriString);
        DocumentFile folder = DocumentFile.fromTreeUri(requireContext(), folderUri);
        if (folder == null || !folder.isDirectory()) {
            Toast.makeText(getContext(), "无效的音乐文件夹路径", Toast.LENGTH_SHORT).show();
            loadDefaultSongs();
            try {
                loadCurrentSong();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        List<Song> loadedSongs = new ArrayList<>();
        for (DocumentFile file : folder.listFiles()) {
            if (file.isFile() && file.canRead()) {
                String name = file.getName();
                if (name != null && (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".m4a"))) {
                    loadedSongs.add(new Song(name, "未知艺术家", R.drawable.default_album_cover, file.getUri().toString()));
                }
            }
        }

        if (loadedSongs.isEmpty()) {
            Toast.makeText(getContext(), "选定文件夹内无音乐文件", Toast.LENGTH_SHORT).show();
            loadDefaultSongs();
        } else {
            playlist.clear();
            playlist.addAll(loadedSongs);
            currentSongIndex = 0;
        }

        try {
            loadCurrentSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPlaylistActivity() {
        if (playlist.isEmpty()) {
            Toast.makeText(getContext(), "播放列表为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), PlaylistActivity.class);

        ArrayList<PlaylistActivity.SongParcelable> songsParcelable = new ArrayList<>();
        for (Song song : playlist) {
            songsParcelable.add(new PlaylistActivity.SongParcelable(song.getName(), song.getArtist(),  song.getFilePath()));
        }
        intent.putParcelableArrayListExtra("playlist", songsParcelable);

        // 用注册的launcher启动，等待结果回调
        playlistLauncher.launch(intent);
    }

    private void loadDefaultSongs() {
        playlist.clear();
        playlist.add(new Song("默认歌曲", "默认艺术家", R.drawable.default_album_cover,
                "android.resource://" + requireContext().getPackageName() + "/" + R.raw.sample_music));
    }

    private void loadCurrentSong() throws IOException {
        if (playlist.isEmpty()) return;
        Song song = playlist.get(currentSongIndex);
        tvSongTitle.setText(song.getName());
        tvArtist.setText(song.getArtist());

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(requireContext(), Uri.parse(song.getFilePath()));

            byte[] artBytes = mmr.getEmbeddedPicture();
            if (artBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
                ivAlbumCover.setImageBitmap(bitmap);
            } else {
                ivAlbumCover.setImageResource(song.getAlbumCoverResId());
            }

            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (durationStr != null) {
                int duration = Integer.parseInt(durationStr);
                tvTotalTime.setText(formatTime(duration));
                seekBar.setMax(duration);
            } else {
                tvTotalTime.setText("0:00");
                seekBar.setMax(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ivAlbumCover.setImageResource(song.getAlbumCoverResId());
            tvTotalTime.setText("0:00");
            seekBar.setMax(0);
        } finally {
            mmr.release();
        }

        tvCurrentTime.setText("0:00");
        seekBar.setProgress(0);

        handler.removeCallbacks(updateSeekBarRunnable);
    }

    private void playMusic() {
        if (playlist.isEmpty()) {
            Toast.makeText(getContext(), "播放列表为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer != null) {
                releaseMediaPlayer();
            }

            Song currentSong = playlist.get(currentSongIndex);
            mediaPlayer = new MediaPlayer();

            Uri uri = Uri.parse(currentSong.getFilePath());
            mediaPlayer.setDataSource(requireContext(), uri);
            mediaPlayer.prepare();

            mediaPlayer.setOnCompletionListener(mp -> {
                try {
                    nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            handler.post(updateSeekBarRunnable);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "无法播放音乐", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private void nextSong() throws IOException {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        loadCurrentSong();
        playMusic();
    }

    private void previousSong() throws IOException {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        loadCurrentSong();
        playMusic();
    }

    private void fastForward() {
        if (mediaPlayer != null) {
            int newPos = mediaPlayer.getCurrentPosition() + 10000;
            if (newPos > mediaPlayer.getDuration()) newPos = mediaPlayer.getDuration();
            mediaPlayer.seekTo(newPos);
            seekBar.setProgress(newPos);
            tvCurrentTime.setText(formatTime(newPos));
        }
    }

    private void rewind() {
        if (mediaPlayer != null) {
            int newPos = mediaPlayer.getCurrentPosition() - 10000;
            if (newPos < 0) newPos = 0;
            mediaPlayer.seekTo(newPos);
            seekBar.setProgress(newPos);
            tvCurrentTime.setText(formatTime(newPos));
        }
    }

    private String formatTime(int milliseconds) {
        int totalSeconds = milliseconds / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
        handler.removeCallbacks(updateSeekBarRunnable);
        btnPlayPause.setImageResource(R.drawable.ic_play);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseMediaPlayer();
    }

    // 内部Song类，方便管理
    private static class Song {
        private final String name;
        private final String artist;
        private final int albumCoverResId;
        private final String filePath;

        public Song(String name, String artist, int albumCoverResId, String filePath) {
            this.name = name;
            this.artist = artist;
            this.albumCoverResId = albumCoverResId;
            this.filePath = filePath;
        }

        public String getName() {
            return name;
        }

        public String getArtist() {
            return artist;
        }

        public int getAlbumCoverResId() {
            return albumCoverResId;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}

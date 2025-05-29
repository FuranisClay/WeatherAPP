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
import com.furan.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MusicPlayerFragment extends Fragment {

    private ImageView ivAlbumCover;
    private TextView tvSongTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private ImageButton btnPrevious, btnPlayPause, btnNext, btnPlaylist;
    private ImageButton btnFastForward, btnRewind;
    private ImageButton btnShuffle;  // 新增随机按钮

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    private List<Song> playlist = new ArrayList<>();
    private int currentSongIndex = 0;
    private boolean isPlaying = false;
    private boolean isUserSeeking = false;

    private boolean isShuffle = false;  // 是否随机播放标志
    private Random random = new Random();

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

        btnShuffle = view.findViewById(R.id.btn_shuffle); // 获取随机按钮控件

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

        // 随机按钮点击事件，切换随机状态并更改图标
        btnShuffle.setOnClickListener(v -> {
            isShuffle = !isShuffle;
            if (isShuffle) {
                btnShuffle.setImageResource(R.drawable.ic_shuffle); // TODO需要准备随机开启图标资源
                Toast.makeText(getContext(), "随机播放已开启", Toast.LENGTH_SHORT).show();
            } else {
                btnShuffle.setImageResource(R.drawable.ic_shuffle); // 随机关闭图标（原图标）
                Toast.makeText(getContext(), "随机播放已关闭", Toast.LENGTH_SHORT).show();
            }
        });

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

        try
        {
            loadCurrentSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadDefaultSongs() {
        playlist.clear();
        playlist.add(new Song("默认歌曲1", "默认艺术家", R.drawable.default_album_cover, null));
    }

    private void openPlaylistActivity() {
        Intent intent = new Intent(getContext(), PlaylistActivity.class);
        ArrayList<String> titles = new ArrayList<>();
        for (Song song : playlist) {
            titles.add(song.getTitle());
        }
        intent.putStringArrayListExtra("playlist_titles", titles);
        playlistLauncher.launch(intent);
    }

    private void loadCurrentSong() throws IOException {
        if (playlist.isEmpty()) return;
        Song song = playlist.get(currentSongIndex);
        tvSongTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        ivAlbumCover.setImageResource(song.getAlbumCoverResId());
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        if (song.getUri() != null) {
            mediaPlayer.setDataSource(getContext(), Uri.parse(song.getUri()));
        }
        mediaPlayer.prepare();
        seekBar.setMax(mediaPlayer.getDuration());
        tvTotalTime.setText(formatTime(mediaPlayer.getDuration()));

        mediaPlayer.setOnCompletionListener(mp -> {
            try {
                nextSong();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            handler.post(updateSeekBarRunnable);
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private void nextSong() throws IOException {
        if (playlist.isEmpty()) return;
        if (isShuffle) {
            currentSongIndex = random.nextInt(playlist.size());
        } else {
            currentSongIndex = (currentSongIndex + 1) % playlist.size();
        }
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
            int currentPos = mediaPlayer.getCurrentPosition();
            int newPos = Math.min(currentPos + 5000, mediaPlayer.getDuration());
            mediaPlayer.seekTo(newPos);
        }
    }

    private void rewind() {
        if (mediaPlayer != null) {
            int currentPos = mediaPlayer.getCurrentPosition();
            int newPos = Math.max(currentPos - 5000, 0);
            mediaPlayer.seekTo(newPos);
        }
    }

    private String formatTime(int milliseconds) {
        int minutes = milliseconds / 1000 / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    }

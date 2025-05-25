package com.furan.fragment;

import android.media.MediaPlayer;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.furan.R;
import com.furan.model.Song;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerFragment extends Fragment {

    private ImageView ivAlbumCover;
    private TextView tvSongTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private ImageButton btnPlayPause, btnPrevious, btnNext, btnRewind, btnFastForward;
    private ImageButton btnPlaylist, btnRepeat, btnShuffle;

    private MediaPlayer mediaPlayer;
    private List<Song> playlist;
    private int currentSongIndex = 0;
    private boolean isPlaying = false;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initPlaylist();
        setupClickListeners();
        loadCurrentSong();
    }

    private void initViews(View view) {
//        ivAlbumCover = view.findViewById(R.id.iv_album_cover);
//        tvSongTitle = view.findViewById(R.id.tv_song_title);
//        tvArtist = view.findViewById(R.id.tv_artist);
//        tvCurrentTime = view.findViewById(R.id.tv_current_time);
//        tvTotalTime = view.findViewById(R.id.tv_total_time);
//        seekBar = view.findViewById(R.id.seek_bar);
//
//        btnPlayPause = view.findViewById(R.id.btn_play_pause);
//        btnPrevious = view.findViewById(R.id.btn_previous);
//        btnNext = view.findViewById(R.id.btn_next);
//        btnRewind = view.findViewById(R.id.btn_rewind);
//        btnFastForward = view.findViewById(R.id.btn_fast_forward);
//        btnPlaylist = view.findViewById(R.id.btn_playlist);
//        btnRepeat = view.findViewById(R.id.btn_repeat);
//        btnShuffle = view.findViewById(R.id.btn_shuffle);
    }

    private void initPlaylist() {
        playlist = new ArrayList<>();
//        playlist.add(new Song("歌曲1", "艺术家1", R.drawable.default_album_cover, ""));
//        playlist.add(new Song("歌曲2", "艺术家2", R.drawable.default_album_cover, ""));
//        playlist.add(new Song("歌曲3", "艺术家3", R.drawable.default_album_cover, ""));
    }

    private void setupClickListeners() {
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPrevious.setOnClickListener(v -> previousSong());
        btnNext.setOnClickListener(v -> nextSong());
        btnRewind.setOnClickListener(v -> rewind());
        btnFastForward.setOnClickListener(v -> fastForward());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadCurrentSong() {
        if (playlist.isEmpty()) return;

        Song currentSong = playlist.get(currentSongIndex);
        tvSongTitle.setText(currentSong.getTitle());
        tvArtist.setText(currentSong.getArtist());
        ivAlbumCover.setImageResource(currentSong.getAlbumCoverRes());
    }

    private void togglePlayPause() {
        if (isPlaying) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    private void playMusic() {
        try {
            if (mediaPlayer == null) {
                // 这里应该加载实际的音频文件
//                mediaPlayer = MediaPlayer.create(getContext(), R.raw.sample_music);
                mediaPlayer.setOnCompletionListener(mp -> nextSong());
            }

            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            updateSeekBar();
        } catch (Exception e) {
            Toast.makeText(getContext(), "播放失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void previousSong() {
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        loadCurrentSong();
        if (isPlaying) {
            stopMusic();
            playMusic();
        }
    }

    private void nextSong() {
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        loadCurrentSong();
        if (isPlaying) {
            stopMusic();
            playMusic();
        }
    }

    private void rewind() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int newPosition = Math.max(0, currentPosition - 10000); // 后退10秒
            mediaPlayer.seekTo(newPosition);
        }
    }

    private void fastForward() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            int newPosition = Math.min(duration, currentPosition + 10000); // 前进10秒
            mediaPlayer.seekTo(newPosition);
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play);
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && isPlaying) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();

            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);

            tvCurrentTime.setText(formatTime(currentPosition));
            tvTotalTime.setText(formatTime(duration));

            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
        handler.removeCallbacksAndMessages(null);
    }
}

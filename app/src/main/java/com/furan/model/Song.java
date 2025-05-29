package com.furan.model;

public class Song {
    private String title;
    private String artist;
    private int albumCoverResId;  // 专辑封面资源 ID
    private String uri;           // 歌曲文件的 URI 字符串

    public Song(String title, String artist, int albumCoverResId, String uri) {
        this.title = title;
        this.artist = artist;
        this.albumCoverResId = albumCoverResId;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getAlbumCoverResId() {
        return albumCoverResId;
    }

    public String getUri() {
        return uri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbumCoverResId(int albumCoverResId) {
        this.albumCoverResId = albumCoverResId;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
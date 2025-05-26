package com.furan.model;

public class Song {
    private String title;
    private String artist;
    private int albumCoverRes;
    private String filePath;

    public Song(String title, String artist, int albumCoverRes, String filePath) {
        this.title = title;
        this.artist = artist;
        this.albumCoverRes = albumCoverRes;
        this.filePath = filePath;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getAlbumCoverRes() { return albumCoverRes; }
    public String getFilePath() { return filePath; }
}

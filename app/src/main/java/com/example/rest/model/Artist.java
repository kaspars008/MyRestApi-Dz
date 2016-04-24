package com.example.rest.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
//Класс позволяющий получить информацию про одного певца из списка
// implements Parcelable нужный для передачи в Intent в новом активити CurrentArtistActivity
public class Artist implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String[] genres;
    private int tracks;
    private int albums;
    private String link;
    private HashMap<String, String> cover;

    public Artist() { }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {

        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    protected Artist(Parcel in) {
        name = in.readString();
        id = in.readInt();
        description = in.readString();
        genres = in.createStringArray();
        tracks = in.readInt();
        albums = in.readInt();
        link = in.readString();
        cover = in.readHashMap(Artist.class.getClassLoader());
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public String[] getGenres() {return genres;}
    public int getTracks() {return tracks;}
    public int getAlbums() {return albums;}
    public String getLink() {return link;}
    public String getDecription() {return description;}
    public HashMap<String, String> getCover() {return cover;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeInt(id);
        parcel.writeString(description);
        parcel.writeStringArray(genres);
        parcel.writeInt(tracks);
        parcel.writeInt(albums);
        parcel.writeString(link);
        parcel.writeMap(cover);
    }
}
package com.example.rest.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;

import com.example.rest.R;
import com.example.rest.model.Artist;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by каспарс on 06.04.2016.
 */
// в этом классе изпользуется методика адаптера для RecyclerView с соответствующими override методами

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.Holder> implements View.OnClickListener {
    private final OnArtistClickedListener listener;
    private List<Artist> artistList; //список певцов который из ArtistActivity с setArtistList передается в ArtistAdapter

    public ArtistAdapter(OnArtistClickedListener listener) {
         this.listener = listener;
     }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_artist, parent, false);
        row.setOnClickListener(this); //добавляем слушатель при нажатии на соответствующего певца
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        //здесь вставляем значения певцов в соответствующие textViews из хранилища holder
        Artist currentArtist = artistList.get(position);
        holder.genres.setText(Arrays.toString(currentArtist.getGenres()).replaceAll("\\[|\\]", ""));
        holder.name.setText(currentArtist.getName());
        holder.numberAlbums.setText(Integer.toString(currentArtist.getAlbums()) + " альбомов, " + currentArtist.getTracks() + " песен");

        // загружаем картинку певца в ImageView
        Picasso.with(holder.itemView.getContext()).load(currentArtist.getCover().get("small"))
                .error(R.drawable.ic_error).placeholder(R.drawable.progress_animation )
                .fit().centerInside().into(holder.image);
        holder.itemView.setTag(currentArtist);
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    @Override
    public void onClick(View row) {
        if (row.getTag() instanceof Artist) {
            Artist artist = (Artist) row.getTag();
            listener.onArtistClicked(artist);
        }
    }
    //в Holder храним views входящие в строки (rows) кажлого певца
    public class Holder extends RecyclerView.ViewHolder {
        public TextView name, genres, numberAlbums;
        public ImageView image;

        public Holder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.artistImage);
            name = (TextView) itemView.findViewById(R.id.artistName);
            genres = (TextView) itemView.findViewById(R.id.artistCategory);
            numberAlbums = (TextView) itemView.findViewById(R.id.artistNumberAlbums);
        }
    }

    public void setArtistList(List<Artist> artists) {
        artistList = new ArrayList<>(artists);
    }

    public void removeItem(int position) {
        artistList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(int position, Artist artist) {
        artistList.add(position, artist);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Artist artist = artistList.remove(fromPosition);
        artistList.add(toPosition, artist);
        notifyItemMoved(fromPosition, toPosition);
    }

    // отбыраем введенный в поиске текст и обновляем список и адаптер
    public void animateTo(List<Artist> artists) {
        applyAndAnimateRemovals(artists);
        applyAndAnimateAdditions(artists);
        applyAndAnimateMovedItems(artists);
    }
// убырает из списка в соответствии с отфильтрованным списком по query в searchView.
    private void applyAndAnimateRemovals(List<Artist> newArtists) {
        for (int i = artistList.size() - 1; i >= 0; i--) {
            Artist artist = artistList.get(i);
            if (!newArtists.contains(artist)) {
                removeItem(i);
            }
        }
    }
    // добавляет в список в соответствии с отфильтрованным списком по query в searchView. При стирании букв в searchView query
    private void applyAndAnimateAdditions(List<Artist> newArtists) {
        for (int i = 0, count = newArtists.size(); i < count; i++) {
            final Artist artist = newArtists.get(i);
            if (!artistList.contains(artist)) {
                addItem(i, artist);
            }
        }
    }
    // упорядочивает выводимый список при поиске в соответствии с отфильтрованным списком по query в searchView
    private void applyAndAnimateMovedItems(List<Artist> newArtists) {
        for (int toPosition = newArtists.size() - 1; toPosition >= 0; toPosition--) {
            final Artist artist = newArtists.get(toPosition);
            final int fromPosition = artistList.indexOf(artist);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
    // интерфейс обрабатывающий слушатель нажатия на певца в ArtistActivity и передачи его информации artist в ArtistsActivity
    public interface OnArtistClickedListener {
        void onArtistClicked(Artist artist);
    }
}
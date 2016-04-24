package com.example.rest.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rest.R;
import com.example.rest.model.Artist;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import java.util.Arrays;

// Активити в которую переходим после нажатия в списке певцов в главном активити
public class CurrentArtistActivity extends BaseActivity {
    public static final String ARTIST_DATA = "ARTIST_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_artist);

        // получаем интент и находим актуальный обьект Artist (переданный при нажатии в предыдущем активити)
        final Artist artist = (Artist) getIntent().getExtras().getParcelable(ARTIST_DATA);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(artist.getName());

        // находим элементы (Views) Layout в которых будем вставлять информацию певца
        ImageView image = (ImageView) findViewById(R.id.activity_current_artist_image);
        TextView numbers = (TextView) findViewById(R.id.activity_current_artist_numbers);
        TextView description = (TextView) findViewById(R.id.activity_current_artist_description);
        TextView genre = (TextView) findViewById(R.id.activity_current_artist_genre);

        // загружаем картинку певца в ImageView
        Picasso.with(this).load(artist.getCover().get("big")).error(R.drawable.ic_error)
                .placeholder(R.drawable.progress_animation)
                .fit().centerInside().into(image);

        // вставляем информацию певца в TextViews в актуальном активити
        numbers.setText(Integer.toString(artist.getAlbums()) + " альбомов, " + artist.getTracks() + " песен");
        description.setText(artist.getDecription());
        genre.setText(Arrays.toString(artist.getGenres()).replaceAll("\\[|\\]", ""));

        image.setOnClickListener(new View.OnClickListener() { //слушатель для картинки
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.activity_current_artist_image) {
                    openBrowser(artist.getLink()); // открывает домашний сайт певца если имеется сылка после нажатии картинки
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // нажимая иконку назад закривает актуальное активити
        {
                finish();
            // анимация fade-out, fade-in, slide при закритии активити
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    public void openBrowser(String link) {
      if (link != null){
        if (!link.startsWith("http://") && !link.startsWith("https://"))
            link = "http://" + link;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent); } else Toast.makeText(this, "Homepage isn't available", Toast.LENGTH_SHORT).show();
    }
}
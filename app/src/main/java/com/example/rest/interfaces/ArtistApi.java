package com.example.rest.interfaces;

/**
 * Created by каспарс on 07.04.2016.
 */
import com.example.rest.model.Artist;
import com.example.rest.model.Constants;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface ArtistApi {
    @GET(Constants.JSON_URL)
    public void getFeed(Callback<List<Artist>> response);
}

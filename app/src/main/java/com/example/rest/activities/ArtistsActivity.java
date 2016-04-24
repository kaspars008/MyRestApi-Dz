package com.example.rest.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.example.rest.adapters.ArtistAdapter;
import com.example.rest.interfaces.ArtistApi;
import com.example.rest.R;
import com.example.rest.model.Constants;
import com.example.rest.model.Artist;
import java.util.ArrayList;
import java.util.List;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ArtistsActivity extends BaseActivity implements ArtistAdapter.OnArtistClickedListener {
    private RecyclerView myRecyclerView; // View для нашего списка певцов
    private SearchView searchView; // поисковик для нахождения певцов из списка
    private String searchQuery = null; // текст который мы вписали в окно поиска; при запуске приложения текста нет = null;
    private boolean isActionViewExpanded = false; // определяет открыто ли окно поиска; при запуске приложения он закрыт = false
    private ArtistAdapter myArtistAdapter;// адаптер для myRecyclerView
    private List<Artist> artistList; // в этом списке хранится список про всех певцов полученных их Json
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Artists");

        configViews(); //конфигурация myRecyclerView
        // проверяет подключен ли интернет

        if (isOnline()) {
            showProgressDialog(true);
            requestData();
            showProgressDialog(false);
            Log.d("myLogs","stop");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
    }
    // сохраняет в Bundle до поворота экрана какой текст введен в поисковик searchView и открито или закрыто
    // окно поиска isActionViewExpanded = true, false. После поворота эти данные будут доступны в searchQuery и isActionViewExpanded
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query_key", searchView.getQuery().toString());
        outState.putBoolean("searchview_expanded_key", isActionViewExpanded);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchQuery = savedInstanceState.getString("query_key");
        isActionViewExpanded = savedInstanceState.getBoolean("searchview_expanded_key");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        configSearchView(item);//конфигурирует поисковик searchView
        return true;
    }
    //конфигурация myRecyclerView
    private void configViews() {
        myRecyclerView = (RecyclerView) findViewById(R.id.list);
        myRecyclerView.setHasFixedSize(true);

        if (isTablet) {
            myRecyclerView.setLayoutManager(new GridLayoutManager(ArtistsActivity.this, 2));
        } else {
            myRecyclerView.setLayoutManager(new LinearLayoutManager(ArtistsActivity.this));
        }
    }
    //конфигурирует поисковик searchView
    private void configSearchView(MenuItem item) {
        searchView = (SearchView) MenuItemCompat.getActionView(item);

        //слушатель чтобы определить открыто ли окно поиска. Важно чтобы после поворота окна экрана
        // поисковик остался в такой же позиции (открытой или закрытой)
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // isActionViewExpanded = true, если окно поиска открыто
                        isActionViewExpanded = true;
                        return true;
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        // isActionViewExpanded = false, если окно поиска закрыто
                        isActionViewExpanded = false;
                        return true;
                    }
                });

        //открывает окно поиска если до поворота экрана был открыт и вставляет внем текст запроса до поворота сохряненный searchQuery
        if (searchQuery != null && isActionViewExpanded) {
            item.expandActionView();
            searchView.setQuery(searchQuery, false);
        }
    }
    //получаем  список певцов из JSON
    private void requestData() {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        ArtistApi api = adapter.create(ArtistApi.class);

        api.getFeed(new Callback<List<Artist>>() {

            @Override
            public void success(List<Artist> arg0, Response arg1) {
                artistList = arg0; //присваиваем полученный список из JSON
                updateDisplay(); //обновляет список полученной из artistList

                // добавляем к searchView слушатель который обрабатывает веденный запрос query в окно поиска
                searchView.setOnQueryTextListener(new QueryListener());
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO Auto-generated method stub
                Log.d("myLogs", "Error :: " + error.getMessage());
            }
        });
    }
    // обнавляет список с полученными данными artistList певцов и добавляет адаптер
    protected void updateDisplay() {
        myArtistAdapter = new ArtistAdapter(this);
        myArtistAdapter.setArtistList(artistList); //передает в адаптер artistList
        myRecyclerView.setAdapter(myArtistAdapter);

        //если перед поворотом окна было открыто окно поиска (isActionViewExpanded = true) то отбыраем введенный в поиске searchQuery
        if (searchQuery != null && isActionViewExpanded) {
            List<Artist> filteredModelList = filter(artistList, searchQuery);
            myArtistAdapter.animateTo(filteredModelList);}
    }
    // проверяет подключен ли интернет
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    // фильтр отбырает из списка то что соответствует введенному query в окно поиска
    private List<Artist> filter(List<Artist> aList, String query) {

        query = query.toLowerCase();
        final List<Artist> filteredList = new ArrayList<>();
        for (int i = 0; i < aList.size(); i++) {
            final String text = aList.get(i).getName().toLowerCase();
            if (text.contains(query)) {
                filteredList.add(aList.get(i));
            }
        }
        return filteredList;
    }

    public void showProgressDialog(boolean isDialogShowing){

        if (isDialogShowing == true) {
            dialog=new ProgressDialog(this);
            dialog.setMessage("Loading...");
            dialog.show();
        } else if (dialog !=null){
            dialog.dismiss();
            dialog=null;}
    }

        // слушатель который обрабатывает веденный запрос query в окно поиска
    public class QueryListener implements SearchView.OnQueryTextListener {
        public QueryListener() { }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            // фильтр отбырает из списка то что соответствует введенному query в окно поиска
            List<Artist> filteredModelList = filter(artistList, query);
            // animateTo стирает все поля которые отличается от query и ставит элементы списка в правильный порядок
            myArtistAdapter.animateTo(filteredModelList);
            myRecyclerView.scrollToPosition(0);
            return true;
        }
    }
    @Override
    public void onArtistClicked(Artist artist) {
        // передача обьекта актуального певца artist после нажатии на него в Intent в новом активити CurrentArtistActivity
        Intent intent = new Intent(this, CurrentArtistActivity.class);
        intent.putExtra(CurrentArtistActivity.ARTIST_DATA, artist);
        startActivity(intent);
        // анимация fade-out, fade-in, slide при переходе в другое активити (CurrentArtistActivity)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
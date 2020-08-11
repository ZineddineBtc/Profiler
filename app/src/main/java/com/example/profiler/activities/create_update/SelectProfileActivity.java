package com.example.profiler.activities.create_update;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.profiler.R;
import com.example.profiler.adapters.SelectProfileAdapter;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.models.Profile;

import java.util.ArrayList;
import java.util.Objects;

public class SelectProfileActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    RecyclerView allProfilesRV;
    SelectProfileAdapter adapter;
    ArrayList<Profile> allProfilesList;
    ProfileDAO profileDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);

        profileDAO = new ProfileDAO(this);
        allProfilesList = profileDAO.getAllProfiles();
        adapter = new SelectProfileAdapter(this, allProfilesList);
        allProfilesRV = findViewById(R.id.allProfilesRV);
        allProfilesRV.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        allProfilesRV.setAdapter(adapter);
        setActionBarTitle("Select Profile");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.all_profiles_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchProfile);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Profile");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        // This method can be used when a query is submitted eg.
        // creating search history using SQLite DB
        Toast.makeText(getApplicationContext(), "Query Inserted", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return true;
    }
    public void setActionBarTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_profiles_blue);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> "+title+" </font>")
        );
    }
}







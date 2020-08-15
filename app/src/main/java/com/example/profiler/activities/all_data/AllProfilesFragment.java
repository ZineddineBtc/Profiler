package com.example.profiler.activities.all_data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profiler.R;
import com.example.profiler.activities.create_update.CreateUpdateProfileActivity;
import com.example.profiler.adapters.ProfilesAdapter;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.models.Profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class AllProfilesFragment extends Fragment implements SearchView.OnQueryTextListener{

    RecyclerView allProfilesRV;
    ProfilesAdapter adapter;
    ArrayList<Profile> allProfilesList;
    Context context;
    FloatingActionButton createProfileButton;
    TextView emptyListTV;
    ProfileDAO profileDAO;
    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_all_profiles, container, false);
        context = Objects.requireNonNull(getContext());

        profileDAO = new ProfileDAO(context);
        allProfilesList = profileDAO.getAllProfiles();
        adapter = new ProfilesAdapter(context, allProfilesList);
        if(allProfilesList.isEmpty()){
            emptyListTV = fragmentView.findViewById(R.id.emptyProfilesListTV);
            emptyListTV.setVisibility(View.VISIBLE);
        }else{
            setRecyclerView();
        }

        createProfileButton = fragmentView.findViewById(R.id.createProfileButton);
        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, CreateUpdateProfileActivity.class));
            }
        });



        return fragmentView;
    }

    public void setRecyclerView(){
        allProfilesRV = fragmentView.findViewById(R.id.allProfilesRV);
        allProfilesRV.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        allProfilesRV.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.all_profiles_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem searchItem = menu.findItem(R.id.searchProfile);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Profile");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
    }

    public boolean onQueryTextSubmit(String query) {
        // This method can be used when a query is submitted eg.
        // creating search history using SQLite DB
        Toast.makeText(fragmentView.getContext(), "Query Inserted", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return true;
    }










    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public AllProfilesFragment() { }
    public static AllProfilesFragment newInstance(String param1, String param2) {
        AllProfilesFragment fragment = new AllProfilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

}
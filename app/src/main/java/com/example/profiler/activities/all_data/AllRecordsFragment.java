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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profiler.StaticClass;
import com.example.profiler.R;
import com.example.profiler.activities.create_update.SelectProfileActivity;
import com.example.profiler.adapters.RecordsAdapter;
import com.example.profiler.daos.RecordDAO;
import com.example.profiler.models.Record;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class AllRecordsFragment extends Fragment implements SearchView.OnQueryTextListener {

    RecyclerView allRecordsRV;
    public static RecordsAdapter adapter;
    public static ArrayList<Record> allRecordsList;
    Context context;
    RecordDAO recordDAO;
    View fragmentView;
    TextView emptyListTV;
    FloatingActionButton createRecordButton;
    LinearLayout LL;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_all_records, container, false);

        context = Objects.requireNonNull(getContext());
        recordDAO = new RecordDAO(context);
        allRecordsList = recordDAO.getAllRecordsReversed();
        adapter = new RecordsAdapter(context, allRecordsList, StaticClass.ALL_RECORDS);
        LL = fragmentView.findViewById(R.id.LL);
        if(allRecordsList.isEmpty()){
            emptyListTV = fragmentView.findViewById(R.id.emptyRecordsListTV);
            emptyListTV.setVisibility(View.VISIBLE);
            LL.setBackgroundColor(context.getColor(R.color.white));
        }else{
            setRecyclerView();
            LL.setBackgroundColor(context.getColor(R.color.grey));
        }

        createRecordButton = fragmentView.findViewById(R.id.createRecordButton);
        createRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SelectProfileActivity.class));
            }
        });

        return fragmentView;
    }
    public void setRecyclerView(){
        allRecordsRV = fragmentView.findViewById(R.id.allRecordsRV);
        allRecordsRV.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        allRecordsRV.setAdapter(adapter);
        allRecordsRV.setDrawingCacheEnabled(true);
        allRecordsRV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.all_records_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem searchItem = menu.findItem(R.id.searchRecord);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Record");
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
    public AllRecordsFragment() { }
    public static AllRecordsFragment newInstance(String param1, String param2) {
        AllRecordsFragment fragment = new AllRecordsFragment();
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
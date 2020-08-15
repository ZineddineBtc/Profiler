
package com.example.profiler.activities.specific_data;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profiler.StaticClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.activities.create_update.CreateUpdateMyProfileRecordActivity;
import com.example.profiler.adapters.MyProfileRecordsAdapter;
import com.example.profiler.daos.MyProfileDAO;
import com.example.profiler.daos.MyProfileRecordDAO;
import com.example.profiler.models.Record;

import java.util.ArrayList;
import java.util.Objects;

public class MyProfileRecordsActivity extends AppCompatActivity {

    RecyclerView profileRecordsRV;
    MyProfileRecordsAdapter adapter;
    ArrayList<Record> profileRecordsList;
    LinearLayout shadeLL, alertLL;
    Button deleteProfileRecordsButton, cancelButton;
    MyProfileDAO myProfileDAO;
    MyProfileRecordDAO myProfileRecordDAO;
    int profileID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_records);


        shadeLL = findViewById(R.id.shadeLL);
        alertLL = findViewById(R.id.alertLL);
        deleteProfileRecordsButton = findViewById(R.id.deleteProfileRecordsButton);
        cancelButton = findViewById(R.id.cancelButton);

        myProfileDAO = new MyProfileDAO(this);
        myProfileRecordDAO = new MyProfileRecordDAO(this);
        profileID = StaticClass.myProfileID;
        profileRecordsList = myProfileRecordDAO.getProfileRecordsReversed(profileID);
        adapter = new MyProfileRecordsAdapter(this, profileRecordsList);
        profileRecordsRV = findViewById(R.id.profileRecordsRV);
        profileRecordsRV.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        profileRecordsRV.setAdapter(adapter);
        profileRecordsRV.setDrawingCacheEnabled(true);
        profileRecordsRV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        setActionBarTitle("Records of "+ myProfileDAO.getMyProfile(profileID).getName());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_records_menu, menu);
        return true;
    }

    public void showAlert(){
        shadeLL.setVisibility(View.VISIBLE);
        alertLL.setVisibility(View.VISIBLE);
    }

    public void deleteProfileRecords(View view){
        myProfileRecordDAO.deleteProfileRecords(profileID);
        startActivity(new Intent(getApplicationContext(), AllDataActivity.class)
                .putExtra(StaticClass.TO, StaticClass.My_PROFILE));
    }

    public void cancelDeletion(View view){
        shadeLL.setVisibility(View.GONE);
        alertLL.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.createRecord:
                startActivity(
                        new Intent(getApplicationContext(), CreateUpdateMyProfileRecordActivity.class)
                );
                break;
            case R.id.deleteProfileRecords:
                showAlert();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AllDataActivity.class)
        .putExtra(StaticClass.TO, StaticClass.My_PROFILE));
    }

    public void setActionBarTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> "+title+" </font>")
        );
    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}

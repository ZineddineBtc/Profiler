
package com.example.profiler.activities.specific_data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.profiler.CommonClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.activities.create_update.CreateUpdateRecordActivity;
import com.example.profiler.adapters.RecordsAdapter;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.daos.RecordDAO;
import com.example.profiler.models.Record;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileRecordsActivity extends AppCompatActivity {

    RecyclerView profileRecords;
    RecordsAdapter adapter;
    ArrayList<Record> profileRecordsList;
    LinearLayout shadeLL, alertLL;
    Button deleteProfileRecordsButton, cancelButton;
    ProfileDAO profileDAO;
    RecordDAO recordDAO;
    int profileID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_records);


        shadeLL = findViewById(R.id.shadeLL);
        alertLL = findViewById(R.id.alertLL);
        deleteProfileRecordsButton = findViewById(R.id.deleteProfileRecordsButton);
        cancelButton = findViewById(R.id.cancelButton);

        profileDAO = new ProfileDAO(this);
        recordDAO = new RecordDAO(this);
        profileID = getIntent().getIntExtra(CommonClass.PROFILE_ID, -1);
        profileRecordsList = recordDAO.getProfileRecordsReversed(profileID);
        adapter = new RecordsAdapter(this, profileRecordsList, CommonClass.PROFILE_RECORDS);
        profileRecords = findViewById(R.id.profileRecordsRV);
        profileRecords.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        profileRecords.setAdapter(adapter);

        setActionBarTitle("Records of "+profileDAO.getProfile(profileID).getName());
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
        recordDAO.deleteProfileRecords(profileID);
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                .putExtra(CommonClass.PROFILE_ID, profileID));
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
                        new Intent(getApplicationContext(), CreateUpdateRecordActivity.class)
                                .putExtra(CommonClass.PROFILE_ID, profileID)
                                .putExtra(CommonClass.FROM, CommonClass.PROFILE_RECORDS)
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
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
        .putExtra(CommonClass.PROFILE_ID, profileID));
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

package com.example.profiler.activities.specific_data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.profiler.CommonClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.activities.create_update.CreateUpdateProfileActivity;
import com.example.profiler.activities.create_update.CreateUpdateRecordActivity;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.daos.RecordDAO;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    ImageView photoIV;
    TextView nameTV, bioTV, phoneTV, birthdayTV, emailTV, addressTV, interestsTV,
            toProfileRecordsTV, relationshipStatusTV, occupationTV;
    LinearLayout shadeLL, alertLL;
    Button deleteProfileButton, cancelButton;
    ProfileDAO profileDAO;
    RecordDAO recordDAO;
    int profileID;
    boolean noRecords=false;
    String noRecordsString = "Profile has no records. Click to create one",
            recordString = "Show profile records >";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileDAO = new ProfileDAO(this);
        recordDAO = new RecordDAO(this);
        findViewsByIds();
        profileID = getIntent().getIntExtra(CommonClass.PROFILE_ID, -1);
        if(profileID != -1){
            setProfileUI();
            if(recordDAO.getProfileRecords(profileID).isEmpty()){
                toProfileRecordsTV.setText(noRecordsString);
                noRecords = true;
            }else{
                toProfileRecordsTV.setText(recordString);
                noRecords = false;
            }
        }
        setActionBarTitle("Profile of "+profileDAO.getProfile(profileID).getName());
    }

    public void findViewsByIds(){
        photoIV = findViewById(R.id.profilePhotoIV);
        nameTV = findViewById(R.id.profileNameTV);
        bioTV = findViewById(R.id.profileBioTV);
        phoneTV = findViewById(R.id.profilePhoneTV);
        birthdayTV = findViewById(R.id.profileBirthdayTV);
        emailTV = findViewById(R.id.profileEmailTV);
        addressTV = findViewById(R.id.profileAddressTV);
        interestsTV = findViewById(R.id.profileInterestsTV);
        relationshipStatusTV = findViewById(R.id.profileRelationshipStatusTV);
        occupationTV = findViewById(R.id.profileOccupationTV);
        toProfileRecordsTV = findViewById(R.id.toProfileRecordsTV);
        shadeLL = findViewById(R.id.shadeLL);
        alertLL = findViewById(R.id.alertLL);
        deleteProfileButton = findViewById(R.id.deleteProfileButton);
        cancelButton = findViewById(R.id.cancelButton);
    }
    public void setProfileUI(){
        if(profileDAO.getProfile(profileID).getPhoto() != null){
            photoIV.setImageBitmap(
                    CommonClass.stringToBitmap(profileDAO.getProfile(profileID).getPhoto()));
        }
        nameTV.setText(profileDAO.getProfile(profileID).getName());
        bioTV.setText(profileDAO.getProfile(profileID).getBio());
        phoneTV.setText(profileDAO.getProfile(profileID).getPhone());
        birthdayTV.setText(profileDAO.getProfile(profileID).getBirthday());
        emailTV.setText(profileDAO.getProfile(profileID).getEmail());
        addressTV.setText(profileDAO.getProfile(profileID).getAddress());
        interestsTV.setText(profileDAO.getProfile(profileID).getInterests());
        relationshipStatusTV.setText(profileDAO.getProfile(profileID).getRelationshipStatus());
        occupationTV.setText(profileDAO.getProfile(profileID).getOccupation());
    }

    public void showAlert(){
        shadeLL.setVisibility(View.VISIBLE);
        alertLL.setVisibility(View.VISIBLE);
    }

    public void deleteProfile(View view){
        recordDAO.deleteProfileRecords(profileID);
        profileDAO.deleteProfile(profileID);
        startActivity(new Intent(getApplicationContext(), AllDataActivity.class));
    }

    public void cancelDeletion(View view){
        shadeLL.setVisibility(View.GONE);
        alertLL.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.updateProfile:
                startActivity(
                        new Intent(getApplicationContext(), CreateUpdateProfileActivity.class)
                        .putExtra(CommonClass.PROFILE_ID, profileID));
                break;
            case R.id.deleteProfile:
                showAlert();
                break;
            default:
                return false;
        }
        return true;
    }

    public void toProfileRecords(View view){
        if(noRecords){ // create one
            startActivity(new Intent(getApplicationContext(), CreateUpdateRecordActivity.class)
                    .putExtra(CommonClass.PROFILE_ID, profileID)
                    .putExtra(CommonClass.FROM, CommonClass.PROFILE));
        }else{ // show records
            startActivity(new Intent(getApplicationContext(), ProfileRecordsActivity.class)
                    .putExtra(CommonClass.PROFILE_ID, profileID));
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AllDataActivity.class));
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









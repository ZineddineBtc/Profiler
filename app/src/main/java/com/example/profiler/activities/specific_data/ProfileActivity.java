package com.example.profiler.activities.specific_data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.profiler.StaticClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.activities.create_update.CreateUpdateProfileActivity;
import com.example.profiler.activities.create_update.CreateUpdateRecordActivity;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.daos.RecordDAO;

import java.io.IOException;
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
    boolean noRecords = false;
    String noRecordsString = "Profile has no records. Click to create one",
            recordString = "Show profile records >";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileDAO = new ProfileDAO(this);
        recordDAO = new RecordDAO(this);
        findViewsByIds();
        profileID = getIntent().getIntExtra(StaticClass.PROFILE_ID, -1);
        if (profileID != -1) {
            setProfileUI();
            if (recordDAO.getProfileRecords(profileID).isEmpty()) {
                toProfileRecordsTV.setText(noRecordsString);
                noRecords = true;
            } else {
                toProfileRecordsTV.setText(recordString);
                noRecords = false;
            }
        }
        setActionBarTitle("Profile");
        if(!phoneTV.getText().toString().isEmpty()) {
            phoneTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone_no = phoneTV.getText().toString()
                            .replaceAll("-", "");
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone_no));
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                }
            });
        }
        if(!emailTV.getText().toString().isEmpty()){
            emailTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                            new String[] {emailTV.getText().toString()});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"");
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            });
        }
        if(!addressTV.getText().toString().isEmpty()){
            addressTV.setMovementMethod(LinkMovementMethod.getInstance());
            addressTV.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(
                            Uri.parse("https://www.google.com/maps/search/"+
                                    addressTV.getText().toString()));
                    startActivity(browserIntent);
                }
            });
        }

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
        String photoString = profileDAO.getProfile(profileID).getPhoto();
        if(photoString != null) {
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), Uri.parse(photoString));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),
                        "IO Exception when adapting a profile image",
                        Toast.LENGTH_LONG).show();
            }
            photoIV.setImageBitmap(imageBitmap);
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
                        .putExtra(StaticClass.PROFILE_ID, profileID));
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
                    .putExtra(StaticClass.PROFILE_ID, profileID)
                    .putExtra(StaticClass.FROM, StaticClass.PROFILE));
        }else{ // show records
            startActivity(new Intent(getApplicationContext(), ProfileRecordsActivity.class)
                    .putExtra(StaticClass.PROFILE_ID, profileID));
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









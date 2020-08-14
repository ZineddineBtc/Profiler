package com.example.profiler.activities.create_update;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profiler.StaticClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.adapters.SetDate;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.models.Profile;

import java.io.IOException;
import java.util.Objects;

public class CreateUpdateProfileActivity extends AppCompatActivity {

    ImageView photoIV;
    EditText nameET, bioET, phoneET, birthdayET, emailET, addressET, interestsET,
            relationshipStatusET, occupationET;
    TextView errorTV;
    int profileID;
    ProfileDAO profileDAO;
    String actionBarTitle = "New Profile";
    String photoString = null;
    SetDate setDate;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_profile);

        profileDAO = new ProfileDAO(getApplicationContext());

        findViewsByIds();
        profileID = getIntent().getIntExtra(StaticClass.PROFILE_ID, -1);
        if(profileID != -1) {
            setUpdateUI();
        }
        nameET.requestFocus();
        setDate = new SetDate(birthdayET, this);
        setActionBarTitle(actionBarTitle);
    }

    public void findViewsByIds(){
        photoIV = findViewById(R.id.profilePhotoIV);
        nameET = findViewById(R.id.profileNameET);
        bioET = findViewById(R.id.profileBioET);
        phoneET = findViewById(R.id.profilePhoneET);
        birthdayET = findViewById(R.id.profileBirthdayET);
        emailET = findViewById(R.id.profileEmailET);
        addressET = findViewById(R.id.profileAddressET);
        interestsET = findViewById(R.id.profileInterestsET);
        relationshipStatusET = findViewById(R.id.profileRelationshipStatusET);
        occupationET = findViewById(R.id.profileOccupationET);
        errorTV = findViewById(R.id.errorTV);
    }

    public void setUpdateUI(){
        photoString = profileDAO.getProfile(profileID).getPhoto();
        if(photoString != null){
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), Uri.parse(photoString));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "IO Exception when adapting a profile image",
                        Toast.LENGTH_LONG).show();
            }
            photoIV.setImageBitmap(imageBitmap);
        }
        nameET.setText(profileDAO.getProfile(profileID).getName());
        bioET.setText(profileDAO.getProfile(profileID).getBio());
        phoneET.setText(profileDAO.getProfile(profileID).getPhone());
        birthdayET.setText(profileDAO.getProfile(profileID).getBirthday());
        emailET.setText(profileDAO.getProfile(profileID).getEmail());
        addressET.setText(profileDAO.getProfile(profileID).getAddress());
        interestsET.setText(profileDAO.getProfile(profileID).getInterests());
        relationshipStatusET.setText(profileDAO.getProfile(profileID).getRelationshipStatus());
        occupationET.setText(profileDAO.getProfile(profileID).getOccupation());
        actionBarTitle = "Edit";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveProfile(){
        Profile profile;
        if(!nameET.getText().toString().isEmpty()){
            profile = new Profile(
                    photoString,
                    nameET.getText().toString(),
                    bioET.getText().toString(),
                    phoneET.getText().toString(),
                    birthdayET.getText().toString(),
                    emailET.getText().toString(),
                    addressET.getText().toString(),
                    interestsET.getText().toString(),
                    relationshipStatusET.getText().toString(),
                    occupationET.getText().toString()
            );
            if(profileID != -1){
                profileDAO.updateProfile(profileID, profile);
            }else{
                profileDAO.insertProfile(profile);
            }
            StaticClass.createReminder(
                    getApplicationContext(),
                    StaticClass.getNotification(getApplicationContext(), profile.getName()),
                    setDate.getCalendar());
            startActivity(new Intent(getApplicationContext(), AllDataActivity.class));
        }else{
            errorTV.setText(R.string.empty_name);
            errorTV.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorTV.setVisibility(View.GONE);
                }
            }, StaticClass.showErrorTV);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save){
            saveProfile();
        }
        return true;
    }

    public void importImage(View view){
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Images"),
                StaticClass.PICK_SINGLE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticClass.PICK_SINGLE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = data.getData();
            if(uri != null){
                final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                ContentResolver resolver = this.getContentResolver();
                resolver.takePersistableUriPermission(uri, takeFlags);

                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), uri);
                } catch (IOException e) {
                    Toast.makeText(this, "IO Exception when selecting a profile image",
                            Toast.LENGTH_LONG).show();
                }
                photoIV.setImageBitmap(imageBitmap);
                photoString = uri.toString();
            }
        }
    }

    public void setActionBarTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_profiles_blue);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> "+title+" </font>")
        );
    }
}

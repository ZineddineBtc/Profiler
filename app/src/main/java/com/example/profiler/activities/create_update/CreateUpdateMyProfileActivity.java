package com.example.profiler.activities.create_update;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.profiler.StaticClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.adapters.SetDate;
import com.example.profiler.daos.MyProfileDAO;
import com.example.profiler.models.Profile;

import java.io.IOException;
import java.util.Objects;

public class CreateUpdateMyProfileActivity extends AppCompatActivity {

    ImageView photoIV;
    EditText nameET, bioET, phoneET, birthdayET, emailET, addressET, interestsET,
            relationshipStatusET, occupationET;
    TextView errorTV;
    MyProfileDAO myProfileDAO;
    String actionBarTitle;
    String photoString = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_profile);
        myProfileDAO = new MyProfileDAO(getApplicationContext());
        findViewsByIds();
        if(Objects.equals(getIntent().getStringExtra(StaticClass.ACTION), StaticClass.UPDATE)){
            setUpdateUI();
        }else{
            actionBarTitle = "Create My Profile";
        }
        new SetDate(birthdayET, this);
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
        photoString = myProfileDAO.getMyProfile(StaticClass.myProfileID).getPhoto();
        if(photoString != null){
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), Uri.parse(photoString));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "IO Exception",
                        Toast.LENGTH_LONG).show();
            }
            photoIV.setImageBitmap(imageBitmap);
        }
        nameET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getName());
        bioET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getBio());
        phoneET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getPhone());
        birthdayET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getBirthday());
        emailET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getEmail());
        addressET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getAddress());
        interestsET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getInterests());
        relationshipStatusET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getRelationshipStatus());
        occupationET.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getOccupation());
        actionBarTitle = "Edit My Profile";
    }

    public void saveProfile(){
        if(!nameET.getText().toString().isEmpty()){
            Profile profile = new Profile(
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
            if(Objects.equals(getIntent().getStringExtra(StaticClass.ACTION), StaticClass.UPDATE)){
                myProfileDAO.updateProfile(StaticClass.myProfileID, profile);
            }else{
                myProfileDAO.insertProfile(profile);
            }
            startActivity(new Intent(getApplicationContext(), AllDataActivity.class)
                    .putExtra(StaticClass.TO, StaticClass.My_PROFILE));
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
                    Toast.makeText(this, "IO Exception when selecting my profile image",
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

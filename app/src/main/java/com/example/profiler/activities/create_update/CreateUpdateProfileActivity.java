package com.example.profiler.activities.create_update;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.profiler.CommonClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.adapters.SetDate;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.models.Profile;

import java.io.FileNotFoundException;
import java.io.InputStream;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_profile);

        profileDAO = new ProfileDAO(getApplicationContext());

        findViewsByIds();
        profileID = getIntent().getIntExtra(CommonClass.PROFILE_ID, -1);
        if(profileID != -1) {
            setUpdateUI();
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
        photoString = profileDAO.getProfile(profileID).getPhoto();
        if(photoString != null){
            photoIV.setImageBitmap(
                    CommonClass.pathToBitmap(photoString)
            );
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
            if(profileID != -1){
                profileDAO.updateProfile(profileID, profile);
            }else{
                profileDAO.insertProfile(profile);
            }
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
            }, CommonClass.showErrorTV);
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
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, CommonClass.PICK_PHOTO);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonClass.PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                return;
            }
            InputStream inputStream = null;
            try {
                inputStream = getApplicationContext().getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "File not found exception", Toast.LENGTH_SHORT).show();
            }
            if(inputStream!=null){
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                photoIV.setImageBitmap(imageBitmap);
                photoString = CommonClass.drawableToString(photoIV.getDrawable());
            }else{
                Toast.makeText(getApplicationContext(), "inputStream is null", Toast.LENGTH_SHORT).show();
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

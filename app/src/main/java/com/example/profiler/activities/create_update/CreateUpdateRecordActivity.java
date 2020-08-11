package com.example.profiler.activities.create_update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.format.DateFormat;
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
import com.example.profiler.activities.specific_data.ProfileRecordsActivity;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.daos.RecordDAO;
import com.example.profiler.models.Record;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class CreateUpdateRecordActivity extends AppCompatActivity {

    ProfileDAO profileDAO;
    RecordDAO recordDAO;
    TextView profileNameTV, errorTV;
    ImageView profilePhotoIV, imageIV;
    EditText titleET, descriptionET;
    int profileID, recordID;
    String imageString=null, dateString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_record);
        profileDAO = new ProfileDAO(this);
        recordDAO = new RecordDAO(this);
        profileID = getIntent().getIntExtra(CommonClass.PROFILE_ID, -1);
        recordID = getIntent().getIntExtra(CommonClass.RECORD_ID, -1);
        if(profileID==-1){
            profileID = recordDAO.getRecord(recordID).getProfileID();
        }
        findViewsByIds();
        if(recordID != -1){
            setUpdateUI();
        }
        dateString = (String) DateFormat.format("dd-MM-yyyy",new java.util.Date());
        setActionBarTitle("New Record");
    }
    public void findViewsByIds(){
        profileNameTV = findViewById(R.id.profileNameTV);
        profileNameTV.setText(profileDAO.getProfile(profileID).getName());
        profilePhotoIV = findViewById(R.id.profilePhotoIV);
        if(profileDAO.getProfile(profileID).getPhoto() != null) {
            profilePhotoIV.setImageBitmap(CommonClass.pathToBitmap(
                    profileDAO.getProfile(profileID).getPhoto()));
        }
        titleET = findViewById(R.id.titleET);
        descriptionET = findViewById(R.id.descriptionET);
        imageIV = findViewById(R.id.imageIV);
        errorTV = findViewById(R.id.errorTV);
    }
    public void setUpdateUI(){
        titleET.setText(recordDAO.getRecord(recordID).getTitle());
        descriptionET.setText(recordDAO.getRecord(recordID).getDescription());
        imageString = recordDAO.getRecord(recordID).getImage();
        if(imageString != null){
            imageIV.setImageBitmap(CommonClass.pathToBitmap(imageString));
        }
    }
    public void saveRecord(){
        if(!titleET.getText().toString().isEmpty()){
            Record record = new Record(
                    titleET.getText().toString(),
                    descriptionET.getText().toString(),
                    imageString,
                    dateString,
                    profileID
            );
            if(recordID != -1){
                recordDAO.updateRecord(recordID, record);
            }else{
                recordDAO.insertRecord(record);
            }
            if(Objects.equals(getIntent().getStringExtra(CommonClass.FROM), CommonClass.SELECT_PROFILE)){
                startActivity(new Intent(getApplicationContext(), AllDataActivity.class)
                        .putExtra(CommonClass.TO, CommonClass.ALL_RECORDS));
            }else if(Objects.equals(getIntent().getStringExtra(CommonClass.FROM), CommonClass.PROFILE_RECORDS)){
                startActivity(new Intent(getApplicationContext(), ProfileRecordsActivity.class)
                        .putExtra(CommonClass.PROFILE_ID, profileID));
            }else if(Objects.equals(getIntent().getStringExtra(CommonClass.FROM), CommonClass.PROFILE)){
                startActivity(new Intent(getApplicationContext(), ProfileRecordsActivity.class)
                        .putExtra(CommonClass.PROFILE_ID, profileID));
            }else if(Objects.equals(getIntent().getStringExtra(CommonClass.FROM), CommonClass.ALL_RECORDS)){
                startActivity(new Intent(getApplicationContext(), AllDataActivity.class)
                        .putExtra(CommonClass.TO, CommonClass.ALL_RECORDS));
            }

        }else{
            errorTV.setText(R.string.empty_title);
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
                imageIV.setImageBitmap(imageBitmap);
                imageString = CommonClass.drawableToString(imageIV.getDrawable());
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save){
            saveRecord();
        }
        return true;
    }
}












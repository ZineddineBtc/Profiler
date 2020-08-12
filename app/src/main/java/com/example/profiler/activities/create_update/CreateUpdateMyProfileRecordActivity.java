package com.example.profiler.activities.create_update;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.profiler.CommonClass;
import com.example.profiler.R;
import com.example.profiler.activities.specific_data.MyProfileRecordsActivity;
import com.example.profiler.daos.MyProfileDAO;
import com.example.profiler.daos.MyProfileRecordDAO;
import com.example.profiler.models.Record;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class CreateUpdateMyProfileRecordActivity extends AppCompatActivity {

    MyProfileDAO myProfileDAO;
    MyProfileRecordDAO myRecordDAO;
    TextView profileNameTV, errorTV;
    ImageView profilePhotoIV, imageIV;
    EditText titleET, descriptionET;
    int profileID, recordID;
    String imageString=null, dateString, actionBarTitle="New Record";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_record);
        myProfileDAO = new MyProfileDAO(this);
        myRecordDAO = new MyProfileRecordDAO(this);
        profileID = CommonClass.myProfileID;
        findViewsByIds();
        recordID = getIntent().getIntExtra(CommonClass.RECORD_ID, -1);
        if(recordID != -1){
            setUpdateUI();
        }
        dateString = (String) DateFormat.format("dd-MM-yyyy",new java.util.Date());
        setActionBarTitle(actionBarTitle);
    }
    public void findViewsByIds(){
        profileNameTV = findViewById(R.id.profileNameTV);
        profileNameTV.setText(myProfileDAO.getMyProfile(profileID).getName());
        profilePhotoIV = findViewById(R.id.profilePhotoIV);
        if(myProfileDAO.getMyProfile(profileID).getPhoto() != null) {
            profilePhotoIV.setImageBitmap(CommonClass.stringToBitmap(
                    myProfileDAO.getMyProfile(profileID).getPhoto()));
        }
        titleET = findViewById(R.id.titleET);
        descriptionET = findViewById(R.id.descriptionET);
        imageIV = findViewById(R.id.imageIV);
        errorTV = findViewById(R.id.errorTV);
    }
    public void setUpdateUI(){
        titleET.setText(myRecordDAO.getRecord(recordID).getTitle());
        descriptionET.setText(myRecordDAO.getRecord(recordID).getDescription());
        imageString = myRecordDAO.getRecord(recordID).getImage();
        if(imageString != null){
            imageIV.setImageBitmap(CommonClass.stringToBitmap(imageString));
        }
        actionBarTitle = "Update Record";
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
                myRecordDAO.updateRecord(recordID, record);
            }else{
                myRecordDAO.insertRecord(record);
            }
            startActivity(new Intent(getApplicationContext(), MyProfileRecordsActivity.class));
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
        getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, CommonClass.PICK_SINGLE_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonClass.PICK_SINGLE_IMAGE && resultCode == Activity.RESULT_OK) {
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












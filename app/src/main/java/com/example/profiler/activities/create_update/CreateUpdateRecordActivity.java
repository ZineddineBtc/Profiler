package com.example.profiler.activities.create_update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profiler.StaticClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.activities.specific_data.ProfileRecordsActivity;
import com.example.profiler.adapters.CustomPagerAdapter;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.daos.RecordDAO;
import com.example.profiler.models.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CreateUpdateRecordActivity extends AppCompatActivity {

    ProfileDAO profileDAO;
    RecordDAO recordDAO;
    TextView profileNameTV, errorTV;
    ImageView profilePhotoIV, imageIV;
    EditText titleET, descriptionET;
    int profileID, recordID;
    String imageString=null, dateString, actionBarTitle = "New Record";

    ViewPager viewPager;
    ArrayList<Bitmap> pagerImagesList;
    LinearLayout dotLayout;
    TextView[] dot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_record);
        profileDAO = new ProfileDAO(this);
        recordDAO = new RecordDAO(this);
        profileID = getIntent().getIntExtra(StaticClass.PROFILE_ID, -1);
        recordID = getIntent().getIntExtra(StaticClass.RECORD_ID, -1);
        if(profileID==-1){
            profileID = recordDAO.getRecord(recordID).getProfileID();
        }
        findViewsByIds();
        if(recordID != -1){
            setUpdateUI();
        }
        dateString = (String) DateFormat.format("dd-MM-yyyy",new java.util.Date());
        setActionBarTitle(actionBarTitle);



    }
    public void findViewsByIds(){
        profileNameTV = findViewById(R.id.profileNameTV);
        profileNameTV.setText(profileDAO.getProfile(profileID).getName());
        profilePhotoIV = findViewById(R.id.profilePhotoIV);
        String photoString = profileDAO.getProfile(profileID).getPhoto();
        if(photoString != null) {
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), Uri.parse(photoString));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "IO Exception",
                        Toast.LENGTH_LONG).show();
            }
            profilePhotoIV.setImageBitmap(imageBitmap);
        }
        titleET = findViewById(R.id.titleET);
        descriptionET = findViewById(R.id.descriptionET);
        errorTV = findViewById(R.id.errorTV);
        imageIV = findViewById(R.id.imageIV);
        pagerImagesList = new ArrayList<>();
        viewPager = findViewById(R.id.viewPager);
        dotLayout = findViewById(R.id.dotLayout);
    }
    public void setUpdateUI(){
        titleET.setText(recordDAO.getRecord(recordID).getTitle());
        descriptionET.setText(recordDAO.getRecord(recordID).getDescription());
        imageString = recordDAO.getRecord(recordID).getImage();
        if(imageString != null) {
            if(imageString.contains(",")){ // multiple images (ViewPager)
                String[] imageStringUris = imageString.split(",");
                for(String uriStr: imageStringUris){
                    Bitmap imageBitmap = null;
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(
                                getApplicationContext().getContentResolver(),
                                Uri.parse(uriStr));
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "IO Exception",
                                Toast.LENGTH_LONG).show();
                    }
                    pagerImagesList.add(imageBitmap);
                }
                initializePagerView();
                imageIV.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                dotLayout.setVisibility(View.VISIBLE);
            }else{ // single image
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(
                            getApplicationContext().getContentResolver(),
                            Uri.parse(imageString));
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "IO Exception",
                            Toast.LENGTH_LONG).show();
                }
                imageIV.setImageBitmap(imageBitmap);
                imageIV.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                dotLayout.setVisibility(View.GONE);
            }
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
                recordDAO.updateRecord(recordID, record);
            }else{
                recordDAO.insertRecord(record);
            }
            if(Objects.equals(getIntent().getStringExtra(StaticClass.FROM), StaticClass.SELECT_PROFILE)){
                startActivity(new Intent(getApplicationContext(), AllDataActivity.class)
                        .putExtra(StaticClass.TO, StaticClass.ALL_RECORDS));
            }else if(Objects.equals(getIntent().getStringExtra(StaticClass.FROM), StaticClass.PROFILE_RECORDS)){
                startActivity(new Intent(getApplicationContext(), ProfileRecordsActivity.class)
                        .putExtra(StaticClass.PROFILE_ID, profileID));
            }else if(Objects.equals(getIntent().getStringExtra(StaticClass.FROM), StaticClass.PROFILE)){
                startActivity(new Intent(getApplicationContext(), ProfileRecordsActivity.class)
                        .putExtra(StaticClass.PROFILE_ID, profileID));
            }else if(Objects.equals(getIntent().getStringExtra(StaticClass.FROM), StaticClass.ALL_RECORDS)){
                startActivity(new Intent(getApplicationContext(), AllDataActivity.class)
                        .putExtra(StaticClass.TO, StaticClass.ALL_RECORDS));
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
            }, StaticClass.showErrorTV);
        }
    }

    public void importImage(View view){
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Images"),
                StaticClass.PICK_MULTIPLE_IMAGES);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticClass.PICK_MULTIPLE_IMAGES && resultCode == RESULT_OK
                && data != null) {
            if(data.getClipData() != null){
                ClipData clipData = data.getClipData();
                ArrayList<Uri> uriList = new ArrayList<>();
                for (int i=0; i<clipData.getItemCount(); i++) {

                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    uriList.add(uri);
                }
                final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                ContentResolver resolver = this.getContentResolver();
                for (Uri uri : uriList) {
                    resolver.takePersistableUriPermission(uri, takeFlags);
                }
                try {
                    StringBuilder multipleImagesStringBuilder = new StringBuilder();
                    for(int i=0; i<uriList.size(); i++){
                        pagerImagesList.add(MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(), uriList.get(i)));
                        multipleImagesStringBuilder.append(uriList.get(i));
                        if(i != uriList.size()-1){
                            multipleImagesStringBuilder.append(",");
                        }
                        imageString = String.valueOf(multipleImagesStringBuilder);
                    }
                    imageIV.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                    dotLayout.setVisibility(View.VISIBLE);
                    initializePagerView();
                } catch (IOException e) {
                    Toast.makeText(this, "IO Exception",
                            Toast.LENGTH_LONG).show();
                }
            }else{ // a single image
                viewPager.setVisibility(View.GONE);
                dotLayout.setVisibility(View.GONE);
                imageIV.setVisibility(View.VISIBLE);
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
                        Toast.makeText(this, "IO Exception when selecting a single image",
                                Toast.LENGTH_LONG).show();
                    }
                    imageIV.setImageBitmap(imageBitmap);
                    imageString = uri.toString();
                }
            }
        }else{
            Toast.makeText(this, "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void initializePagerView(){
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(getApplicationContext(), pagerImagesList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(20);
        StaticClass.addDot(getApplicationContext(), pagerImagesList,
                0, dot, dotLayout);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                StaticClass.addDot(getApplicationContext(), pagerImagesList,
                        i, dot, dotLayout);
            }
            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageScrollStateChanged(int i) {}
        });
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












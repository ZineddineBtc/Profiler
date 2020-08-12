package com.example.profiler.activities.create_update;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.profiler.CommonClass;
import com.example.profiler.R;
import com.example.profiler.activities.specific_data.MyProfileRecordsActivity;
import com.example.profiler.adapters.CustomPagerAdapter;
import com.example.profiler.daos.MyProfileDAO;
import com.example.profiler.daos.MyProfileRecordDAO;
import com.example.profiler.models.Record;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class CreateUpdateMyProfileRecordActivity extends AppCompatActivity {

    MyProfileDAO myProfileDAO;
    MyProfileRecordDAO myRecordDAO;
    TextView profileNameTV, errorTV;
    ImageView profilePhotoIV, imageIV;
    EditText titleET, descriptionET;
    int profileID, recordID;
    String imageString=null, dateString, actionBarTitle="New Record";

    ViewPager viewPager;
    ArrayList<Bitmap> pagerImagesList;
    LinearLayout dotLayout;
    TextView[] dot;

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

        pagerImagesList = new ArrayList<>();
        viewPager = findViewById(R.id.viewPager);
        dotLayout = findViewById(R.id.dotLayout);
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
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Images"),
                CommonClass.PICK_MULTIPLE_IMAGES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonClass.PICK_MULTIPLE_IMAGES && resultCode == RESULT_OK
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
        addDot(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                addDot(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void addDot(int pagePosition) {
        dot = new TextView[pagerImagesList.size()];
        dotLayout.removeAllViews();
        for (int i = 0; i < dot.length; i++) {;
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml("&#9673;"));
            dot[i].setTextSize(35);
            dot[i].setTextColor(getColor(R.color.dark_grey));
            dotLayout.addView(dot[i]);
        }
        dot[pagePosition].setTextColor(getColor(R.color.blue));
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












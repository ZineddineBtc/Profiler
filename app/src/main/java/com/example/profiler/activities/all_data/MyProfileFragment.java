package com.example.profiler.activities.all_data;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.profiler.StaticClass;
import com.example.profiler.R;
import com.example.profiler.activities.create_update.CreateUpdateMyProfileActivity;
import com.example.profiler.activities.create_update.CreateUpdateMyProfileRecordActivity;
import com.example.profiler.activities.specific_data.MyProfileRecordsActivity;
import com.example.profiler.daos.MyProfileDAO;
import com.example.profiler.daos.MyProfileRecordDAO;

import java.io.IOException;
import java.util.Objects;

public class MyProfileFragment extends Fragment {

    Context context;
    View fragmentView;
    ImageView photoIV;
    TextView nameTV, bioTV, phoneTV, birthdayTV, emailTV, addressTV, interestsTV,
            toMyProfileRecordsTV, relationshipStatusTV, occupationTV;
    LinearLayout noMyProfileLL, myProfileLL;
    Button createMyProfileButton;
    ImageButton createUpdateMyProfileIB;
    MyProfileDAO myProfileDAO;
    MyProfileRecordDAO myRecordDAO;
    boolean noRecords=false;
    String noRecordsString = "Your Profile has no records. Click to create one",
            recordString = "Show my records >";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        context = Objects.requireNonNull(getContext());
        myProfileDAO = new MyProfileDAO(context);
        myRecordDAO = new MyProfileRecordDAO(context);
        findViewsByIds();
        if(myProfileDAO.numberOfRows() != 0){
            setProfileUI();
            createUpdateMyProfileIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createUpdateMyProfile(StaticClass.UPDATE);
                }
            });
            if(myRecordDAO.getProfileRecords(StaticClass.myProfileID).isEmpty()){
                toMyProfileRecordsTV.setText(noRecordsString);
                noRecords = true;
            }else{
                toMyProfileRecordsTV.setText(recordString);
                noRecords = false;
            }
            toMyProfileRecordsTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toMyProfileRecords();
                }
            });
        }else{
            createUpdateMyProfileIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createUpdateMyProfile(StaticClass.CREATE);
                }
            });
        }
        if(!phoneTV.getText().toString().isEmpty()) {
            phoneTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone_no = phoneTV.getText().toString()
                            .replaceAll("-", "");
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone_no));
                    if (context.checkSelfPermission(Manifest.permission.CALL_PHONE)
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
        return fragmentView;
    }
    public void findViewsByIds(){
        photoIV = fragmentView.findViewById(R.id.profilePhotoIV);
        nameTV = fragmentView.findViewById(R.id.profileNameTV);
        bioTV = fragmentView.findViewById(R.id.profileBioTV);
        phoneTV = fragmentView.findViewById(R.id.profilePhoneTV);
        birthdayTV = fragmentView.findViewById(R.id.profileBirthdayTV);
        emailTV = fragmentView.findViewById(R.id.profileEmailTV);
        addressTV = fragmentView.findViewById(R.id.profileAddressTV);
        interestsTV = fragmentView.findViewById(R.id.profileInterestsTV);
        relationshipStatusTV = fragmentView.findViewById(R.id.profileRelationshipStatusTV);
        occupationTV = fragmentView.findViewById(R.id.profileOccupationTV);
        toMyProfileRecordsTV = fragmentView.findViewById(R.id.toMyProfileRecordsTV);
        noMyProfileLL = fragmentView.findViewById(R.id.noMyProfileLL);
        myProfileLL = fragmentView.findViewById(R.id.myProfileLL);
        createMyProfileButton = fragmentView.findViewById(R.id.createMyProfileButton);
        createUpdateMyProfileIB = fragmentView.findViewById(R.id.updateMyProfileIB);
    }
    public void setProfileUI(){
        noMyProfileLL.setVisibility(View.GONE);
        myProfileLL.setVisibility(View.VISIBLE);
        String photoString = myProfileDAO.getMyProfile(StaticClass.myProfileID).getPhoto();
        if(photoString != null){
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        Objects.requireNonNull(getContext()).
                                getContentResolver(), Uri.parse(photoString));
            } catch (IOException e) {
                Toast.makeText(getContext(),
                        "IO Exception when adapting a profile image",
                        Toast.LENGTH_LONG).show();
            }
            photoIV.setImageBitmap(imageBitmap);
        }
        nameTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getName());
        bioTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getBio());
        phoneTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getPhone());
        birthdayTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getBirthday());
        emailTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getEmail());
        addressTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getAddress());
        interestsTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getInterests());
        relationshipStatusTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getRelationshipStatus());
        occupationTV.setText(myProfileDAO.getMyProfile(StaticClass.myProfileID).getOccupation());
    }
    public void createUpdateMyProfile(String action){
        startActivity(new Intent(context, CreateUpdateMyProfileActivity.class)
        .putExtra(StaticClass.ACTION, action));
    }
    public void toMyProfileRecords(){
        if(noRecords){ // create one
            startActivity(new Intent(context, CreateUpdateMyProfileRecordActivity.class));
        }else{ // show records
            startActivity(new Intent(context, MyProfileRecordsActivity.class));
        }
    }











    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public MyProfileFragment() { }
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

}
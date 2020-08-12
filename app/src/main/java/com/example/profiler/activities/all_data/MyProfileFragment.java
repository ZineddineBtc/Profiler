package com.example.profiler.activities.all_data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.profiler.CommonClass;
import com.example.profiler.R;
import com.example.profiler.activities.create_update.CreateUpdateMyProfileActivity;
import com.example.profiler.activities.create_update.CreateUpdateMyProfileRecordActivity;
import com.example.profiler.activities.specific_data.MyProfileRecordsActivity;
import com.example.profiler.daos.MyProfileDAO;
import com.example.profiler.daos.MyProfileRecordDAO;

import java.util.Objects;

public class MyProfileFragment extends Fragment {

    Context context;
    View fragmentView;
    ImageView photoIV;
    TextView nameTV, bioTV, phoneTV, birthdayTV, emailTV, addressTV, interestsTV,
            toMyProfileRecordsTV, relationshipStatusTV, occupationTV;
    LinearLayout noMyProfileLL, myProfileLL;
    Button createMyProfileButton;
    ImageButton updateMyProfileIB;
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
            updateMyProfileIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createUpdateMyProfile(CommonClass.UPDATE);
                }
            });
            if(myRecordDAO.getProfileRecords(CommonClass.myProfileID).isEmpty()){
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
            createMyProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createUpdateMyProfile(CommonClass.CREATE);
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
        updateMyProfileIB = fragmentView.findViewById(R.id.updateMyProfileIB);
    }
    public void setProfileUI(){
        noMyProfileLL.setVisibility(View.GONE);
        myProfileLL.setVisibility(View.VISIBLE);
        if(myProfileDAO.getMyProfile(CommonClass.myProfileID).getPhoto() != null){
            photoIV.setImageBitmap(
                    CommonClass.stringToBitmap(myProfileDAO.getMyProfile(CommonClass.myProfileID).getPhoto()));
        }
        nameTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getName());
        bioTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getBio());
        phoneTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getPhone());
        birthdayTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getBirthday());
        emailTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getEmail());
        addressTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getAddress());
        interestsTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getInterests());
        relationshipStatusTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getRelationshipStatus());
        occupationTV.setText(myProfileDAO.getMyProfile(CommonClass.myProfileID).getOccupation());
    }
    public void createUpdateMyProfile(String action){
        startActivity(new Intent(context, CreateUpdateMyProfileActivity.class)
        .putExtra(CommonClass.ACTION, action));
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
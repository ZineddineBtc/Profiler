package com.example.profiler.activities.all_data;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.profiler.CommonClass;
import com.example.profiler.R;

import java.util.Objects;

public class AllDataActivity extends AppCompatActivity {

    Fragment allProfilesFragment, allRecordsFragment, myProfileF;
    ImageButton allProfilesTabButton, allRecordsTabButton, myProfileTabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_data);

        findViewsByIds();
        initiateFragments();
        if (Objects.equals(getIntent().getStringExtra(CommonClass.TO), CommonClass.ALL_RECORDS)) {
            setAllRecordsF();
        }else if(Objects.equals(getIntent().getStringExtra(CommonClass.TO), CommonClass.My_PROFILE)) {
            setMyProfileF();
        }else{
            setAllProfilesF();
        }
    }

    public void findViewsByIds(){
        allProfilesTabButton = findViewById(R.id.allProfilesTabButton);
        allRecordsTabButton = findViewById(R.id.allRecordsTabButton);
        myProfileTabButton = findViewById(R.id.myProfileTabButton);
    }

    public void initiateFragments(){
        allProfilesFragment = new AllProfilesFragment();
        allRecordsFragment = new AllRecordsFragment();
        myProfileF = new MyProfileFragment();
    }

    public void openFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.allDataFragmentFL, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setActionBarTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_profiles_blue);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> "+title+" </font>")
        );
    }

    public void setFragment(View view){
        if(view.getId() == R.id.allProfilesTabButton){
            setAllProfilesF();
        }else if(view.getId() == R.id.allRecordsTabButton){
            setAllRecordsF();
        }else{
            setMyProfileF();
        }
    }

    public void setAllProfilesF(){
        openFragment(allProfilesFragment);
        setActionBarTitle("All Profiles");
        allProfilesTabButton.setImageResource(R.drawable.ic_people_blue);
        allRecordsTabButton.setImageResource(R.drawable.ic_records_grey);
        myProfileTabButton.setImageResource(R.drawable.ic_my_profile_grey);
    }

    public void setAllRecordsF(){
        openFragment(allRecordsFragment);
        setActionBarTitle("All Records");
        allProfilesTabButton.setImageResource(R.drawable.ic_people_grey);
        allRecordsTabButton.setImageResource(R.drawable.ic_records_blue);
        myProfileTabButton.setImageResource(R.drawable.ic_my_profile_grey);
    }
    public void setMyProfileF(){
        openFragment(myProfileF);
        setActionBarTitle("My Profile");
        allProfilesTabButton.setImageResource(R.drawable.ic_people_grey);
        allRecordsTabButton.setImageResource(R.drawable.ic_records_grey);
        myProfileTabButton.setImageResource(R.drawable.ic_my_profile_blue);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

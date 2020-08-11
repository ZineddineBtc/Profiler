package com.example.profiler.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.profiler.CommonClass;
import com.example.profiler.R;
import com.example.profiler.activities.create_update.CreateUpdateRecordActivity;
import com.example.profiler.models.Profile;

import java.util.ArrayList;
import java.util.List;

public class SelectProfileAdapter extends RecyclerView.Adapter<SelectProfileAdapter.ViewHolder> {

    private List<Profile> profileList, copyList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public SelectProfileAdapter(Context context, List<Profile> profileList) {
        this.mInflater = LayoutInflater.from(context);
        this.profileList = profileList;
        copyList = new ArrayList<>(profileList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profiles_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTV.setText(profileList.get(position).getName());
        if(profileList.get(position).getPhoto() != null){
            holder.photoIV.setImageBitmap(
                    CommonClass.pathToBitmap(profileList.get(position).getPhoto()));
        }
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTV;
        ImageView photoIV;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            photoIV = itemView.findViewById(R.id.photoIV);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());

            itemView.getContext().startActivity(
                    new Intent(itemView.getContext(), CreateUpdateRecordActivity.class)
                    .putExtra(CommonClass.FROM, CommonClass.SELECT_PROFILE)
                    .putExtra(CommonClass.PROFILE_ID, profileList.get(getAdapterPosition()).getId())
            );
        }
    }

    Profile getItem(int id) {
        return profileList.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void filter(String queryText) {
        profileList.clear();
        if(queryText.isEmpty()) {
            profileList.addAll(copyList);
        }else{
            for(Profile profile: copyList) {
                if(profile.getName().toLowerCase().contains(queryText.toLowerCase())) {
                    profileList.add(profile);
                }
            }
        }
        notifyDataSetChanged();
    }
}
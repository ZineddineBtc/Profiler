package com.example.profiler.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profiler.CommonClass;
import com.example.profiler.R;
import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.activities.create_update.CreateUpdateMyProfileRecordActivity;
import com.example.profiler.daos.MyProfileDAO;
import com.example.profiler.daos.MyProfileRecordDAO;
import com.example.profiler.models.Record;

import java.util.ArrayList;
import java.util.List;

public class MyProfileRecordsAdapter extends RecyclerView.Adapter<MyProfileRecordsAdapter.ViewHolder> {

    private List<Record> recordList, copyList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    Context context;

    public MyProfileRecordsAdapter(Context context, List<Record> data) {
        this.mInflater = LayoutInflater.from(context);
        this.recordList = data;
        this.context = context;
        copyList = new ArrayList<>(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.record_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTV.setText(recordList.get(position).getTitle());
        if(!recordList.get(position).getDescription().isEmpty()){
            holder.descriptionTV.setText(recordList.get(position).getDescription());
        }else{
            holder.descriptionTV.setVisibility(View.GONE);
        }
        if(recordList.get(position).getImage() != null) {
            holder.imageIV.setImageBitmap(stringToBitmap(recordList.get(position).getImage()));
        }else{
            holder.imageIV.setImageBitmap(null);
        }
        holder.timeTV.setText(recordList.get(position).getTime());

        holder.profileNameTV.setText(
                holder.myProfileDAO.getMyProfile(CommonClass.myProfileID).getName()
        );
        if(holder.myProfileDAO.getMyProfile(CommonClass.myProfileID)
                .getPhoto() != null) {
            holder.profilePhotoIV.setImageBitmap(CommonClass.stringToBitmap(
                    holder.myProfileDAO.getMyProfile(CommonClass.myProfileID).getPhoto())
            );
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView profileNameTV, titleTV, descriptionTV, timeTV,
                editTV, deleteTV;
        ImageView imageIV, toggleRecord, profilePhotoIV;
        LinearLayout editDeleteRecordLL;
        boolean shown = false;

        MyProfileDAO myProfileDAO;
        MyProfileRecordDAO myProfileRecordDAO;

        public ViewHolder(final View itemView) {
            super(itemView);

            myProfileRecordDAO = new MyProfileRecordDAO(itemView.getContext());
            myProfileDAO = new MyProfileDAO(itemView.getContext());

            profileNameTV = itemView.findViewById(R.id.profileNameTV);
            profilePhotoIV = itemView.findViewById(R.id.profilePhotoIV);

            titleTV = itemView.findViewById(R.id.titleTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            imageIV = itemView.findViewById(R.id.imageIV);
            timeTV = itemView.findViewById(R.id.timeTV);

            editTV = itemView.findViewById(R.id.editTV);
            deleteTV = itemView.findViewById(R.id.deleteTV);
            toggleRecord = itemView.findViewById(R.id.toggleInfoIV);
            editDeleteRecordLL = itemView.findViewById(R.id.editDeleteRecordLL);

            toggleRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(shown){
                        editDeleteRecordLL.setVisibility(View.GONE);
                        toggleRecord.setImageDrawable(
                                itemView.getContext().getDrawable(R.drawable.ic_show_left)
                        );
                    }else{
                        editDeleteRecordLL.setVisibility(View.VISIBLE);
                        toggleRecord.setImageDrawable(
                                itemView.getContext().getDrawable(R.drawable.ic_hide_left)
                        );
                    }
                    shown = !shown;
                }
            });

            editTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(
                            new Intent(itemView.getContext(), CreateUpdateMyProfileRecordActivity.class)
                            .putExtra(CommonClass.RECORD_ID, recordList.get(getAdapterPosition()).getId())
                    );
                }
            });

            deleteTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Record")
                            .setMessage("Are you sure you want to delete this record?")
                            .setPositiveButton(
                                    Html.fromHtml("<font color=\"#FF0000\"> Delete </font>")
                                    , new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    myProfileRecordDAO.deleteRecord(recordList.get(getAdapterPosition()).getId());
                                    recordList.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(
                                    Html.fromHtml("<font color=\"#1976D2\"> Cancel </font>"),
                                    null)
                            .show();
                }
            });

            profilePhotoIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(
                            new Intent(itemView.getContext(), AllDataActivity.class)
                            .putExtra(CommonClass.TO, CommonClass.My_PROFILE)
                    );
                }
            });
            profileNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(
                            new Intent(itemView.getContext(), AllDataActivity.class)
                                    .putExtra(CommonClass.TO, CommonClass.My_PROFILE)
                    );
                }
            });

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());

        }
    }

    Record getItem(int id) {
        return recordList.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public static Bitmap stringToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public void filter(String queryText) {
        recordList.clear();
        if(queryText.isEmpty()) {
            recordList.addAll(copyList);
        }else{
            for(Record record: copyList) {
                if(record.getTitle().toLowerCase().contains(queryText.toLowerCase())) {
                    recordList.add(record);
                }
            }
        }
        notifyDataSetChanged();
    }
}
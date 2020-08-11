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
import com.example.profiler.activities.create_update.CreateUpdateRecordActivity;
import com.example.profiler.activities.specific_data.ProfileActivity;
import com.example.profiler.daos.ProfileDAO;
import com.example.profiler.daos.RecordDAO;
import com.example.profiler.models.Record;

import java.util.ArrayList;
import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private List<Record> recordList, copyList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    Context context;
    String from;

    public RecordsAdapter(Context context, List<Record> data, String from) {
        this.mInflater = LayoutInflater.from(context);
        this.recordList = data;
        this.context = context;
        copyList = new ArrayList<>(data);
        this.from = from;
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
                holder.profileDAO.getProfile(recordList.get(position).getProfileID()).getName()
        );
        if(holder.profileDAO.getProfile(recordList.get(position).getProfileID())
                .getPhoto() != null) {
            holder.profilePhotoIV.setImageBitmap(CommonClass.pathToBitmap(
                    holder.profileDAO.getProfile(recordList.get(position).getProfileID()).getPhoto())
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

        ProfileDAO profileDAO;
        RecordDAO recordDAO;

        public ViewHolder(final View itemView) {
            super(itemView);

            recordDAO = new RecordDAO(itemView.getContext());
            profileDAO = new ProfileDAO(itemView.getContext());

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
                            new Intent(itemView.getContext(), CreateUpdateRecordActivity.class)
                            .putExtra(CommonClass.RECORD_ID, recordList.get(getAdapterPosition()).getId())
                            .putExtra(CommonClass.FROM, from)
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
                                    recordDAO.deleteRecord(recordList.get(getAdapterPosition()).getId());
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
                            new Intent(itemView.getContext(), ProfileActivity.class)
                            .putExtra(CommonClass.PROFILE_ID, recordList.get(getAdapterPosition()).getProfileID())
                    );
                }
            });
            profileNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(
                            new Intent(itemView.getContext(), ProfileActivity.class)
                                    .putExtra(CommonClass.PROFILE_ID, recordList.get(getAdapterPosition()).getProfileID())
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
package com.example.reminder.ui;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.reminder.R;
import com.example.reminder.model.BucketListItem;

import java.util.List;

public class BucketListItemAdapter extends RecyclerView.Adapter<BucketListItemAdapter.ViewHolder>  {

    private List<BucketListItem> mBucketListItems;

    public BucketListItemAdapter(List<BucketListItem> mBucketListItems) {
        this.mBucketListItems = mBucketListItems;
    }

    @NonNull
    @Override
    public BucketListItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_layout, null);
        // Return a new holder instance
        BucketListItemAdapter.ViewHolder viewHolder = new BucketListItemAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BucketListItemAdapter.ViewHolder viewHolder, int i) {
        BucketListItem bucketListItem = mBucketListItems.get(i);
        viewHolder.title.setText(bucketListItem.getTitle());
        viewHolder.description.setText(bucketListItem.getDescription());

        if (mBucketListItems.get(i).isChecked()) {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.description.setPaintFlags(viewHolder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (mBucketListItems.get(i).isChecked() == false) {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.description.setPaintFlags(viewHolder.description.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return mBucketListItems.size();
    }

    public void swapList (List<BucketListItem> newList) {
        mBucketListItems = newList;
        if (newList != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView1);
            description = itemView.findViewById(R.id.textView2);
            checkbox = itemView.findViewById(R.id.checkBox);
        }
    }

}
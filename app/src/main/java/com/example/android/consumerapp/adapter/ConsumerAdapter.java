package com.example.android.consumerapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.consumerapp.CustomOnItemClickListener;
import com.example.android.consumerapp.FormActivity;
import com.example.android.consumerapp.R;
import com.example.android.consumerapp.entity.NoteItem;

import java.util.ArrayList;

import static com.example.android.consumerapp.db.DatabaseContract.NoteColumns.CONTENT_URI;

public class ConsumerAdapter extends RecyclerView.Adapter<ConsumerAdapter.NoteViewHolder> {

    private final ArrayList<NoteItem> listNotes = new ArrayList<>();
    private final Activity activity;

    public ConsumerAdapter(Activity activity){
        this.activity = activity;
    }

    public ArrayList<NoteItem> getListNotes() {
        return listNotes;
    }

    public void setListNotes(ArrayList<NoteItem> listNotes){
        this.listNotes.clear();
        this.listNotes.addAll(listNotes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consumer_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int i) {
        holder.tvTitle.setText(getListNotes().get(i).getTitle());
        holder.tvDate.setText(getListNotes().get(i).getDate());
        holder.tvDescription.setText(getListNotes().get(i).getDescription());
        holder.itemView.setOnClickListener(new CustomOnItemClickListener(i, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(activity, FormActivity.class);

                Uri uri = Uri.parse(CONTENT_URI +  "/" + getListNotes().get(position).getId());
                intent.setData(uri);
                activity.startActivity(intent);
            }
        }));

    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        final TextView tvTitle, tvDescription, tvDate;
        View rootView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvDate = itemView.findViewById(R.id.tv_item_date);
        }
    }
}

package com.example.cvsuvirtualid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.HolderAdapter> {
    Context context;
    ArrayList<PostModel> postModels;

    public PostAdapter(Context context, ArrayList<PostModel> postModels) {
        this.context = context;
        this.postModels = postModels;
    }

    @NonNull
    @Override
    public PostAdapter.HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list, parent, false);
        return new HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.HolderAdapter holder, int position) {
        PostModel model = postModels.get(position);
        String caption = model.getCaption();
        String date = model.getDate();
        String imageId = model.getFileId();


        holder.date.setText(date);

        if(caption.isEmpty()){
            holder.pCaption.setVisibility(View.GONE);
        }else {
            holder.pCaption.setText(caption);
        }

        if(imageId.equals("null")){
            holder.ImageV.setVisibility(View.GONE);
        }else {
        Glide
                .with(context)
                .load("https://docs.google.com/uc?id="+imageId)
                .centerCrop()
                .into(holder.ImageV);}
    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public class HolderAdapter extends RecyclerView.ViewHolder {
        private TextView pCaption, date;
        private ImageView ImageV;

        public HolderAdapter(@NonNull View itemView) {
            super(itemView);

            pCaption = itemView.findViewById(R.id.pCaption);
            date = itemView.findViewById(R.id.date);
            ImageV = itemView.findViewById(R.id.ImageV);

        }
    }
}
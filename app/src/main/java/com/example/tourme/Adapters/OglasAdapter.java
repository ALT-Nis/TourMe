package com.example.tourme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tourme.MessageActivity;
import com.example.tourme.Model.Chat;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.User;
import com.example.tourme.R;

import java.util.List;

public class OglasAdapter extends RecyclerView.Adapter<OglasAdapter.ViewHolder> {

    private Context mContext;
    private List<Oglas> mOglas;

    public OglasAdapter(Context mContext, List<Oglas> mOglas){
        this.mContext = mContext;
        this.mOglas = mOglas;
    }

    public OglasAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.oglas_item, parent, false);
        return new OglasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mOglas.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView username;
        public ImageView oglas_image;
        public TextView deskripcija;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            oglas_image = itemView.findViewById(R.id.oglas_image);
            deskripcija = itemView.findViewById(R.id.deskripcija);

        }
    }
}

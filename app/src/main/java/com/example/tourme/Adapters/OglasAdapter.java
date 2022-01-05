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
import com.example.tourme.Model.Oglas;
import com.example.tourme.R;
import com.example.tourme.pregledJednogOglasa;

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
        Oglas oglas = mOglas.get(position);

        holder.deskripcija.setText(oglas.getOpis());
        holder.username.setText(oglas.getUsername());
        holder.cena.setText(oglas.getCenaOglasa()+" rsd");

        if(oglas.getImageurl().equals("default")){
            holder.oglas_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(oglas.getImageurl()).into(holder.oglas_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, pregledJednogOglasa.class);
                intent.putExtra("IDOglasa",oglas.getIdOglasa());
                intent.putExtra("NazivGrada", oglas.getGrad());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mOglas.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView username;
        public ImageView oglas_image;
        public TextView deskripcija;
        public TextView cena;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            oglas_image = itemView.findViewById(R.id.oglas_image);
            deskripcija = itemView.findViewById(R.id.deskripcija);
            cena = itemView.findViewById(R.id.cena);

        }
    }
}

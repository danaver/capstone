package com.example.gailsemilladucao.capstone.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.io.File;
import java.util.List;

public class categAdapter extends RecyclerView.Adapter<categAdapter.MyViewHolder> {
    
    private Context mcontext;
    private List<categ> mdata;


    public categAdapter(Context mcontext, List<categ> mdata) {
        this.mcontext = mcontext;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        view = inflater.inflate(R.layout.cardview,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.categ.setText(mdata.get(i).getName());
        holder.view.setBackgroundResource(mdata.get(i).getImg());

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    //interface

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDownloadClick(int i);
        void onDeleteClick(int i);
        void onViewClick(int i);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener =listener;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView categ;
        Button view;
        Button download;
        Button delete;


        public MyViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            categ = itemView.findViewById(R.id.categ);
            view = itemView.findViewById(R.id.view);
            download = itemView.findViewById(R.id.download);
            delete = itemView.findViewById(R.id.delete);

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!= null){
                        int i = getAdapterPosition();
                        if(i != RecyclerView.NO_POSITION){
                             listener.onDownloadClick(i);
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!= null){
                        int i = getAdapterPosition();
                        if(i != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(i);
                        }
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!= null){
                        int i = getAdapterPosition();
                        if(i != RecyclerView.NO_POSITION){
                            listener.onViewClick(i);
                        }
                    }
                }
            });

        }
    }
}

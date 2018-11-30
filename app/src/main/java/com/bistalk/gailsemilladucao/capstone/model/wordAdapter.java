package com.bistalk.gailsemilladucao.capstone.model;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bistalk.gailsemilladucao.capstone.R;
import com.bistalk.gailsemilladucao.capstone.backend.EditData;
import com.bistalk.gailsemilladucao.capstone.view.ShowData;

import java.util.ArrayList;

public class wordAdapter extends ArrayAdapter {

    Activity context;
    int resource, status;
    ArrayList<wordbanks> list;

    public wordAdapter(@NonNull Activity context, int resource, @NonNull ArrayList<wordbanks> objects, int status) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        this.status = status;
    }

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        final View row = inflater.inflate(R.layout.row,null);

        final TextView eng = row.findViewById(R.id.engword);
        final TextView ceb = row.findViewById(R.id.cebword);
        final CheckBox cimg = row.findViewById(R.id.cimg);
        final CheckBox caud = row.findViewById(R.id.caud);
        final CheckBox cfx = row.findViewById(R.id.cfx);
        final Button cedt = row.findViewById(R.id.cedit);

        final wordbanks wl = list.get(position);

        eng.setText(wl.getEnglish());
        ceb.setText(wl.getCebuano());
        caud.setChecked(true);

        if(wl.getEffect().equals("null")){
            cfx.setChecked(false);
        }else{
            cfx.setChecked(true);
        }

        if(wl.getPicture().equals("null")){
            cimg.setChecked(false);
        }else{
            cimg.setChecked(true);
        }

        if(wl.getStatus() == 0){
            cedt.setEnabled(false);
        }

        if(status == 1) {
            cedt.setVisibility(View.GONE);
        }

        cedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, EditData.class);
                i.putExtra("res", wl);

                context.startActivity(i);
            }
        });

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ShowData.class);
                i.putExtra("res", wl);
                if(wl.getStatus() != 0) {
                    context.startActivity(i);
                }else{
                    Toast.makeText(context, "Please download to view", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return row;
    }
}

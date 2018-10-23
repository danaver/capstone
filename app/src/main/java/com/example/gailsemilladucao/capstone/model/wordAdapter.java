package com.example.gailsemilladucao.capstone.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.gailsemilladucao.capstone.R;

import java.util.ArrayList;
import java.util.List;

public class wordAdapter extends ArrayAdapter {

    Activity context;
    int resource;
    ArrayList<wordbanks> list;

    public wordAdapter(@NonNull Activity context, int resource, @NonNull ArrayList<wordbanks> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
    }

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        final View row = inflater.inflate(R.layout.row,null);

        final TextView eng = row.findViewById(R.id.engword);
        final TextView ceb = row.findViewById(R.id.cebword);
        final CheckBox cimg = row.findViewById(R.id.cimg);
        final CheckBox caud = row.findViewById(R.id.caud);
        final CheckBox cfx = row.findViewById(R.id.cfx);

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

        return row;
    }
}

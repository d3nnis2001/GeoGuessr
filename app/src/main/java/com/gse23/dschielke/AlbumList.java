package com.gse23.dschielke;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class AlbumList extends BaseAdapter {

    Context con;
    String[] albumliste;
    LayoutInflater inflater;
    private boolean[] selected;

    public AlbumList(Context con, String[] albumliste) {
        this.con = con;
        this.albumliste = albumliste;
        inflater = LayoutInflater.from(con);
        selected = new boolean[albumliste.length];
    }

    public void putSelected(int pos, boolean selec) {
        selected[pos] = selec;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return albumliste.length;
    }

    @Override
    public Object getItem(int item) {
        return null;
    }

    @Override
    public long getItemId(int item) {
        return 0;
    }

    @Override
    public View getView(int item, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.albumlist, null);
        }
        TextView txt = (TextView) view.findViewById(R.id.txtview);
        txt.setText(albumliste[item]);

        if (selected[item]) {
            int blue = ContextCompat.getColor(con, R.color.mainBlue);
            view.setBackgroundColor(blue);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }
}

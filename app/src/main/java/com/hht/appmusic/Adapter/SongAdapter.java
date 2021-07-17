package com.hht.appmusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hht.appmusic.Model.Song;
import com.hht.appmusic.R;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<Song> songs;

    public SongAdapter(Context context, int layout, ArrayList<Song> songs) {
        this.context = context;
        this.layout = layout;
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        ViewHolder holder = new ViewHolder();

        if(rowView==null){
            rowView = inflater.inflate(layout,null);
            holder.SongImg = rowView.findViewById(R.id.imgSong);
            holder.Name = rowView.findViewById(R.id.twNameMusic);
            holder.Name.setSelected(true);
            holder.Artist = rowView.findViewById(R.id.twArtist);
            rowView.setTag(holder);
        }else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.Name.setText(songs.get(position).getTitle());
        holder.SongImg.setImageBitmap(songs.get(position).getImg());
        holder.Artist.setText(songs.get(position).getArtist());
        return rowView;

    }

    public void setCurrentPlaying(){

    }

    public class ViewHolder {
        TextView Name,Artist;
        ImageView SongImg;
    }
}

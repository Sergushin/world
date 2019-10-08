package com.example.sergushin.world;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Sergushin on 1/11/2018.
 */

public class CustomAdapter extends ArrayAdapter<Country> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Country> data = null;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    public CustomAdapter(Context context, int layoutResourceId, ArrayList<Country> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CustomHolder holder = new CustomHolder();

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder.imgFlag = row.findViewById(R.id.countryFlag);
            holder.textName = row.findViewById(R.id.countryName);
            holder.textCode = row.findViewById(R.id.callCode);

            row.setTag(holder);
        } else {
            holder = (CustomHolder) row.getTag();
        }
        Country country = data.get(position);

        holder.textName.setText(country.getCountryName());
        holder.textCode.setText(country.getCallCode());

        requestBuilder = Glide.with(row.getContext())
                .using(Glide.buildStreamModelLoader(Uri.class, row.getContext()), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());

        Uri uri = Uri.parse(country.getCountryFlag());
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(holder.imgFlag);

        return row;
    }

    static class CustomHolder {
        ImageView imgFlag;
        TextView textName;
        TextView textCode;
    }
}


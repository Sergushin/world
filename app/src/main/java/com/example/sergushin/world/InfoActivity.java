package com.example.sergushin.world;

import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import stanford.androidlib.*;

public class InfoActivity extends SimpleActivity {
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setTitle("Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String countryFinance = intent.getStringExtra("countryFinance");
        final String countryInfo = intent.getStringExtra("countryInfo");
        //$TV(R.id.countryInfo).setText(countryInfo);

        Ion.with(getApplicationContext())
                .load("https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            String all = null;
                            JSONObject geoJsonData = new JSONObject(result);
                            for(int i = 0; i < geoJsonData.getJSONObject("list").getJSONArray( "resources").length(); i++) {
                                if (geoJsonData.getJSONObject("list").getJSONArray("resources")
                                        .getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("name").length() == 7) {
                                    String rate = geoJsonData.getJSONObject("list").getJSONArray("resources")
                                            .getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("name").substring(4, geoJsonData.getJSONObject("list").getJSONArray("resources")
                                                    .getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("name").length());

                                    if (countryFinance.equalsIgnoreCase(rate)) {
                                        all = countryInfo + "\n\n" + "Rate (Date: " + geoJsonData.getJSONObject("list").getJSONArray("resources")
                                                .getJSONObject(i).getJSONObject("resource").getJSONObject("fields")
                                                .getString("utctime").substring(0, 10) +
                                                ") " + geoJsonData.getJSONObject("list").getJSONArray("resources")
                                                .getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("name") + " price is " +
                                                geoJsonData.getJSONObject("list").getJSONArray("resources")
                                                        .getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("price");
                                    }
                                }
                                if(all == null){
                                    $TV(R.id.countryInfo).setText(countryInfo);
                                }else{
                                    $TV(R.id.countryInfo).setText(all);
                                }
                            }
                        } catch (JSONException jsone) {
                            Toast.makeText(getApplicationContext(), jsone.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        String imgFlag = intent.getStringExtra("countryFlag");

        requestBuilder = Glide.with(this)
                .using(Glide.buildStreamModelLoader(Uri.class, this), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());

        Uri uri = Uri.parse(imgFlag);
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into($IV(R.id.countryFlagInfo));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


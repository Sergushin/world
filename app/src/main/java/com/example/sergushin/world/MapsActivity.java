package com.example.sergushin.world;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap Map;
    private String jsonProp;
    private GeoJsonLayer layer;
    private ArrayList<Country> countries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        countries = (ArrayList<Country>) getIntent().getSerializableExtra(Country.class.getCanonicalName());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;
        //"http://freegeoip.net/json"
        Ion.with(getApplicationContext())
                .load("http://ip-api.com/json/?callback=")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject geoJsonData = new JSONObject(result);
                            double lat = geoJsonData.getDouble("lat");
                            double lng = geoJsonData.getDouble("lon");

                            String country = geoJsonData.getString("country");
                            for(int i = 0; i < countries.size(); i++){
                                if(countries.get(i).getCountryName().equalsIgnoreCase(country)) {
                                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                                    intent.putExtra("countryInfo", countries.get(i).getCountryInfo());
                                    intent.putExtra("countryFlag", countries.get(i).getCountryFlag());
                                    intent.putExtra("countryFinance", countries.get(i).getCountryFinance());
                                    startActivity(intent);
                                }
                            }

                            LatLng yourLocation = new LatLng(lat, lng);
                            Map.addMarker(new MarkerOptions().position(yourLocation).title("You're here!"));
                            Map.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                        } catch (JSONException json) {
                            Toast.makeText(getApplicationContext(), "Fail coordinates", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                for(int i = 0; i < countries.size(); i++) {
                    if(getAddress(latLng.latitude, latLng.longitude) == null) break;
                    if (countries.get(i).getCountryName().contains(getAddress(latLng.latitude, latLng.longitude)) ||
                            countries.get(i).getCountryName().equalsIgnoreCase(getAddress(latLng.latitude, latLng.longitude)) ) {
                        Ion.with(getApplicationContext())
                                .load("https://raw.githubusercontent.com/mledoze/countries/master/data/" +
                                        countries.get(i).getCountryCioc() + ".geo.json")
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        try {
                                            Map.clear();
                                            JSONObject geoJsonData = new JSONObject(result);
                                            jsonProp = geoJsonData.getJSONArray("features").
                                                    getJSONObject(0).getJSONObject("properties").
                                                    getString("cca2").toLowerCase().toString();
                                            layer = new GeoJsonLayer(Map, geoJsonData);
                                            for (GeoJsonFeature feature : layer.getFeatures()) {
                                                feature.getPolygonStyle().setFillColor(Color.parseColor("#4D9932CC"));
                                                feature.getPolygonStyle().setStrokeColor(Color.parseColor("#809932CC"));
                                                feature.getPolygonStyle().setStrokeWidth(3);
                                            }
                                            layer.addLayerToMap();
                                        } catch (JSONException jsone) {
                                            Toast.makeText(getApplicationContext(), "Fail coordinates", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;
                    }
                }
            }
        });

        Map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                for (int i = 0; i < countries.size(); i++) {
                    if (countries.get(i).getCountryProp().equalsIgnoreCase(jsonProp)) {
                        Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                        intent.putExtra("countryInfo", countries.get(i).getCountryInfo());
                        intent.putExtra("countryFlag", countries.get(i).getCountryFlag());
                        intent.putExtra("countryFinance", countries.get(i).getCountryFinance());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String add = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses.size() != 0) {
                add = addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "No address!", Toast.LENGTH_SHORT).show();
        }
        return add;
    }
}
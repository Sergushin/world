package com.example.sergushin.world;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.service.voice.VoiceInteractionSession;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.request.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import stanford.androidlib.*;

import static com.bumptech.glide.request.Request.*;

public class MainActivity extends SimpleActivity {
    private ListView listView;
    private CustomAdapter adapter;
    private ArrayList<Country> countriesList = new ArrayList<>();
    private ArrayList<Country> searchingResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(null);

        listView = $LV(R.id.countriesList);

        createList();

        /*DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.push().child("countryInfo").setValue("Voll");*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Intent intent = new Intent(parent.getContext(), InfoActivity.class);
                intent.putExtra("countryInfo", countriesList.get(position).getCountryInfo());
                intent.putExtra("countryFlag", countriesList.get(position).getCountryFlag());
                intent.putExtra("countryFinance", countriesList.get(position).getCountryFinance());
                startActivity(intent);
            }
        });

        $ET(R.id.searchBar).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = $ET(R.id.searchBar).getText().toString().toLowerCase();
                searchingResults.clear();

                for (int i = 0; i < countriesList.size(); i++) {
                    String countryName = countriesList.get(i).getCountryName().toString().toLowerCase();
                    String callCode = countriesList.get(i).getCallCode().toString().toLowerCase();

                    if (countryName.toLowerCase().contains(searchString) || callCode.toLowerCase().contains(searchString))
                        searchingResults.add(countriesList.get(i));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                        Intent intent = new Intent(parent.getContext(), InfoActivity.class);
                        if ($ET(R.id.searchBar).getText().toString().equals("")) {
                            intent.putExtra("countryInfo", countriesList.get(position).getCountryInfo());
                            intent.putExtra("countryFlag", countriesList.get(position).getCountryFlag());
                            intent.putExtra("countryFinance", countriesList.get(position).getCountryFinance());
                        } else {
                            intent.putExtra("countryInfo", searchingResults.get(position).getCountryInfo());
                            intent.putExtra("countryFlag", searchingResults.get(position).getCountryFlag());
                            intent.putExtra("countryFinance", searchingResults.get(position).getCountryFinance());
                        }
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter = new CustomAdapter(getApplicationContext(), R.layout.country_string, searchingResults);
                listView.setAdapter(adapter);
            }
        });
        //https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void mapClick(MenuItem item) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(Country.class.getCanonicalName(), countriesList);
        startActivity(intent);
    }

    public void createList() {
        Ion.with(this)
                .load("https://restcountries.eu/rest/v2/all")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                                JSONArray arr = new JSONArray(result);

                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject json = arr.getJSONObject(i);
                                    String flag = json.getString("flag");
                                    String name = json.getString("name");
                                    String code;
                                    if (json.getJSONArray("callingCodes").get(0).hashCode() == 0) {
                                        code = "No Code";
                                    } else {
                                        code = "+" + json.getJSONArray("callingCodes").get(0);
                                    }
                                    String info = name + "\nCapital: " + json.getString("capital") +
                                            "\nRegion: " + json.getString("region") +
                                            "\nSubbegion: " + json.getString("subregion") +
                                            "\nPopulation: " + json.getString("population") +
                                            "\nTimezones: " + json.getJSONArray("timezones").getString(0) +
                                            "\nNumeric Code: " + json.getString("numericCode") +
                                            "\nCalling Code: " + code +
                                            "\nCioc: " + json.getString("cioc") +
                                            "\nLanguage: " + json.getJSONArray("languages").getJSONObject(0).getString("name") +
                                            "\nCurrencies: " + json.getJSONArray("currencies").getJSONObject(0).getString("code")
                                            + " (" + json.getJSONArray("currencies").getJSONObject(0).getString("name") +
                                            ", " + json.getJSONArray("currencies").getJSONObject(0).getString("symbol") + ")";
                                    String prop = json.getString("alpha2Code").toLowerCase();
                                    String cioc = flag.substring(flag.length() - 7, flag.length() - 4);
                                    String finance = json.getJSONArray("currencies").getJSONObject(0).getString("code");
                                    log(flag);
                                    log(name);
                                    log(code);
                                    log(info);
                                    log(prop);
                                    log(cioc);
                                    log(finance);

                                    countriesList.add(new Country(name, flag, code, prop, info, cioc, finance));
                                    adapter = new CustomAdapter(getApplicationContext(), R.layout.country_string, countriesList);
                                    listView.setAdapter(adapter);
                                }
                        } catch (JSONException el) {
                            toast(el);
                        }
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelableArrayList("countries", countriesList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        countriesList = savedInstanceState.getParcelableArrayList("countries");
    }
}
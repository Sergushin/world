package com.example.sergushin.world;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sergushin on 1/9/2018.
 */

public class Country implements Parcelable {
    private String countryName;
    private String countryFlag;
    private String callCode;
    private String countryInfo;
    private String countryProp;
    private String countryCioc;
    private String countryFinance;

    public Country() {
    }

    public Country(String countryName, String countryFlag, String callCode, String countryProp,
                   String countryInfo, String countryCioc, String countryFinance) {
        this.callCode = callCode;
        this.countryFlag = countryFlag;
        this.countryInfo = countryInfo;
        this.countryName = countryName;
        this.countryProp = countryProp;
        this.countryCioc = countryCioc;
        this.countryFinance = countryFinance;
    }

    protected Country(Parcel in) {
        countryName = in.readString();
        countryFlag = in.readString();
        callCode = in.readString();
        countryInfo = in.readString();
        countryProp = in.readString();
        countryCioc = in.readString();
        countryFinance = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(countryName);
        dest.writeString(countryFlag);
        dest.writeString(callCode);
        dest.writeString(countryInfo);
        dest.writeString(countryProp);
        dest.writeString(countryCioc);
        dest.writeString(countryFinance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public void setCountryInfo(String countryInfo) {
        this.countryInfo = countryInfo;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCountryProp(String countryProp) {
        this.countryProp = countryProp;
    }

    public void setCountryCioc(String countryCioc) {
        this.countryCioc = countryCioc;
    }

    public void setCountryFinance(String countryFinance) {
        this.countryFinance = countryFinance;
    }

    public String getCallCode() {
        return callCode;
    }

    public String getCountryFlag() {
        return countryFlag;
    }

    public String getCountryInfo() {
        return countryInfo;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryProp() {
        return countryProp;
    }

    public String getCountryCioc() {
        return countryCioc;
    }

    public String getCountryFinance() {
        return countryFinance;
    }
}


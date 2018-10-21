package co.cgclab.gpstracker.main.controllers;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class Markers implements ClusterItem {
    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private BitmapDescriptor mIcon;

    public Markers(MarkerOptions markerOptions) {
        this.mPosition = markerOptions.getPosition();
        this.mTitle = markerOptions.getTitle();
        this.mSnippet = markerOptions.getSnippet();
        this.mIcon = markerOptions.getIcon();
    }

    public Markers(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public Markers(double lat, double lng, String title, String snippet, BitmapDescriptor icon) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mIcon = icon;
    }

    public BitmapDescriptor getIcon() {
        return mIcon;
    }

    public void setIcon(BitmapDescriptor mIcon) {
        this.mIcon = mIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSnippet() {
        return mSnippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
